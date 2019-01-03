package fr.coppernic.lib.utils.time;

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import fr.coppernic.lib.utils.net.SntpClient;

/**
 * {@link TrustedTime} that connects with a remote NTP server as its trusted
 * time source.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public final class NtpTrustedTime implements TrustedTime {
    /**
     * "pool.ntp.org"
     */
    public static final String DEFAULT_SERVER = "pool.ntp.org";
    /**
     * 20000
     */
    public static final long DEFAULT_TIMEOUT = 20000;
    private static final String TAG = "NtpTrustedTime";
    private static final boolean LOGD = true;
    // 24 hrs
    private static final long POLLING_INTERVAL_MS = 24L * 60 * 60 * 1000;
    /**
     * If the time difference is greater than this threshold, then update the
     * time.
     */
    private static final int TIME_ERROR_THRESHOLD_MS = 5 * 1000;
    private static NtpTrustedTime sSingleton;
    private boolean mHasCache = false;
    private long mCachedNtpTime;
    private long mCachedNtpElapsedRealtime;
    private long mCachedNtpCertainty;
    private long mPollingInterval = POLLING_INTERVAL_MS;
    private int mTimeErrorThreshold = TIME_ERROR_THRESHOLD_MS;
    private String mServer;
    private long mTimeout;

    private NtpTrustedTime(String server, long timeout) {
        if (LOGD) {
            Log.d(TAG, "creating NtpTrustedTime using " + server
                       + ", timeout : " + timeout);
        }
        mServer = server;
        mTimeout = timeout;
    }

    public static synchronized NtpTrustedTime getInstance(String server,
                                                          long timeout) {
        if (sSingleton == null) {
            sSingleton = new NtpTrustedTime(server, timeout);
        }
        return sSingleton;
    }

    /**
     * Give a NtpTrustedTime instance or create one if it does not exists.
     * <p>
     * It looks in Settings.Secure or Settings.Global table to get NTP server
     * address and timeout. Table keys are :
     * <ul>
     * <li>ntp_server
     * <li>ntp_timeout
     * </ul>
     *
     * @param context
     * @return NtpTrustedTime instance
     */
    public static synchronized NtpTrustedTime getInstance(Context context) {
        if (sSingleton == null) {
            final ContentResolver resolver = context.getContentResolver();

            String secureServer = "";
            long timeout = 0;

            secureServer = Settings.Global.getString(resolver, "ntp_server");
            timeout = Settings.Global.getLong(resolver, "ntp_timeout", DEFAULT_TIMEOUT);

            final String server = secureServer != null ? secureServer : DEFAULT_SERVER;
            sSingleton = new NtpTrustedTime(server, timeout);
        }

        return sSingleton;
    }

    /**
     * Set server address.
     * <p>
     * Example : pool.ntp.org
     *
     * @param server
     */
    public void setServer(String server) {
        mServer = server;
    }

    /**
     * Set timeout to communicate with the server
     *
     * @param timeout
     */
    public void setTimeout(long timeout) {
        mTimeout = timeout;
    }

    /**
     * Set server polling interval
     *
     * @param interval
     */
    public void setPollingInterval(long interval) {
        mPollingInterval = interval;
    }

    /**
     * Set threshold under which time will not be updated
     *
     * @param threshold
     */
    public void setTimeErrorThreshold(int threshold) {
        mTimeErrorThreshold = threshold;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean forceRefresh() {
        if (mServer == null) {
            Log.e(TAG, "missing server, so no trusted time available");
            return false;
        }

        if (LOGD) {
            Log.d(TAG, "forceRefresh() from cache miss");
        }
        final SntpClient client = new SntpClient();
        if (client.requestTime(mServer, (int) mTimeout)) {
            mHasCache = true;
            mCachedNtpTime = client.getNtpTime();
            mCachedNtpElapsedRealtime = client.getNtpTimeReference();
            mCachedNtpCertainty = client.getRoundTripTime() / 2;

            if (LOGD) {
                Log.d(TAG, "Got ntptime : " + mCachedNtpTime + ", elapsed : "
                           + mCachedNtpElapsedRealtime + ", Certainty : "
                           + mCachedNtpCertainty);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasCache() {
        return mHasCache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCacheAge() {
        if (mHasCache) {
            return SystemClock.elapsedRealtime() - mCachedNtpElapsedRealtime;
        } else {
            return Long.MAX_VALUE;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCacheCertainty() {
        if (mHasCache) {
            return mCachedNtpCertainty;
        } else {
            return Long.MAX_VALUE;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long currentTimeMillis() {
        if (!mHasCache) {
            throw new IllegalStateException("Missing authoritative time source");
        }
        if (LOGD) {
            Log.d(TAG, "currentTimeMillis() cache hit");
        }

        // current time is age after the last ntp cache; callers who
        // want fresh values will hit makeAuthoritative() first.
        return mCachedNtpTime + getCacheAge();
    }

    /**
     * Get time in milliseconds given by specified NTP server.
     * <p>
     * This methods handle a time cache system that is customizable by this
     * class's setters.
     * <p>
     * It is easy to set current time like the following :
     * <p>
     * <code>
     * final AlarmManager am = (AlarmManager) mContext
     * .getSystemService(Context.ALARM_SERVICE);
     * am.setTime(NtpTrustedTime.getInstance("fr.pool.ntp.org",
     * 20000).getNtpCurrentTimeMilli());
     * </code>
     *
     * @return Time in milliseconds, or <code>-1</code> if error.
     */
    public long getNtpCurrentTimeMilli() {
        Log.d(TAG, "applyTimeConfig");
        long currentTime = System.currentTimeMillis();

        if (LOGD) {
            Log.d(TAG, "System time = " + currentTime);
        }

        // force refresh NTP cache when outdated
        if (getCacheAge() >= mPollingInterval) {
            if (LOGD) {
                Log.d(TAG, "Cache age : " + getCacheAge());
            }
            if (!forceRefresh()) {
                Log.w(TAG, "Force refresh failed");
                // FIXME
            } else {
                if (LOGD) {
                    Log.d(TAG, "Force refresh OK");
                }
            }
        }

        // only update when NTP time is fresh
        if (getCacheAge() < mPollingInterval) {
            final long ntp = currentTimeMillis();
            if (Math.abs(ntp - currentTime) > mTimeErrorThreshold) {
                // Set the system time
                if (LOGD) {
                    Log.d(TAG, "Ntp time to be set = " + ntp);
                }
                // Make sure we don't overflow, since it's going to be converted
                // to an int
                if (ntp / 1000 < Integer.MAX_VALUE) {
                    currentTime = ntp;
                } else {
                    Log.w(TAG, "Ntp/1000 > Integer.MAX_VALUE : " + ntp / 1000);
                }
            } else {
                if (LOGD) {
                    Log.d(TAG, "Ntp time is close enough = " + ntp);
                }
            }
        } else {
            Log.w(TAG, "NTP Update is not done");
            currentTime = -1;
        }

        if (LOGD) {
            Log.d(TAG, "System time = " + currentTime);
        }
        return currentTime;
    }
}

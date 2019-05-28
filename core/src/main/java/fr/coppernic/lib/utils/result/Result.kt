package fr.coppernic.lib.utils.result

import android.os.Parcel
import android.os.Parcelable

enum class RESULT {
    /**
     * No error
     */
    OK,
    /**
     * General error
     */
    ERROR,
    /**
     * Method called is not implemented
     */
    NOT_IMPLEMENTED,
    /**
     * Method called is not supported
     */
    NOT_SUPPORTED,
    /**
     * Supplied parameters are invalid
     */
    INVALID_PARAM,
    /**
     * Current process is busy
     */
    BUSY,
    /**
     * Method called in a wrong state
     */
    WRONG_STATE,
    /**
     * Method called when not ready
     */
    NOT_READY,
    /**
     * Invalid procedure
     *
     *
     * A previous method or procedure should be called before calling this
     * one
     */
    INVALID_PROCEDURE,
    /**
     * Opening of a file has failed
     */
    OPEN_FAIL,
    /**
     * File not found
     */
    FILE_NOT_FOUND,
    /**
     * Wrong format
     */
    WRONG_FORMAT,
    /**
     * Process was interrupted
     */
    INTERRUPTED,
    /**
     * Io problems
     */
    IO,
    /**
     * Error during reset
     */
    RESET_FAIL,
    /**
     * Not authorized to open resource
     */
    OPEN_SECURITY_EXCEPTION,
    /**
     * Time out
     */
    TIMEOUT,
    /**
     * Port is not opened
     */
    NOT_OPENED,
    /**
     * Result should be ignored
     */
    IGNORE,
    /**
     * Value already set
     */
    ALREADY_SET,
    /**
     * No data available
     */
    NO_CARD,
    /**
     * Reader not connected to a card
     */
    NOT_CONNECTED_TO_A_CARD,
    /**
     * Operation has been cancelled
     */
    CANCELLED,
    /**
     * PC/SC context is invalid
     */
    INVALID_CONTEXT,
    /**
     * No PC/SC reader is available
     */
    NO_READER_AVAILABLE,
    /**
     * Invalid handle
     */
    INVALID_HANDLE,
    /**
     * Invalid command
     */
    INVALID_COMMAND,
    /**
     * Invalid version
     */
    INVALID_VERSION,
    /**
     * No reader found
     */
    NO_READER_FOUND,
    /**
     * Not found that one's was looking for
     */
    NOT_FOUND,
    /**
     * No data
     */
    NO_DATA,
    /**
     * Error in parsing
     */
    PARSE_ERROR,
    /**
     * Security error
     */
    SECURITY_ERROR,
    /**
     * Service not found
     */
    SERVICE_NOT_FOUND,
    /**
     * Not initialized
     */
    NOT_INITIALIZED,
    /**
     * NOt connected
     */
    NOT_CONNECTED,
    /**
     * All command has been aborted, reader is ready to receive new command
     */
    READER_ABORTED,
    /**
     * No command has been found to abort
     */
    READER_NOT_ABORTED,
    /**
     * Fail to abort Reader
     */
    READER_ABORT_FAIL,
    /**
     * Fail to set USB Endpoints
     */
    USB_FAIL_TO_SET_ENDPOINTS,
    /**
     * Already opened
     */
    ALREADY_OPENED,
    /**
     * OUt of range
     */
    OUT_OF_RANGE,
    /**
     * Usb not authenticated
     */
    USB_NOT_AUTHENTICATED,
    /**
     * Operation is in progress
     */
    OPP_IN_PROGRESS,
    /**
     * HTTP Error
     */
    HTTP_ERROR,
    /**
     * WEB Service Error
     */
    WEB_SERVICE_ERROR,
    /**
     * Pending
     */
    PENDING,
    /**
     * Bytes are available
     */
    BYTES_AVAILABLE,
    /**
     * Wrong length
     */
    INVALID_LENGTH,
    /**
     * Forbidden
     */
    FORBIDDEN,
    /**
     * Not authorized
     */
    NOT_AUTHORIZED,
    /**
     * No Devices
     */
    NO_DEVICE,
    /**
     * No Connection
     */
    NO_CONN,
    /**
     * CRC Error
     */
    CRC_ERROR,
    /**
     * MAC Error
     */
    MAC_ERROR,
    /**
     * IMEI Error
     */
    IMEI_ERROR,
    /**
     * Requested algorithm could
     * not be found
     */
    NO_SUCH_ALGORITHM,
    /**
     * Invalid Key
     */
    INVALID_KEY,
    /**
     * Invalid Signature
     */
    INVALID_SIGNATURE,
    /**
     * Invalid certificate
     */
    INVALID_CERTIFICATE,
    /**
     * Certificate chain validation failed
     */
    CERTIFICATES_VALIDATION_FAILED,
    /**
     * Data hashes comparison failed
     */
    DATA_HASHES_FAILED,
    /**
     * Wrong address
     */
    WRONG_ADDRESS,
    /**
     * Wrong answer
     */
    WRONG_ANSWER,
    /**
     * Fail to parse answer
     */
    ERROR_PARSING_ANSWER,
    /**
     * Connection error
     */
    CONNECTION_ERROR,
    /**
     * Hashes Comparison failed
     */
    HASHES_COMPARISON_FAILED,
    /**
     * Encryption failed
     */
    ENCRYPTION_FAILED,
    /**
     * SAM selection failed
     */
    SAM_SELECT_FAILED,
    /**
     * SAM power failed
     */
    SAM_POWER_FAILED,
    /**
     * No operation
     */
    NO_OP,
    /**
     * Result Unknown
     */
    UNKNOWN
    ;

    fun toException(): ResultException {
        return ResultException(toResult())
    }

    fun toResult(): Result {
        return Result(this)
    }

    companion object {
        fun fromErrno(errno: Int): RESULT {
            return when (errno) {
                0 -> RESULT.OK
                Errno.EPERM -> RESULT.FORBIDDEN
                Errno.ENOENT -> RESULT.FILE_NOT_FOUND
                Errno.EIO -> RESULT.IO
                Errno.ENXIO -> RESULT.NOT_FOUND
                Errno.E2BIG -> RESULT.INVALID_PARAM
                Errno.EACCES -> RESULT.FORBIDDEN
                Errno.EBUSY -> RESULT.BUSY
                Errno.ENODEV -> RESULT.NOT_FOUND
                Errno.EINVAL -> RESULT.INVALID_PARAM
                else -> RESULT.ERROR
            }
        }

        fun fromOrdinal(i: Int): RESULT {
            return RESULT.values().find { it.ordinal == i } ?: RESULT.UNKNOWN
        }
    }
}

class ResultParcelable(val result: RESULT) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readSerializable() as RESULT)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(result)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResultParcelable> {
        override fun createFromParcel(parcel: Parcel): ResultParcelable {
            return ResultParcelable(parcel)
        }

        override fun newArray(size: Int): Array<ResultParcelable?> {
            return arrayOfNulls(size)
        }
    }
}

class ResultException : Exception {
    val result: Result

    constructor(result: Result) : super() {
        this.result = result
    }

    constructor(result: Result, message: String?) : super(message) {
        this.result = result
    }

    constructor(result: Result, message: String?, cause: Throwable?) : super(message, cause) {
        this.result = result
    }

    constructor(result: Result, cause: Throwable?) : super(cause) {
        this.result = result
    }

    constructor(result: Result, message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean)
            : super(message, cause, enableSuppression, writableStackTrace) {
        this.result = result
    }

    override fun toString(): String {
        return "${super.toString()}, $result"
    }
}

data class Result(val result: RESULT, var message: String = "", var cause: Throwable? = null) {
    fun toException(): ResultException {
        return when {
            message.isEmpty() && cause == null -> ResultException(this)
            message.isEmpty() && cause != null -> ResultException(this, cause)
            message.isNotEmpty() && cause == null -> ResultException(this, message)
            else -> ResultException(this, message, cause)
        }
    }

    fun withMessage(message: String): Result {
        this.message = message
        return this
    }

    fun withCause(cause: Throwable): Result {
        this.cause = cause
        return this
    }
}

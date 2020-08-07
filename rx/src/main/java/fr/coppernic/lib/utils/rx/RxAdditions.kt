package fr.coppernic.lib.utils.rx

import fr.coppernic.lib.utils.rx.log.LogDefines
import fr.coppernic.lib.utils.rx.log.LogDefines.LOG
import io.reactivex.CompletableEmitter
import io.reactivex.FlowableEmitter
import io.reactivex.ObservableEmitter
import io.reactivex.SingleEmitter

fun CompletableEmitter?.complete() {
    if (this != null && !isDisposed) {
        onComplete()
    } else {
        if (LogDefines.verbose) {
            LOG.warn("Emitter is disposed, cannot do onSuccess")
        }
    }
}

fun CompletableEmitter?.error(obj: Throwable) {
    if (this != null && !isDisposed) {
        onError(obj)
    } else {
        if (LogDefines.verbose) {
            LOG.warn("Emitter is disposed, cannot do onError $obj")
        }
    }
}

fun <T> SingleEmitter<T>?.success(obj: T) {
    if (this != null && !isDisposed) {
        onSuccess(obj)
    } else {
        if (LogDefines.verbose) {
            LOG.warn("Emitter is disposed, cannot do onSuccess")
        }
    }
}

fun <T> SingleEmitter<T>?.error(obj: Throwable) {
    if (this != null && !isDisposed) {
        onError(obj)
    } else {
        if (LogDefines.verbose) {
            LOG.warn("Emitter is disposed, cannot do onError $obj")
        }
    }
}

fun <T> ObservableEmitter<T>?.next(obj: T) {
    if (this != null && !isDisposed) {
        onNext(obj)
    } else {
        if (LogDefines.verbose) {
            LOG.warn("Emitter is disposed, cannot do onSuccess")
        }
    }
}

fun <T> ObservableEmitter<T>?.complete() {
    if (this != null && !isDisposed) {
        onComplete()
    } else {
        if (LogDefines.verbose) {
            LOG.warn("Emitter is disposed, cannot do onSuccess")
        }
    }
}

fun <T> ObservableEmitter<T>?.error(obj: Throwable) {
    if (this != null && !isDisposed) {
        onError(obj)
    } else {
        if (LogDefines.verbose) {
            LOG.warn("Emitter is disposed, cannot do onError $obj")
        }
    }
}

fun <T> FlowableEmitter<T>?.next(obj: T) {
    if (this != null && !isCancelled) {
        onNext(obj)
    } else {
        if (LogDefines.verbose) {
            LOG.warn("Emitter is disposed, cannot do onSuccess")
        }
    }
}

fun <T> FlowableEmitter<T>?.complete() {
    if (this != null && !isCancelled) {
        onComplete()
    } else {
        if (LogDefines.verbose) {
            LOG.warn("Emitter is disposed, cannot do onSuccess")
        }
    }
}

fun <T> FlowableEmitter<T>?.error(obj: Throwable) {
    if (this != null && !isCancelled) {
        onError(obj)
    } else {
        if (LogDefines.verbose) {
            LOG.warn("Emitter is disposed, cannot do onError $obj")
        }
    }
}


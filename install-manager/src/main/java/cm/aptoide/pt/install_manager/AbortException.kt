package cm.aptoide.pt.install_manager

/**
 * An exception if the process was aborted by worker.
 *
 * This class covers the case when worker aborts.
 *
 * @property message - a reason for abortion.
 */
class AbortException(message: String?) : Exception(message)

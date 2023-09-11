package cm.aptoide.pt.install_manager

/**
 * An exception if the download should be deferred.
 *
 * This class covers the case when the download should be deferred.
 *
 * @property message - a reason for deferring the download.
 */
class DeferDownloadException(message: String?) : Exception(message)

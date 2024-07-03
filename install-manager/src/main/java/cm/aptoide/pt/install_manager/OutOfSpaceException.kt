package cm.aptoide.pt.install_manager

/**
 * An exception if there is not enough space for download and install.
 *
 * @property missingSpace - how much free space is missing for installation.
 * @property message - a reason for error.
 */
class OutOfSpaceException(
  val missingSpace: Long,
  message: String?,
) : Exception(message)

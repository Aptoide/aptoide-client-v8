package cm.aptoide.pt.installer.platform

/**
 * Whether the app declares the restricted permissions ENFORCE_UPDATE_OWNERSHIP and
 * UPDATE_PACKAGES_WITHOUT_USER_ACTION. Some distribution channels (e.g. Google Play)
 * reject uploads that declare them, so the installer must not call the APIs that require
 * them on those builds. Optional binding: builds that don't override it keep full behavior.
 */
interface UpdateOwnershipPermissions {
  val enabled: Boolean
}

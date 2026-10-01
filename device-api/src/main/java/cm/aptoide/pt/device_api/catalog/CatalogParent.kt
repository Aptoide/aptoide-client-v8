package cm.aptoide.pt.device_api.catalog

/**
 * The two halves of a device API catalog. Every category belongs to one of them, and each is
 * itself a category the apps can be listed by.
 */
object CatalogParent {
  const val GAMES = "games"
  const val APPS = "apps"
}

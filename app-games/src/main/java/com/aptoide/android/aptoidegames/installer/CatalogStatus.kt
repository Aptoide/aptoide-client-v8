package com.aptoide.android.aptoidegames.installer

/** What is known about an app's presence in the Google Play catalog. */
enum class CatalogStatus {
  /** Not looked up yet, or a lookup is in flight. */
  UNKNOWN,

  /** Play can install it. */
  IN_CATALOG,

  /** The catalog does not hold it, so Play cannot install it. */
  NOT_IN_CATALOG,

  /** The lookup could not tell, which is not the same as not being in the catalog. */
  FAILED,
}

package cm.aptoide.aptoideviews.filters

import org.parceler.Parcel

@Parcel(Parcel.Serialization.BEAN)
data class Filter @JvmOverloads constructor(val name: String = "", val selected: Boolean = false,
                                            val identifier: String? = null) {
  /**
   * This is an internal id uniquely used for RecyclerViews
   */
  internal var id: Int = -1
}
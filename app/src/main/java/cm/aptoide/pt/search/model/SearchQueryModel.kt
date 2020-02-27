package cm.aptoide.pt.search.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * This class represents a search query.
 * userQuery represents what the user has written, finalQuery is the actual search sent to
 * the webservice. finalQuery might be different from userQuery if a suggestion is clicked.
 */
@Parcelize
data class SearchQueryModel @JvmOverloads constructor(val userQuery: String = "",
                                                      val finalQuery: String = userQuery,
                                                      val source: Source = Source.MANUAL) :
    Parcelable

enum class Source {
  MANUAL, FROM_TRENDING, FROM_AUTOCOMPLETE, DEEPLINK
}
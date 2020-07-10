package cm.aptoide.pt.search

import androidx.recyclerview.widget.DiffUtil
import cm.aptoide.pt.search.model.SearchAppResult

data class SearchResultDiffModel(var diffResult: DiffUtil.DiffResult?,
                                 var searchResultsList: List<SearchAppResult>,
                                 var newSearchResultsList: List<SearchAppResult>)
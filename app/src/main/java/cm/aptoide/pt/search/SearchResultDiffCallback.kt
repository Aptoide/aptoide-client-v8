package cm.aptoide.pt.search

import androidx.recyclerview.widget.DiffUtil
import cm.aptoide.pt.search.model.SearchAppResult

class SearchResultDiffCallback(private val oldSearchResultList: List<SearchAppResult>?,
                               private val newSearchResultList: List<SearchAppResult>?) :
    DiffUtil.Callback() {

  override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return oldSearchResultList?.get(oldItemPosition)?.getAppId() == newSearchResultList?.get(
        newItemPosition)?.getAppId()
  }

  override fun getOldListSize(): Int {
    return oldSearchResultList?.size ?: 0
  }

  override fun getNewListSize(): Int {
    return newSearchResultList?.size ?: 0
  }

  override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return oldSearchResultList?.get(oldItemPosition) == newSearchResultList?.get(
        newItemPosition)
  }

  override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
    return newSearchResultList?.get(newItemPosition)
  }
}
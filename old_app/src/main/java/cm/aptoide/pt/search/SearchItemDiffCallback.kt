package cm.aptoide.pt.search

import androidx.recyclerview.widget.DiffUtil
import cm.aptoide.pt.search.model.SearchItem

class SearchItemDiffCallback(private val oldSearchResultList: List<SearchItem>?,
                             private val newSearchResultList: List<SearchItem>?) :
    DiffUtil.Callback() {

  override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return oldSearchResultList?.get(oldItemPosition)?.getId() == newSearchResultList?.get(
        newItemPosition)?.getId()
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
package cm.aptoide.pt.search.model

class SearchLoadingItem : SearchItem {
  override fun getType(): SearchItem.Type {
    return SearchItem.Type.LOADING
  }

  override fun getId(): Long {
    return SearchItem.Type.LOADING.ordinal.toLong()
  }
}
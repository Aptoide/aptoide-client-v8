package cm.aptoide.pt.search.model

interface SearchItem {

  fun getType(): Type

  fun getId(): Long

  enum class Type {
    LOADING, APP
  }
}
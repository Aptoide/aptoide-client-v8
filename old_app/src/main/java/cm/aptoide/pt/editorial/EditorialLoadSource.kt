package cm.aptoide.pt.editorial

sealed class EditorialLoadSource
data class CardId(val cardId: String?) : EditorialLoadSource()
data class Slug(val slug: String?) : EditorialLoadSource()


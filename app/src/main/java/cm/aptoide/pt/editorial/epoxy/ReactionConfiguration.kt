package cm.aptoide.pt.editorial.epoxy

data class ReactionConfiguration(val id: String, val groupId: String, val source: ReactionSource) {
  enum class ReactionSource(val stringName: String) {
    CURATION_CARD("curation_card"), CURATION_DETAIL("curation_detail")
  }
}



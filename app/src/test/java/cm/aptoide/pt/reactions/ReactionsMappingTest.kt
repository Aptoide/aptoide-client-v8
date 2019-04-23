package cm.aptoide.pt.reactions

import cm.aptoide.pt.reactions.data.ReactionType
import org.junit.Test

class ReactionsMappingTest {

  private val availableReactions: List<String> =
      arrayListOf("thumbs_down", "thumbs_up", "laugh", "thug", "love")

  @Test
  fun reactionTypeEnumToStringConverterTest() {
    for (reaction: ReactionType in ReactionType.values()) {
      val reactionString = ReactionMapper.mapUserReaction(reaction)
      assert(availableReactions.contains(reactionString))
    }
  }
}
package cm.aptoide.pt.reactions

import cm.aptoide.pt.editorialList.EditorialListManager
import cm.aptoide.pt.editorialList.EditorialListRepository
import cm.aptoide.pt.reactions.data.TopReaction
import cm.aptoide.pt.reactions.network.LoadReactionModel
import cm.aptoide.pt.reactions.network.ReactionsRemoteService
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.*

class UserReactionsTest {

  private val cardId: String = "1"
  private val groupId: String = "CURATION_1"
  private val reaction: String = "laugh"
  private val userId: String = "userId"
  @Mock
  private lateinit var reactionRemoveService: ReactionsRemoteService
  @Mock
  private lateinit var editoriaListRepository: EditorialListRepository
  private lateinit var reactionManager: ReactionsManager
  private lateinit var editorialListManager: EditorialListManager
  private lateinit var userReactions: HashMap<String, UserReaction>
  private lateinit var emptyReactionModel: LoadReactionModel
  private lateinit var reactedReactionModel: LoadReactionModel

  @Before
  fun init() {
    MockitoAnnotations.initMocks(this)
    userReactions = HashMap()
    reactionManager = ReactionsManager(reactionRemoveService, userReactions)
    editorialListManager = EditorialListManager(editoriaListRepository, reactionManager)
    emptyReactionModel = LoadReactionModel(10, "", "", emptyList())
    reactedReactionModel = LoadReactionModel(10, "", "", listOf(TopReaction(reaction, 3)))
  }

  @Test
  fun noReactionsTest() {
    //If the user has not reacted
    //And the user reacts
    editorialListManager.setReaction(cardId, groupId, reaction)
    //Then it should call the first reaction request
    Mockito.verify(reactionRemoveService).setReaction(cardId, groupId, reaction)
  }

  @Test
  fun reactingSecondTimeWithDifferentReactionTest() {
    //If the user has already reacted with thug
    userReactions.put(cardId + groupId, UserReaction(userId, "thug"))
    reactionManager = ReactionsManager(reactionRemoveService, userReactions)
    editorialListManager = EditorialListManager(editoriaListRepository, reactionManager)

    //And reacts with a different reaction(laugh)
    editorialListManager.setReaction(cardId, groupId, reaction)
    //Then the second reaction request should be called
    Mockito.verify(reactionRemoveService).setSecondReaction(userId, reaction)
  }

  @Test
  fun reactingSecondTimeWithSameReactionTest() {
    //If the user has already reacted with laugh
    userReactions.put(cardId + groupId, UserReaction(userId, reaction))
    reactionManager = ReactionsManager(reactionRemoveService, userReactions)
    editorialListManager = EditorialListManager(editoriaListRepository, reactionManager)

    //And reacts with the same reaction
    editorialListManager.setReaction(cardId, groupId, reaction)
    //Then it should make any request
    Mockito.verify(reactionRemoveService, Mockito.times(0)).setReaction(cardId, groupId, reaction)
    Mockito.verify(reactionRemoveService, Mockito.times(0))
        .setSecondReaction(userId, reaction)
  }

  @Test
  fun secondReactionOnSecondCardTest() {
    //If the user already reacted on one card
    userReactions.put(cardId + groupId, UserReaction(userId, reaction))
    reactionManager = ReactionsManager(reactionRemoveService, userReactions)
    editorialListManager = EditorialListManager(editoriaListRepository, reactionManager)

    //And tries to react on a different card
    editorialListManager.setReaction("2", groupId, reaction)

    //Then it should request the first reaction request for the desired card
    Mockito.verify(reactionRemoveService).setReaction("2", groupId, reaction)
    //And not call the second reaction request or the first reaction service for the other card
    Mockito.verify(reactionRemoveService, Mockito.times(0))
        .setSecondReaction("2", reaction)
    Mockito.verify(reactionRemoveService, Mockito.times(0))
        .setReaction(cardId, groupId, reaction)
    Mockito.verify(reactionRemoveService, Mockito.times(0))
        .setSecondReaction(userId, reaction)
  }
}
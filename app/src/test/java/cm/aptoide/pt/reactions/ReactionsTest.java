package cm.aptoide.pt.reactions;

import cm.aptoide.pt.reactions.data.ReactionType;
import cm.aptoide.pt.reactions.network.ReactionsRemoteService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Completable;
import rx.observers.AssertableSubscriber;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReactionsTest {

  private Reactions reactions;
  @Mock private ReactionsRemoteService reactionsService;

  @Before public void setup() {
    MockitoAnnotations.initMocks(this);
    reactions = new Reactions(reactionsService);

    when(reactionsService.setReaction(Matchers.anyString(), Matchers.any())).thenReturn(
        Completable.complete());
  }

  @Test public void react() {
    //Given an initialized Reactions interactor
    //When react is called with a specific content id and a reaction type
    AssertableSubscriber<Void> test = reactions.react("id", ReactionType.LIKE)
        .test();
    //then the reactions service should be called with the exact same id and reaction type
    verify(reactionsService).setReaction("id", ReactionType.LIKE);
    //and then the chain should complete
    test.assertCompleted();
  }
}
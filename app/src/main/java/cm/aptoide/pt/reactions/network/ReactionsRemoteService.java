package cm.aptoide.pt.reactions.network;

import cm.aptoide.pt.editorial.ReactionsResponse;
import cm.aptoide.pt.reactions.TopReaction;
import java.util.ArrayList;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

public class ReactionsRemoteService implements ReactionsService {

  private ServiceV8 service;
  private Scheduler ioScheduler;

  public ReactionsRemoteService(ServiceV8 service, Scheduler ioScheduler) {
    this.service = service;
    this.ioScheduler = ioScheduler;
  }

  @Override public Single<ReactionsResponse> setReaction(String id, String like) {
    return null;
  }

  @Override public Single<LoadReactionModel> loadReactionModel(String cardId, String groupId) {
    return service.getTopReactionsResponse(groupId, cardId)
        .observeOn(ioScheduler)
        .map(this::mapToTopReactionsList)
        .toSingle();
  }

  private LoadReactionModel mapToTopReactionsList(TopReactionsResponse topReactionsResponse) {
    List<TopReaction> topReactionList = new ArrayList<>();

    for (TopReactionsResponse.ReactionTypeResponse reaction : topReactionsResponse.getTop()) {
      topReactionList.add(new TopReaction(reaction.getType(), reaction.getTotal()));
    }
    String userReaction = "";
    if (topReactionsResponse.getMy() != null) {
      userReaction = topReactionsResponse.getMy()
          .getType();
    }
    return new LoadReactionModel(topReactionsResponse.getTotal(), userReaction, topReactionList);
  }

  public interface ServiceV8 {
    @GET("echo/8.22112018/groups/{group_id}/objects/{id}/reactions/summary") //test
      //@GET("echo/8.20181116/groups/{group_id}/objects/{id}/reactions/summary") //prod
    Observable<TopReactionsResponse> getTopReactionsResponse(@Path("group_id") String groupId,
        @Path("id") String id);
  }
}
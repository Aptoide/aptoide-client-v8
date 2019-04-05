package cm.aptoide.pt.reactions.network;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.reactions.data.TopReaction;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

import static cm.aptoide.pt.reactions.network.ReactionsResponse.ReactionResponseMapper.mapReactionResponse;

public class ReactionsRemoteService implements ReactionsService {

  private ServiceV8 service;
  private Scheduler ioScheduler;

  public ReactionsRemoteService(ServiceV8 service, Scheduler ioScheduler) {
    this.service = service;
    this.ioScheduler = ioScheduler;
  }

  @Override
  public Single<ReactionsResponse> setReaction(String cardId, String groupId, String reaction) {
    Body body = new Body(cardId, groupId, reaction);
    return service.setFirstUserReaction(body)
        .map(this::mapResponse)
        .toSingle()
        .subscribeOn(ioScheduler);
  }

  @Override public Single<LoadReactionModel> loadReactionModel(String cardId, String groupId) {
    return service.getTopReactionsResponse(groupId, cardId)
        .map(this::mapToTopReactionsList)
        .toSingle()
        .subscribeOn(ioScheduler);
  }

  @Override public Single<ReactionsResponse> setSecondReaction(String uid, String reaction) {
    Body body = new Body(reaction);
    return service.setSecondUserReaction(uid, body)
        .map(this::mapResponse)
        .toSingle()
        .subscribeOn(ioScheduler);
  }

  @Override public Single<ReactionsResponse> deleteReaction(String uid) {
    return service.deleteReaction(uid)
        .map(this::mapResponse)
        .toSingle()
        .subscribeOn(ioScheduler);
  }

  private ReactionsResponse mapResponse(Response response) {
    return new ReactionsResponse(mapReactionResponse(response));
  }

  private LoadReactionModel mapToTopReactionsList(TopReactionsResponse topReactionsResponse) {
    List<TopReaction> topReactionList = new ArrayList<>();

    for (TopReactionsResponse.ReactionTypeResponse reaction : topReactionsResponse.getTop()) {
      topReactionList.add(new TopReaction(reaction.getType(), reaction.getTotal()));
    }
    String userReaction = "";
    String userId = "";
    if (topReactionsResponse.getMy() != null) {
      userReaction = topReactionsResponse.getMy()
          .getType();
      userId = topReactionsResponse.getMy()
          .getUid();
    }
    return new LoadReactionModel(topReactionsResponse.getTotal(), userReaction, userId,
        topReactionList);
  }

  public interface ServiceV8 {
    @GET("echo/8.22112018/groups/{group_id}/objects/{id}/reactions/summary") //test
      //@GET("echo/8.20181116/groups/{group_id}/objects/{id}/reactions/summary") //prod
    Observable<TopReactionsResponse> getTopReactionsResponse(@Path("group_id") String groupId,
        @Path("id") String id);

    @POST("echo/8.22112018/reactions/") //@POST("echo/20181116/reactions
    Observable<Response<EmptyResponse>> setFirstUserReaction(@retrofit2.http.Body Body body);

    @DELETE("echo/8.22112018/reactions/{uid}/") Observable<Response<EmptyResponse>> deleteReaction(
        @Path("uid") String uid);

    @PATCH("echo/8.22112018/reactions/{uid}/")
    Observable<Response<EmptyResponse>> setSecondUserReaction(@Path("uid") String uid,
        @retrofit2.http.Body Body body);
  }

  public static class Body extends BaseBody {

    private String objectUid;
    private String groupUid;
    private String type;

    public Body(String cardId, String groupId, String reaction) {
      this.objectUid = cardId;
      this.groupUid = groupId;
      this.type = reaction;
    }

    public Body(String reaction) {
      this.type = reaction;
    }

    public String getObjectUid() {
      return objectUid;
    }

    public void setObjectUid(String objectUid) {
      this.objectUid = objectUid;
    }

    public String getGroupUid() {
      return groupUid;
    }

    public void setGroupUid(String groupUid) {
      this.groupUid = groupUid;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }
}
package cm.aptoide.pt.dataprovider.model.v7;

import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by trinkes on 16/12/2016.
 */
@EqualsAndHashCode(callSuper = false) @Data public class GetFollowers
    extends BaseV7EndlessDataListResponse<GetFollowers.TimelineUser> {

  @Data public static class TimelineUser {
    long id;
    String name;
    String avatar;
    Store store;
    TimelineStats.StatusData stats;
  }
}

//"list": {
//    "id": 2552022,
//    "name": "Aptoide Agent",
//    "avatar": "http://pool.img.aptoide.com/user/228f730fc48999593475e0ab7ce0ad6f_avatar.png",
//    "store": {
//      "id": 798468,
//      "name": "rmota",
//      "avatar": "http://pool.img.aptoide.com/rmota/a346ea94af55291088a6e2d8da2e9280_ravatar.png"
//    },
//    "stats": {
//      "followers": 92,
//      "following": 376
//    }
//}


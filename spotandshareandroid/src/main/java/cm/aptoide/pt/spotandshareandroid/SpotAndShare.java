package cm.aptoide.pt.spotandshareandroid;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import java.util.List;

/**
 * Created by neuro on 07-06-2017.
 */

public interface SpotAndShare {

  void createGroup(OnSuccess onSuccess, OnError onError, AcceptReception acceptReception);

  void isGroupCreated(GroupCreated groupCreated);

  void joinGroup(OnSuccess onSuccess, OnError onError, AcceptReception acceptReception);

  void leaveGroup(OnSuccess onSuccess, OnError onError);

  void sendApps(List<AndroidAppInfo> appsList);

  interface GroupCreated {
    void isCreated(boolean created);
  }

  interface OnSuccess {
    void onSuccess(String uuid);
  }

  interface OnError {
    void onError(Throwable throwable);
  }

  interface AcceptReception {
    boolean accept(AndroidAppInfo androidAppInfo);
  }
}

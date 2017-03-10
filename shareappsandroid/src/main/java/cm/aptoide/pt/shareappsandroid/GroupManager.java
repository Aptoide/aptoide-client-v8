package cm.aptoide.pt.shareappsandroid;

import android.os.AsyncTask;
import android.text.TextUtils;

/**
 * Created by filipegoncalves on 02-02-2017.
 */

public class GroupManager {

  private final ConnectionManager connectionManager;
  private AsyncTask<Void, Void, Integer> joinTask;
  private boolean interactingWithGroup;
      //flag to know if he is already joining a group (multiple clicks) or creating a group
  private boolean mobileDataDialog;
  private Group group;
  private GroupListener joinGrouplistener;
  private GroupListener createGrouplistener;
  private String deviceName;
  private String randomAlphaNum;
  private JoinHotspotTask joinHotspotTask;
  private ActivateHotspotTask activateHotspotTask;
  private AsyncTask<Void, Void, Integer> createTask;

  public GroupManager(ConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
    this.joinHotspotTask = new JoinHotspotTask();
    this.activateHotspotTask = new ActivateHotspotTask();
  }

  public void joinGroup(Group group, GroupListener listener) {
    if (interactingWithGroup) {
      return;
    }
    this.group = group;
    this.joinGrouplistener = listener;
    if (group == null || TextUtils.isEmpty(group.getName())) {
      joinGrouplistener.onError(ConnectionManager.ERROR_INVALID_GROUP);
      return;
    }
    //if (connectionManager.isMobileDataOn() && !mobileDataDialog) {
    //  mobileDataDialog = true;
    //  listener.onError(ConnectionManager.ERROR_MOBILE_DATA_ON_DIALOG);
    //  return;
    //}
    try {
      joinTask = joinHotspotTask.execute();
    } catch (IllegalStateException e) {
      //TODO ERROR bc highwayActivity is singletask->doesn't create new groupManager + lazzy patern (reuse task)
      joinTask = new JoinHotspotTask().execute();
    }
  }

  public void retryToJoinGroup(Group group) {//after mobileDataDialog
    if (group == null || TextUtils.isEmpty(group.getName())) {
      joinGrouplistener.onError(ConnectionManager.ERROR_INVALID_GROUP);
      return;
    }
    //if (connectionManager.isMobileDataOn()) {
    //  joiningGroup = false;
    //  if (mobileDataDialog) {
    //    listener.onError(ConnectionManager.ERROR_MOBILE_DATA_ON_TOAST);
    //    return;
    //  }
    //  mobileDataDialog = true;
    //  listener.onError(ConnectionManager.ERROR_MOBILE_DATA_ON_DIALOG);
    //  return;
    //}
    this.joinHotspotTask = new JoinHotspotTask();
    joinTask = joinHotspotTask.execute();
  }

  public void createGroup(String randomAlphaNum, String deviceName, GroupListener listener) {
    this.createGrouplistener = listener;
    this.randomAlphaNum = randomAlphaNum;
    this.deviceName = deviceName;
    createTask = activateHotspotTask.execute();
  }

  public void cancel() {
    joinTask.cancel(false);
    activateHotspotTask.cancel(false);
  }

  public void stop() {
    this.createGrouplistener = null;
    this.joinGrouplistener = null;
    this.connectionManager.stop();
    mobileDataDialog = false;
    interactingWithGroup = false;
  }

  public interface GroupListener {
    void onSuccess();

    void onError(int result);
  }

  private class JoinHotspotTask extends AsyncTask<Void, Void, Integer> {

    @Override protected Integer doInBackground(Void... params) {
      return connectionManager.joinHotspot(group.getName());//future -> pass the whole group
    }

    @Override protected void onPreExecute() {
      super.onPreExecute();
      interactingWithGroup = true;
    }

    @Override protected void onCancelled() {
      super.onCancelled();
      joinGrouplistener = null;
      interactingWithGroup = false;
    }

    protected void onPostExecute(Integer result) {
      if (joinGrouplistener != null) {
        if (result == ConnectionManager.SUCCESSFUL_JOIN) {
          joinGrouplistener.onSuccess();
        } else {
          joinGrouplistener.onError(result);
        }
      }
      interactingWithGroup = false;
    }
  }

  private class ActivateHotspotTask extends AsyncTask<Void, Void, Integer> {

    @Override protected Integer doInBackground(Void... params) {
      return connectionManager.enableHotspot(randomAlphaNum, deviceName);
    }

    @Override protected void onPreExecute() {
      super.onPreExecute();
      interactingWithGroup = true;
    }

    @Override protected void onPostExecute(Integer integer) {
      super.onPostExecute(integer);
      if (createGrouplistener != null) {
        if (integer == ConnectionManager.SUCCESS_HOTSPOT_CREATION) {
          createGrouplistener.onSuccess();
        } else {
          createGrouplistener.onError(integer);
        }
      }
      interactingWithGroup = false;
    }

    @Override protected void onCancelled() {
      super.onCancelled();
      if (createGrouplistener != null) {
        createGrouplistener = null;
      }
      interactingWithGroup = false;
    }
  }
}

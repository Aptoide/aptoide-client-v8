package cm.aptoide.pt.spotandshareandroid;

import android.os.AsyncTask;
import android.text.TextUtils;

/**
 * Created by filipegoncalves on 02-02-2017.
 */

public class GroupManager {

  private final ConnectionManager connectionManager;
  private AsyncTask<Void, Void, Integer> joinTask;
  private boolean interactingWithGroup;
  private boolean mobileDataDialog;
  private Group group;
  private JoinGroupListener joinGrouplistener;
  private CreateGroupListener createGrouplistener;
  private String deviceName;
  private JoinHotspotTask joinHotspotTask;
  private ActivateHotspotTask activateHotspotTask;
  private AsyncTask<Void, Void, Integer> createTask;

  public GroupManager(ConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
    this.joinHotspotTask = new JoinHotspotTask();
    this.activateHotspotTask = new ActivateHotspotTask();
  }

  public void joinGroup(Group group, JoinGroupListener listener) {
    if (interactingWithGroup) {
      return;
    }
    this.group = group;
    this.joinGrouplistener = listener;
    if (group == null || TextUtils.isEmpty(group.getSsid())) {
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
      joinTask = new JoinHotspotTask().execute();
    }
  }

  public void retryToJoinGroup(Group group) {//after mobileDataDialog
    if (group == null || TextUtils.isEmpty(group.getSsid())) {
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

  public void createGroup(String deviceName, CreateGroupListener listener) {
    this.createGrouplistener = listener;
    this.deviceName = deviceName;
    try {
      createTask = activateHotspotTask.execute();
    } catch (IllegalStateException e) {
      createTask = new ActivateHotspotTask().execute();
    }
  }

  public void cancelTasks() {
    if (joinTask != null) {
      joinTask.cancel(false);
      joinTask = null;
    }
    if (joinHotspotTask != null) {
      joinHotspotTask.cancel(true);
      joinHotspotTask = null;
    }
    if (createTask != null) {
      createTask.cancel(false);
      createTask = null;
    }
    if (activateHotspotTask != null) {
      activateHotspotTask.cancel(true);
      activateHotspotTask = null;
    }
  }

  public void stop() {
    //cancelTasks();
    this.createGrouplistener = null;
    this.joinGrouplistener = null;
    this.connectionManager.stop();
    mobileDataDialog = false;
    interactingWithGroup = false;
  }

  public interface JoinGroupListener {
    void onSuccess(String groupName);

    void onError(int result);
  }

  public interface CreateGroupListener {
    void onSuccess();

    void onError(int result);
  }

  private class JoinHotspotTask extends AsyncTask<Void, Void, Integer> {

    @Override protected Integer doInBackground(Void... params) {
      return connectionManager.joinHotspot(group.getSsid(), true);//future -> pass the whole group
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
          String hostDeviceName = group.getDeviceName();
          joinGrouplistener.onSuccess(hostDeviceName);
        } else {
          joinGrouplistener.onError(result);
        }
      }
      interactingWithGroup = false;
    }
  }

  private class ActivateHotspotTask extends AsyncTask<Void, Void, Integer> {

    @Override protected Integer doInBackground(Void... params) {
      return connectionManager.enableHotspot(deviceName);
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

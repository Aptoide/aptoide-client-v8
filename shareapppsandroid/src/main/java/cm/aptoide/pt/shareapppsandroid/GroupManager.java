package cm.aptoide.pt.shareapppsandroid;

import android.os.AsyncTask;
import android.text.TextUtils;

/**
 * Created by filipegoncalves on 02-02-2017.
 */

public class GroupManager {

  private final ConnectionManager connectionManager;
  private AsyncTask<Void, Void, Integer> joinTask;
  private boolean joiningGroup;
  private boolean mobileDataDialog;
  private Group group;
  private GroupListener listener;
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
    this.group = group;
    this.listener = listener;
    if (group == null || TextUtils.isEmpty(group.getName())) {
      listener.onError(ConnectionManager.ERROR_INVALID_GROUP);
      return;
    }
    if (connectionManager.isMobileDataOn()) {
      if (mobileDataDialog) {
        listener.onError(ConnectionManager.ERROR_MOBILE_DATA_ON_TOAST);
        return;
      }
      mobileDataDialog = true;
      listener.onError(ConnectionManager.ERROR_MOBILE_DATA_ON_DIALOG);
      return;
    }
    if (joiningGroup) {
      return;
    }
    try{
      joinTask = joinHotspotTask.execute();
    }catch (IllegalStateException e){
      //TODO ERROR bc highwayActivity is singletask->doesn't create new groupManager + lazzy patern (reuse task)
      joinTask=new JoinHotspotTask().execute();
    }
  }

  public void retryToJoinGroup(Group group) {//after mobileDataDialog
    if (group == null || TextUtils.isEmpty(group.getName())) {
      listener.onError(ConnectionManager.ERROR_INVALID_GROUP);
      return;
    }
    if (connectionManager.isMobileDataOn()) {
      joiningGroup = false;
      if (mobileDataDialog) {
        listener.onError(ConnectionManager.ERROR_MOBILE_DATA_ON_TOAST);
        return;
      }
      mobileDataDialog = true;
      listener.onError(ConnectionManager.ERROR_MOBILE_DATA_ON_DIALOG);
      return;
    }
    this.joinHotspotTask = new JoinHotspotTask();
    joinTask = joinHotspotTask.execute();
  }

  public void createGroup(String randomAlphaNum, String deviceName, GroupListener listener) {
    this.listener = listener;
    this.randomAlphaNum = randomAlphaNum;
    this.deviceName = deviceName;
    createTask = activateHotspotTask.execute();
  }

  public void cancel() {
    joinTask.cancel(false);
    activateHotspotTask.cancel(false);
  }

  public void stop() {
    this.listener = null;
    this.connectionManager.stop();
    mobileDataDialog = false;
    joiningGroup = false;
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
      joiningGroup = true;
    }

    @Override protected void onCancelled() {
      super.onCancelled();
      listener = null;
      joiningGroup = false;
    }

    protected void onPostExecute(Integer result) {
      joiningGroup = false;
      if (listener != null) {
        if (result == ConnectionManager.SUCCESSFUL_JOIN) {
          listener.onSuccess();
        } else {
          listener.onError(result);
        }
      }
    }
  }

  private class ActivateHotspotTask extends AsyncTask<Void, Void, Integer> {

    @Override protected Integer doInBackground(Void... params) {
      return connectionManager.enableHotspot(randomAlphaNum, deviceName);
    }

    @Override protected void onPostExecute(Integer integer) {
      super.onPostExecute(integer);
      if (listener != null) {
        if (integer == ConnectionManager.SUCCESS_HOTSPOT_CREATION) {
          listener.onSuccess();
        } else {
          listener.onError(integer);
        }
      }
    }

    @Override protected void onCancelled() {
      super.onCancelled();
      if(listener != null){
        listener = null;
      }
    }
  }
}

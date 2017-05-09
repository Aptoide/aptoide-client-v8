package cm.aptoide.pt.v8engine.presenter;

/**
 * Created by jdandrade on 13/02/2017.
 */

public interface SyncResultContract {
  interface View {

    void finishView();

    void showStore();

    void setProgressIndicator(boolean active);
  }

  interface UserActionsListener {

    void allowFindClicked();

    void doneClicked();
  }
}

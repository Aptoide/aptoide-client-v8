package cm.aptoide.pt.v8engine.presenter;

/**
 * Created by jdandrade on 14/02/2017.
 */

public interface PhoneInputContract {
  interface View {

    void finishView();

    void setGenericPleaseWaitDialog(boolean active);

    void showSubmissionError();

    void hideVirtualKeyboard();
  }

  interface UserActionsListener {

    void notNowClicked();

    void submitClicked(String phoneNumber);
  }
}

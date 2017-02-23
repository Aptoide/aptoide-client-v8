package cm.aptoide.pt.v8engine.addressbook.phoneinput;

/**
 * Created by jdandrade on 14/02/2017.
 */

public interface PhoneInputContract {
  interface View {

    void finishView();

    void showProgressIndicator(boolean active);

    void showSubmissionSuccess();

    void showSubmissionError();
  }

  interface UserActionsListener {

    void notNowClicked();

    void submitClicked(String phoneNumber);
  }
}

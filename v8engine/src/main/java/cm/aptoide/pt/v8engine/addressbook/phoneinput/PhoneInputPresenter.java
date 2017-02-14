package cm.aptoide.pt.v8engine.addressbook.phoneinput;

/**
 * Created by jdandrade on 14/02/2017.
 */
public class PhoneInputPresenter implements PhoneInputContract.UserActionsListener {
  private PhoneInputContract.View mPhoneInputView;

  public PhoneInputPresenter(PhoneInputContract.View phoneInputView) {
    this.mPhoneInputView = phoneInputView;
  }

  @Override public void notNowClicked() {
    this.mPhoneInputView.finishView();
  }

  @Override public void submitClicked() {

  }
}

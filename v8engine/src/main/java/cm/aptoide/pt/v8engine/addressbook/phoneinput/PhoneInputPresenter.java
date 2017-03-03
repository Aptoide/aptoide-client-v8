package cm.aptoide.pt.v8engine.addressbook.phoneinput;

import cm.aptoide.pt.v8engine.addressbook.AddressBookAnalytics;
import cm.aptoide.pt.v8engine.addressbook.data.ContactsRepository;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 14/02/2017.
 */
public class PhoneInputPresenter implements PhoneInputContract.UserActionsListener {
  private PhoneInputContract.View mPhoneInputView;
  private ContactsRepository mContactsRepository;
  private final AddressBookAnalytics analytics;

  public PhoneInputPresenter(PhoneInputContract.View phoneInputView,
      ContactsRepository contactsRepository, AddressBookAnalytics analytics) {
    this.mPhoneInputView = phoneInputView;
    this.mContactsRepository = contactsRepository;
    this.analytics = analytics;
  }

  @Override public void notNowClicked() {
    this.mPhoneInputView.finishView();
  }

  @Override public void submitClicked(String phoneNumber) {
    mPhoneInputView.setGenericPleaseWaitDialog(true);
    mContactsRepository.submitPhoneNumber(success -> Observable.just(success)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(success1 -> {
          if (success) {
            analytics.sendShareYourPhoneSuccessEvent();
            mPhoneInputView.showSubmissionSuccess();
            mPhoneInputView.hideVirtualKeyboard();
          } else {
            mPhoneInputView.showSubmissionError();
          }
          mPhoneInputView.setGenericPleaseWaitDialog(false);
        }, throwable -> {
          mPhoneInputView.showSubmissionError();
          mPhoneInputView.setGenericPleaseWaitDialog(false);
        }), phoneNumber);
  }
}

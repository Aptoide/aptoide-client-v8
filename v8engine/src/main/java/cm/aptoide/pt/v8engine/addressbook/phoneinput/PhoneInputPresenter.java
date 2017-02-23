package cm.aptoide.pt.v8engine.addressbook.phoneinput;

import cm.aptoide.pt.v8engine.addressbook.data.ContactsRepository;

/**
 * Created by jdandrade on 14/02/2017.
 */
public class PhoneInputPresenter implements PhoneInputContract.UserActionsListener {
  private PhoneInputContract.View mPhoneInputView;
  private ContactsRepository mContactsRepository;

  public PhoneInputPresenter(PhoneInputContract.View phoneInputView,
      ContactsRepository contactsRepository) {
    this.mPhoneInputView = phoneInputView;
    this.mContactsRepository = contactsRepository;
  }

  @Override public void notNowClicked() {
    this.mPhoneInputView.finishView();
  }

  @Override public void submitClicked(String phoneNumber) {
    mContactsRepository.submitPhoneNumber(success -> {
      if (success) {
        mPhoneInputView.showSubmissionSuccess();
      } else {
        mPhoneInputView.showSubmissionError();
      }
    }, phoneNumber);
  }
}

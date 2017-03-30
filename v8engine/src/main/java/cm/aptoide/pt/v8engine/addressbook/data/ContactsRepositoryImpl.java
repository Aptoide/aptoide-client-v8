package cm.aptoide.pt.v8engine.addressbook.data;

import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.SetConnectionRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SyncAddressBookRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.FacebookModel;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.model.v7.TwitterModel;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.addressbook.utils.ContactUtils;
import cm.aptoide.pt.v8engine.addressbook.utils.StringEncryption;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by jdandrade on 15/02/2017.
 */

public class ContactsRepositoryImpl implements ContactsRepository {

  private final BodyInterceptor bodyInterceptor;

  public ContactsRepositoryImpl(BodyInterceptor bodyInterceptor) {
    this.bodyInterceptor = bodyInterceptor;
  }

  @Override public void getContacts(@NonNull LoadContactsCallback callback1) {
    Observable.just(callback1).observeOn(Schedulers.computation()).subscribe(callback -> {
      ContactUtils contactUtils = new ContactUtils();

      ContactsModel contacts = contactUtils.getContacts(V8Engine.getContext());

      List<String> numbers = contacts.getMobileNumbers();
      List<String> emails = contacts.getEmails();

      SyncAddressBookRequest.of(numbers, emails, bodyInterceptor)
          .observe()
          .subscribe(getFollowers -> {
            List<Contact> contactList = new ArrayList<>();
            for (GetFollowers.TimelineUser user : getFollowers.getDatalist().getList()) {
              Contact contact = new Contact();
              contact.setStore(user.getStore());
              Comment.User person = new Comment.User();
              person.setAvatar(user.getAvatar());
              person.setName(user.getName());
              contact.setPerson(person);
              contactList.add(contact);
            }
            callback.onContactsLoaded(contactList, true);
          }, (throwable) -> {
            throwable.printStackTrace();
            callback.onContactsLoaded(null, false);
          });
    });
  }

  @Override public void getTwitterContacts(@NonNull TwitterModel twitterModel,
      @NonNull LoadContactsCallback callback) {
    AptoideClientUUID aptoideClientUUID =
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext());
    SyncAddressBookRequest.of(twitterModel.getId(), twitterModel.getToken(),
        twitterModel.getSecret(), bodyInterceptor).observe().subscribe(getFollowers -> {
      List<Contact> contactList = new ArrayList<>();
      for (GetFollowers.TimelineUser user : getFollowers.getDatalist().getList()) {
        Contact contact = new Contact();
        contact.setStore(user.getStore());
        Comment.User person = new Comment.User();
        person.setAvatar(user.getAvatar());
        person.setName(user.getName());
        contact.setPerson(person);
        contactList.add(contact);
      }
      callback.onContactsLoaded(contactList, true);
    }, (throwable) -> {
      throwable.printStackTrace();
      callback.onContactsLoaded(null, false);
    });
  }

  @Override public void getFacebookContacts(@NonNull FacebookModel facebookModel,
      @NonNull LoadContactsCallback callback) {
    AptoideClientUUID aptoideClientUUID =
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext());
    SyncAddressBookRequest.of(facebookModel.getId(), facebookModel.getAccessToken(),
        bodyInterceptor).observe().subscribe(getFriends -> {
      List<Contact> contactList = new ArrayList<>();
      for (GetFollowers.TimelineUser user : getFriends.getDatalist().getList()) {
        Contact contact = new Contact();
        contact.setStore(user.getStore());
        Comment.User person = new Comment.User();
        person.setAvatar(user.getAvatar());
        person.setName(user.getName());
        contact.setPerson(person);
        contactList.add(contact);
      }
      callback.onContactsLoaded(contactList, true);
    }, throwable -> {
      throwable.printStackTrace();
      callback.onContactsLoaded(null, false);
    });
  }

  @Override
  public void submitPhoneNumber(@NonNull SubmitContactCallback callback, String phoneNumber) {
    ContactUtils contactUtils = new ContactUtils();
    phoneNumber = contactUtils.normalizePhoneNumber(phoneNumber);
    if (!contactUtils.isValidNumberInE164Format(phoneNumber)) {
      callback.onPhoneNumberSubmission(false);
      return;
    }

    String hashedPhoneNumber = null;
    try {
      hashedPhoneNumber = StringEncryption.SHA256(phoneNumber);
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    if (hashedPhoneNumber != null && !hashedPhoneNumber.isEmpty()) {
      AptoideClientUUID aptoideClientUUID =
          new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
              DataProvider.getContext());
      SetConnectionRequest.of(hashedPhoneNumber, bodyInterceptor).observe().subscribe(response -> {
        if (response.isOk()) {
          callback.onPhoneNumberSubmission(true);
        } else {
          callback.onPhoneNumberSubmission(false);
        }
      }, throwable -> {
        callback.onPhoneNumberSubmission(false);
      });
    } else {
      callback.onPhoneNumberSubmission(false);
    }
  }
}

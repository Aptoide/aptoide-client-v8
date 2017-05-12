package cm.aptoide.pt.v8engine.addressbook.data;

import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.SetConnectionRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SyncAddressBookRequest;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.FacebookModel;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.model.v7.TwitterModel;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.addressbook.utils.ContactUtils;
import cm.aptoide.pt.v8engine.addressbook.utils.StringEncryption;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by jdandrade on 15/02/2017.
 */

public class ContactsRepository {

  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final IdsRepository idsRepository;
  private final ContactUtils contactUtils;

  public ContactsRepository(BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, IdsRepository idsRepository, ContactUtils contactUtils) {
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.idsRepository = idsRepository;
    this.contactUtils = contactUtils;
  }

  public void getContacts(@NonNull LoadContactsCallback callback1) {
    Observable.just(callback1)
        .observeOn(Schedulers.computation())
        .subscribe(callback -> {

          ContactsModel contacts = contactUtils.getContacts(V8Engine.getContext());

          List<String> numbers = contacts.getMobileNumbers();
          List<String> emails = contacts.getEmails();

          SyncAddressBookRequest.of(numbers, emails, bodyInterceptor, httpClient, converterFactory)
              .observe()
              .subscribe(getFollowers -> {
                List<Contact> contactList = new ArrayList<>();
                for (GetFollowers.TimelineUser user : getFollowers.getDatalist()
                    .getList()) {
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

  public void getTwitterContacts(@NonNull TwitterModel twitterModel,
      @NonNull LoadContactsCallback callback) {
    SyncAddressBookRequest.of(twitterModel.getId(), twitterModel.getToken(),
        twitterModel.getSecret(), bodyInterceptor, httpClient, converterFactory)
        .observe()
        .subscribe(getFollowers -> {
          List<Contact> contactList = new ArrayList<>();
          for (GetFollowers.TimelineUser user : getFollowers.getDatalist()
              .getList()) {
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

  public void getFacebookContacts(@NonNull FacebookModel facebookModel,
      @NonNull LoadContactsCallback callback) {
    SyncAddressBookRequest.of(facebookModel.getId(), facebookModel.getAccessToken(),
        bodyInterceptor, httpClient, converterFactory)
        .observe()
        .subscribe(getFriends -> {
          List<Contact> contactList = new ArrayList<>();
          for (GetFollowers.TimelineUser user : getFriends.getDatalist()
              .getList()) {
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

  public void submitPhoneNumber(@NonNull SubmitContactCallback callback, String phoneNumber) {
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
      SetConnectionRequest.of(hashedPhoneNumber, bodyInterceptor, httpClient, converterFactory)
          .observe()
          .subscribe(response -> {
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

  public interface LoadContactsCallback {

    void onContactsLoaded(List<Contact> contacts, boolean success);
  }

  public interface SubmitContactCallback {

    void onPhoneNumberSubmission(boolean success);
  }
}

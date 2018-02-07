package cm.aptoide.pt;

import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.annotation.WorkerThread;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AccountException;
import cm.aptoide.accountmanager.AccountFactory;
import cm.aptoide.accountmanager.AccountPersistence;
import cm.aptoide.accountmanager.AccountService;
import cm.aptoide.accountmanager.AdultContent;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.SocialLink;
import cm.aptoide.accountmanager.Store;
import cm.aptoide.pt.account.AndroidAccountProvider;
import cm.aptoide.pt.account.FacebookLoginResult;
import cm.aptoide.pt.account.FacebookSignUpAdapter;
import cm.aptoide.pt.account.GoogleSignUpAdapter;
import cm.aptoide.pt.account.LoginPreferences;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.networking.AuthenticationPersistence;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.networking.MultipartBodyInterceptor;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.view.TestType;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Named;
import okhttp3.OkHttpClient;
import rx.Completable;
import rx.Single;

public class MockApplicationModule extends ApplicationModule {

  private final AptoideApplication application;

  public MockApplicationModule(AptoideApplication application, String aptoideMd5sum) {
    super(application, aptoideMd5sum);
    this.application = application;
  }

  @Override IdsRepository provideIdsRepository(SharedPreferences defaultSharedPreferences,
      ContentResolver contentResolver) {
    return new IdsRepository(
        SecurePreferencesImplementation.getInstance(application.getApplicationContext(),
            defaultSharedPreferences), application,
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)) {
      @Override @WorkerThread public synchronized String getGoogleAdvertisingId() {
        return defaultSharedPreferences.getString("googleAdvertisingId", null);
      }
    };
  }

  @Override AptoideAccountManager provideAptoideAccountManager(AdultContent adultContent,
      StoreAccessor storeAccessor, AccountManager accountManager,
      SharedPreferences defaultSharedPreferences,
      AuthenticationPersistence authenticationPersistence,
      AndroidAccountProvider androidAccountProvider, GoogleApiClient googleApiClient,
      StoreManager storeManager, AccountService accountService, AccountFactory accountFactory,
      LoginPreferences loginPreferences) {

    FacebookSdk.sdkInitialize(application);

    Account account = new Account() {
      @Override public List<Store> getSubscribedStores() {
        return new ArrayList<>();
      }

      @Override public String getId() {
        return "1";
      }

      @Override public String getNickname() {
        return "D01";
      }

      @Override public String getAvatar() {
        return "avatar";
      }

      @Override public boolean isAdultContentEnabled() {
        return true;
      }

      @Override public Access getAccess() {
        return Access.PRIVATE;
      }

      @Override public boolean isAccessConfirmed() {
        return true;
      }

      @Override public boolean isLoggedIn() {
        if (TestType.initialization.equals(TestType.TestTypes.LOGGEDIN)
            || TestType.initialization.equals(TestType.TestTypes.LOGGEDINWITHSTORE)) {
          return true;
        } else {
          return false;
        }
      }

      @Override public String getEmail() {
        return "jose.messejana@aptoide.com";
      }

      @Override public Store getStore() {
        if (TestType.initialization.equals(TestType.TestTypes.LOGGEDINWITHSTORE)) {
          return new Store(0, "", 0, "store", "DEFAULT", "", "", true);
        } else {
          return Store.emptyStore();
        }
      }

      @Override public boolean hasStore() {
        return false;
      }

      @Override public boolean isPublicUser() {
        return false;
      }

      @Override public boolean isPublicStore() {
        return false;
      }
    };
    final AccountService accountServiceMock = new AccountService() {
      @Override public Single<Account> getAccount(String email, String password) {
        List<ErrorResponse> list = new ArrayList<>();
        ErrorResponse errorResponse = new ErrorResponse();
        if (TestType.types.equals(TestType.TestTypes.SIGNSIGNUPTESTS)) {
          return Single.just(account);
        } else if (TestType.types.equals(TestType.TestTypes.SIGNINWRONG)) {
          errorResponse.code = "invalid_grant";
          list.add(errorResponse);
          return Single.error(new AccountException(list));
        }
        return Single.just(account);
      }

      @Override public Single<Account> createAccount(String email, String metadata, String name,
          String type) {
        return Single.just(account);
      }

      @Override public Single<Account> createAccount(String email, String password) {
        List<ErrorResponse> list = new ArrayList<>();
        ErrorResponse errorResponse = new ErrorResponse();
        if (TestType.types.equals(TestType.TestTypes.SIGNSIGNUPTESTS)) {
          return Single.just(account);
        } else if (TestType.types.equals(TestType.TestTypes.USEDEMAIL)) {
          errorResponse.code = "WOP-9";
          list.add(errorResponse);
          return Single.error(new AccountException(list));
        } else if (TestType.types.equals(TestType.TestTypes.INVALIDEMAIL)) {
          errorResponse.code = "IARG_106";
          list.add(errorResponse);
          return Single.error(new AccountException(list));
        }
        return Single.just(account);
      }

      @Override public Single<Account> getAccount() {
        return Single.just(account);
      }

      @Override public Completable updateAccount(String nickname, String avatarPath) {
        return Completable.complete();
      }

      @Override public Completable updateAccount(String accessLevel) {
        return Completable.complete();
      }

      @Override public Completable updateAccountUsername(String username) {
        return Completable.complete();
      }

      @Override public Completable unsubscribeStore(String storeName, String storeUserName,
          String storePassword) {
        return Completable.complete();
      }

      @Override public Completable subscribeStore(String storeName, String storeUserName,
          String storePassword) {
        return Completable.complete();
      }

      @Override public Completable updateAccount(boolean adultContentEnabled) {
        return Completable.complete();
      }

      @Override public Completable removeAccount() {
        return Completable.complete();
      }
    };
    final AccountPersistence accountPersistence = new AccountPersistence() {
      @Override public Completable saveAccount(Account account) {
        return Completable.complete();
      }

      @Override public Single<Account> getAccount() {
        return Single.just(account);
      }

      @Override public Completable removeAccount() {
        return Completable.complete();
      }
    };

    return new AptoideAccountManager.Builder().setAccountPersistence(accountPersistence)
        .setAccountService(accountServiceMock)
        .setAdultService(adultContent)
        .registerSignUpAdapter(GoogleSignUpAdapter.TYPE,
            new GoogleSignUpAdapter(googleApiClient, loginPreferences) {
              @Override
              public Single<Account> signUp(GoogleSignInResult result, AccountService service) {
                return Single.just(account);
              }

              @Override public Completable logout() {
                return Completable.complete();
              }

              @Override public boolean isEnabled() {
                return true;
              }
            })
        .registerSignUpAdapter(FacebookSignUpAdapter.TYPE,
            new FacebookSignUpAdapter(Arrays.asList("email"), LoginManager.getInstance(),
                loginPreferences) {
              @Override
              public Single<Account> signUp(FacebookLoginResult result, AccountService service) {
                return Single.just(account);
              }

              @Override public Completable logout() {
                return Completable.complete();
              }

              @Override public boolean isEnabled() {
                return true;
              }
            })
        .setStoreManager(storeManager)
        .build();
  }

  @Override StoreManager provideStoreManager(@Named("default") OkHttpClient okHttpClient,
      @Named("multipart") MultipartBodyInterceptor multipartBodyInterceptor,
      @Named("defaultInterceptorV3")
          BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> bodyInterceptorV3,
      @Named("account-settings-pool-v7")
          BodyInterceptor<BaseBody> accountSettingsBodyInterceptorPoolV7,
      @Named("default") SharedPreferences defaultSharedPreferences,
      TokenInvalidator tokenInvalidator, RequestBodyFactory requestBodyFactory,
      ObjectMapper nonNullObjectMapper) {
    final StoreManager storeManager =
        new StoreManager(okHttpClient, WebService.getDefaultConverter(), multipartBodyInterceptor,
            bodyInterceptorV3, accountSettingsBodyInterceptorPoolV7, defaultSharedPreferences,
            tokenInvalidator, requestBodyFactory, nonNullObjectMapper) {
          @Override
          public Completable createOrUpdate(String a, String b, String c, boolean d, String e,
              boolean f, List<SocialLink> storeLinksList,
              List<cm.aptoide.pt.dataprovider.model.v7.store.Store.SocialChannelType> storeDeleteLinksList) {
            return Completable.complete();
          }
        };
    return storeManager;
  }
}

package cm.aptoide.accountmanager;

import android.accounts.AccountManager;
import android.content.Context;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class AccountManagerTests {

  @Test public void accountRefreshRequestSuccessful() {
    AptoideAccountManager accountManager = getMockedAccountManager();

    TestSubscriber<Account> testSubscriber = new TestSubscriber<>();

    Observable<Account> accountObservable = accountManager.accountStatus();
    accountObservable.subscribe(testSubscriber);

    assertThat(testSubscriber.getOnNextEvents(), hasItem(Account.empty()));
    testSubscriber.assertNoErrors();
    testSubscriber.assertValueCount(1);
  }

  private AptoideAccountManager getMockedAccountManager() {
    Context context = mock(Context.class);
    AptoidePreferencesConfiguration preferencesConfiguration =
        mock(AptoidePreferencesConfiguration.class);
    AccountManager accountManager = mock(AccountManager.class);
    AptoideClientUUID aptoideClientUUID = mock(AptoideClientUUID.class);
    LoginAvailability loginAvailability = mock(LoginAvailability.class);
    Analytics analytics = mock(Analytics.class);
    String accountType = "aptoide";
    AccountRequestFactory requestFactory = mock(AccountRequestFactory.class);
    StoreDataPersist storeDataPersist = mock(StoreDataPersist.class);

    return new AptoideAccountManager(context, preferencesConfiguration, accountManager,
        aptoideClientUUID, loginAvailability, analytics, accountType, requestFactory,
        storeDataPersist);
  }
}

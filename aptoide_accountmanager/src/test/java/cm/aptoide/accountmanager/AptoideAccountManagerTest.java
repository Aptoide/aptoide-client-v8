package cm.aptoide.accountmanager;

import cm.aptoide.pt.model.v3.OAuth;
import com.jakewharton.rxrelay.PublishRelay;
import java.net.SocketTimeoutException;
import org.junit.Before;
import org.junit.Test;
import rx.Completable;
import rx.Single;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AptoideAccountManagerTest {

  private AccountAnalytics accountAnalyticsMock;
  private CredentialsValidator credentialsValidatorMock;
  private AccountFactory accountFactoryMock;
  private AccountDataPersist dataPersistMock;
  private AccountManagerService serviceMock;
  private AptoideAccountManager accountManager;
  private PublishRelay accountRelayMock;

  @Before public void before() {
    accountAnalyticsMock = mock(AccountAnalytics.class);
    credentialsValidatorMock = mock(CredentialsValidator.class);
    accountFactoryMock = mock(AccountFactory.class);
    dataPersistMock = mock(AccountDataPersist.class);
    serviceMock = mock(AccountManagerService.class);
    accountRelayMock = mock(PublishRelay.class);
    accountManager = new AptoideAccountManager.Builder().setAccountAnalytics(accountAnalyticsMock)
        .setCredentialsValidator(credentialsValidatorMock)
        .setAccountDataPersist(dataPersistMock)
        .setAccountManagerService(serviceMock)
        .setAccountRelay(accountRelayMock)
        .build();
  }

  @Test public void shouldLogin() throws Exception {

    final OAuth oAuthMock = mock(OAuth.class);
    final Account accountMock = mock(Account.class);

    when(credentialsValidatorMock.validate(eq("marcelo.benites@aptoide.com"), eq("1234"),
        anyBoolean())).thenReturn(Completable.complete());
    when(serviceMock.login("APTOIDE", "marcelo.benites@aptoide.com", "1234", null)).thenReturn(
        Single.just(oAuthMock));

    when(oAuthMock.getAccessToken()).thenReturn("ABCD");
    when(oAuthMock.getRefreshToken()).thenReturn("EFG");

    when(serviceMock.getAccount("ABCD", "EFG", "1234", "APTOIDE", accountManager)).thenReturn(
        Single.just(accountMock));

    when(dataPersistMock.saveAccount(accountMock)).thenReturn(Completable.complete());

    final TestSubscriber testSubscriber = TestSubscriber.create();

    accountManager.login(Account.Type.APTOIDE, "marcelo.benites@aptoide.com", "1234", null)
        .subscribe(testSubscriber);

    testSubscriber.awaitTerminalEvent();
    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();

    verify(accountAnalyticsMock).login("marcelo.benites@aptoide.com");
    verify(accountRelayMock).call(accountMock);
  }

  @Test public void shouldSignUp() throws Exception {

    when(credentialsValidatorMock.validate(eq("john.lennon@aptoide.com"), eq("imagine"),
        anyBoolean())).thenReturn(Completable.complete());

    when(serviceMock.createAccount("john.lennon@aptoide.com", "imagine")).thenReturn(
        Completable.complete());

    final OAuth oAuthMock = mock(OAuth.class);
    final Account accountMock = mock(Account.class);

    when(serviceMock.login("APTOIDE", "john.lennon@aptoide.com", "imagine", null)).thenReturn(
        Single.just(oAuthMock));

    when(oAuthMock.getAccessToken()).thenReturn("ABCD");
    when(oAuthMock.getRefreshToken()).thenReturn("EFG");

    when(serviceMock.getAccount("ABCD", "EFG", "imagine", "APTOIDE", accountManager)).thenReturn(
        Single.just(accountMock));

    when(dataPersistMock.saveAccount(accountMock)).thenReturn(Completable.complete());

    final TestSubscriber testSubscriber = TestSubscriber.create();

    accountManager.signUp("john.lennon@aptoide.com", "imagine")
        .subscribe(testSubscriber);

    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();

    verify(accountAnalyticsMock).login("john.lennon@aptoide.com");
    verify(accountAnalyticsMock).signUp();
    verify(accountRelayMock).call(accountMock);
  }

  @Test public void shouldLoginOnSignUpTimeout() throws Exception {

    when(credentialsValidatorMock.validate(eq("john.lennon@aptoide.com"), eq("imagine"),
        anyBoolean())).thenReturn(Completable.complete());

    when(serviceMock.createAccount("john.lennon@aptoide.com", "imagine")).thenReturn(
        Completable.error(new SocketTimeoutException()));

    final OAuth oAuthMock = mock(OAuth.class);
    final Account accountMock = mock(Account.class);

    when(serviceMock.login("APTOIDE", "john.lennon@aptoide.com", "imagine", null)).thenReturn(
        Single.just(oAuthMock));

    when(oAuthMock.getAccessToken()).thenReturn("ABCD");
    when(oAuthMock.getRefreshToken()).thenReturn("EFG");

    when(serviceMock.getAccount("ABCD", "EFG", "imagine", "APTOIDE", accountManager)).thenReturn(
        Single.just(accountMock));

    when(dataPersistMock.saveAccount(accountMock)).thenReturn(Completable.complete());

    final TestSubscriber testSubscriber = TestSubscriber.create();

    accountManager.signUp("john.lennon@aptoide.com", "imagine")
        .subscribe(testSubscriber);

    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();

    verify(accountAnalyticsMock, never()).signUp();
  }
}

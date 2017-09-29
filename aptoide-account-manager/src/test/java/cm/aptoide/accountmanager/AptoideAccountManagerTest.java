package cm.aptoide.accountmanager;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AptoideAccountManagerTest {

  private AccountAnalytics accountAnalyticsMock;
  private CredentialsValidator credentialsValidatorMock;
  private AccountPersistence dataPersistMock;
  private AccountService serviceMock;
  private AptoideAccountManager accountManager;
  private PublishRelay accountRelayMock;

  @Before public void before() {
    accountAnalyticsMock = mock(AccountAnalytics.class);
    credentialsValidatorMock = mock(CredentialsValidator.class);
    dataPersistMock = mock(AccountPersistence.class);
    serviceMock = mock(AccountService.class);
    accountRelayMock = mock(PublishRelay.class);
    accountManager = new AptoideAccountManager.Builder().setAccountAnalytics(accountAnalyticsMock)
        .setCredentialsValidator(credentialsValidatorMock)
        .setAccountPersistence(dataPersistMock)
        .setAccountService(serviceMock)
        .setAccountRelay(accountRelayMock)
        .build();
  }

  @Test public void shouldLogin() throws Exception {

    final Account accountMock = mock(Account.class);

    final AptoideCredentials credentials =
        new AptoideCredentials("marcelo.benites@aptoide.com", "1234");

    when(credentialsValidatorMock.validate(eq(credentials), anyBoolean())).thenReturn(
        Completable.complete());

    when(serviceMock.getAccount("marcelo.benites@aptoide.com", "1234")).thenReturn(
        Single.just(accountMock));

    when(dataPersistMock.saveAccount(accountMock)).thenReturn(Completable.complete());

    final TestSubscriber testSubscriber = TestSubscriber.create();

    accountManager.login(credentials)
        .subscribe(testSubscriber);

    testSubscriber.awaitTerminalEvent();
    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();

    verify(accountAnalyticsMock).loginSuccess();
    verify(accountRelayMock).call(accountMock);
  }

  @Test public void shouldSignUp() throws Exception {

    final Account accountMock = mock(Account.class);

    final AptoideCredentials credentials =
        new AptoideCredentials("john.lennon@aptoide.com", "imagine");

    when(credentialsValidatorMock.validate(eq(credentials), anyBoolean())).thenReturn(
        Completable.complete());

    when(serviceMock.createAccount("john.lennon@aptoide.com", "imagine")).thenReturn(
        Single.just(accountMock));

    when(dataPersistMock.saveAccount(accountMock)).thenReturn(Completable.complete());

    final TestSubscriber testSubscriber = TestSubscriber.create();

    accountManager.signUp("APTOIDE", credentials)
        .subscribe(testSubscriber);

    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();

    verify(accountAnalyticsMock).signUp();
    verify(accountRelayMock).call(accountMock);
  }

  @Test public void shouldLoginOnSignUpTimeout() throws Exception {

    final AptoideCredentials credentials =
        new AptoideCredentials("john.lennon@aptoide.com", "imagine");

    when(credentialsValidatorMock.validate(eq(credentials), anyBoolean())).thenReturn(
        Completable.complete());

    when(serviceMock.createAccount("john.lennon@aptoide.com", "imagine")).thenReturn(
        Single.error(new SocketTimeoutException()));

    final Account accountMock = mock(Account.class);

    when(accountMock.getEmail()).thenReturn("john.lennon@aptoide.com");

    when(serviceMock.getAccount("john.lennon@aptoide.com", "imagine")).thenReturn(
        Single.just(accountMock));

    when(dataPersistMock.saveAccount(accountMock)).thenReturn(Completable.complete());

    final TestSubscriber testSubscriber = TestSubscriber.create();

    accountManager.signUp("APTOIDE", credentials)
        .subscribe(testSubscriber);

    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();

    verify(accountRelayMock).call(accountMock);
  }
}

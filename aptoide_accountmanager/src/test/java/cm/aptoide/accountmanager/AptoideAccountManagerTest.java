package cm.aptoide.accountmanager;

import cm.aptoide.pt.model.v3.OAuth;
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

  private Analytics analyticsMock;
  private CredentialsValidator credentialsValidatorMock;
  private AccountFactory accountFactoryMock;
  private AccountDataPersist dataPersistMock;
  private AccountManagerService serviceMock;
  private AptoideAccountManager accountManager;

  @Before public void before() {
    analyticsMock = mock(Analytics.class);
    credentialsValidatorMock = mock(CredentialsValidator.class);
    accountFactoryMock = mock(AccountFactory.class);
    dataPersistMock = mock(AccountDataPersist.class);
    serviceMock = mock(AccountManagerService.class);
    accountManager =
        new AptoideAccountManager(analyticsMock, credentialsValidatorMock, dataPersistMock,
            serviceMock);
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

    when(serviceMock.getAccount("ABCD", "EFG", "1234", "APTOIDE")).thenReturn(
        Single.just(accountMock));

    when(dataPersistMock.saveAccount(accountMock)).thenReturn(Completable.complete());

    final TestSubscriber testSubscriber = TestSubscriber.create();

    accountManager.login(Account.Type.APTOIDE, "marcelo.benites@aptoide.com", "1234", null)
        .subscribe(testSubscriber);

    testSubscriber.awaitTerminalEvent();
    testSubscriber.assertCompleted();
    testSubscriber.assertNoErrors();

    verify(analyticsMock).login("marcelo.benites@aptoide.com");
  }
}
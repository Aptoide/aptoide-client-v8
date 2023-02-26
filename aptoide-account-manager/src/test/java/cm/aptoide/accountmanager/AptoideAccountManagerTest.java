package cm.aptoide.accountmanager;

import androidx.core.util.Pair;
import com.jakewharton.rxrelay.PublishRelay;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import java.net.SocketTimeoutException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AptoideAccountManagerTest {

  @Mock
  private CredentialsValidator credentialsValidatorMock;

  @Mock
  private AccountPersistence dataPersistMock;

  @Mock
  private AccountService serviceMock;

  @Mock
  private PublishRelay<Account> accountRelayMock;

  @Mock
  private AdultContent adultContentMock;

  @Mock
  private StoreManager storeManagerMock;

  private AptoideAccountManager accountManager;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    accountManager =
        new AptoideAccountManager.Builder()
            .setCredentialsValidator(credentialsValidatorMock)
            .setAccountPersistence(dataPersistMock)
            .setAccountService(serviceMock)
            .setAccountRelay(accountRelayMock)
            .setAdultService(adultContentMock)
            .setStoreManager(storeManagerMock)
            .build();
  }

  @Test
  public void shouldLogin() throws Exception {

    final Account accountMock = mock(Account.class);

    final AptoideCredentials credentials =
        new AptoideCredentials("marcelo.benites@aptoide.com", "1234", true, "", "");

    when(credentialsValidatorMock.validate(eq(credentials))).thenReturn(Completable.complete());

    when(serviceMock.getAccount("marcelo.benites@aptoide.com", "1234", "", "")).thenReturn(
        Single.just(Pair.create(accountMock, true)));

    when(dataPersistMock.saveAccount(accountMock)).thenReturn(Completable.complete());

    final TestObserver<Void> testObserver = accountManager.login(credentials).test();

    testObserver.assertComplete();
    testObserver.assertNoErrors();

    verify(accountRelayMock).accept(accountMock);
  }

  @Test
  public void shouldSignUp() throws Exception {

    final Account accountMock = mock(Account.class);

    final AptoideCredentials credentials =
        new AptoideCredentials("john.lennon@aptoide.com", "imagine", true, "", "");

    when(credentialsValidatorMock.validate(eq(credentials))).thenReturn(Completable.complete());

    when(serviceMock.createAccount("john.lennon@aptoide.com", "imagine")).thenReturn(
        Single.just(accountMock));

    when(dataPersistMock.saveAccount(accountMock)).thenReturn(Completable.complete());

    final TestObserver<Void> testObserver = accountManager.signUp("APTOIDE", credentials).test();

    testObserver.assertComplete();
    testObserver.assertNoErrors();

    verify(accountRelayMock).accept(accountMock);
  }

  @Test
  public void shouldLoginOnSignUpTimeout() throws Exception {

    final AptoideCredentials credentials =
        new AptoideCredentials("john.lennon@aptoide.com", "imagine", true, "", "");

    when(credentialsValidatorMock.validate(eq(credentials))).thenReturn(Completable.complete());

    when(serviceMock.createAccount("john.lennon@aptoide.com", "imagine")).thenReturn(
        Single.error(new SocketTimeoutException()));

    final Account accountMock = mock(Account.class);

    when(accountMock.getEmail()).thenReturn("john.lennon@aptoide.com");

    when(serviceMock.getAccount("john.lennon@aptoide.com

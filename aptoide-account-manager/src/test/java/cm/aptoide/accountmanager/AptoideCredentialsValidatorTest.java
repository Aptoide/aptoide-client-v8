package cm.aptoide.accountmanager;

import org.junit.Test;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;

public class AptoideCredentialsValidatorTest {

  @Test public void shouldValidateEmailAndPassword() throws Exception {

    CredentialsValidator validator = new CredentialsValidator();

    final TestSubscriber testSubscriber = TestSubscriber.create();

    validator.validate(
        new AptoideCredentials("marcelo.benites@aptoide.com", "1aMarcelo", true, "", ""))
        .subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    testSubscriber.assertCompleted();
  }

  @Test public void shouldNotValidateEmptyEmail() throws Exception {

    CredentialsValidator validator = new CredentialsValidator();

    final TestSubscriber testSubscriber = TestSubscriber.create();

    validator.validate(new AptoideCredentials("", "1aMarcelo", true, "", ""))
        .subscribe(testSubscriber);

    testSubscriber.assertError(AccountValidationException.class);
    assertEquals(1, ((AccountValidationException) testSubscriber.getOnErrorEvents()
        .get(0)).getCode());
    testSubscriber.assertNotCompleted();
  }

  @Test public void shouldNotValidateEmptyPassword() throws Exception {

    CredentialsValidator validator = new CredentialsValidator();

    final TestSubscriber testSubscriber = TestSubscriber.create();

    validator.validate(new AptoideCredentials("paul.mccartney@beatles.com", "", true, "", ""))
        .subscribe(testSubscriber);

    testSubscriber.assertError(AccountValidationException.class);
    assertEquals(2, ((AccountValidationException) testSubscriber.getOnErrorEvents()
        .get(0)).getCode());
    testSubscriber.assertNotCompleted();
  }

  @Test public void shouldNotValidateEmptyPasswordAndEmptyEmail() throws Exception {

    CredentialsValidator validator = new CredentialsValidator();

    final TestSubscriber testSubscriber = TestSubscriber.create();

    validator.validate(new AptoideCredentials("", "", true, "", ""))
        .subscribe(testSubscriber);

    testSubscriber.assertError(AccountValidationException.class);
    assertEquals(3, ((AccountValidationException) testSubscriber.getOnErrorEvents()
        .get(0)).getCode());
    testSubscriber.assertNotCompleted();
  }
}

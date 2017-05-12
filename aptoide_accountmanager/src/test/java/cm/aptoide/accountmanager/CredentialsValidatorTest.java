package cm.aptoide.accountmanager;

import org.junit.Test;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;

public class CredentialsValidatorTest {

  @Test public void shouldValidateEmailAndPassword() throws Exception {

    CredentialsValidator validator = new CredentialsValidator();

    final TestSubscriber testSubscriber = TestSubscriber.create();

    validator.validate("marcelo.benites@aptoide.com", "1aMarcelo", true)
        .subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    testSubscriber.assertCompleted();
  }

  @Test public void shouldNotValidateEmptyEmail() throws Exception {

    CredentialsValidator validator = new CredentialsValidator();

    final TestSubscriber testSubscriber = TestSubscriber.create();

    validator.validate("", "1aMarcelo", true)
        .subscribe(testSubscriber);

    testSubscriber.assertError(AccountValidationException.class);
    assertEquals(1, ((AccountValidationException) testSubscriber.getOnErrorEvents()
        .get(0)).getCode());
    testSubscriber.assertNotCompleted();
  }

  @Test public void shouldNotValidateEmptyPassword() throws Exception {

    CredentialsValidator validator = new CredentialsValidator();

    final TestSubscriber testSubscriber = TestSubscriber.create();

    validator.validate("paul.mccartney@beatles.com", "", true)
        .subscribe(testSubscriber);

    testSubscriber.assertError(AccountValidationException.class);
    assertEquals(2, ((AccountValidationException) testSubscriber.getOnErrorEvents()
        .get(0)).getCode());
    testSubscriber.assertNotCompleted();
  }

  @Test public void shouldNotValidateEmptyPasswordAndEmptyEmail() throws Exception {

    CredentialsValidator validator = new CredentialsValidator();

    final TestSubscriber testSubscriber = TestSubscriber.create();

    validator.validate("", "", true)
        .subscribe(testSubscriber);

    testSubscriber.assertError(AccountValidationException.class);
    assertEquals(3, ((AccountValidationException) testSubscriber.getOnErrorEvents()
        .get(0)).getCode());
    testSubscriber.assertNotCompleted();
  }

  @Test public void shouldNotValidateLessThan8CharactersPassword() throws Exception {

    CredentialsValidator validator = new CredentialsValidator();

    final TestSubscriber testSubscriber = TestSubscriber.create();

    validator.validate("paul.mccartney@beatles.com", "1234567", true)
        .subscribe(testSubscriber);

    testSubscriber.assertError(AccountValidationException.class);
    assertEquals(4, ((AccountValidationException) testSubscriber.getOnErrorEvents()
        .get(0)).getCode());
    testSubscriber.assertNotCompleted();
  }

  @Test public void shouldNotValidatePasswordWithout1LetterAnd1Number() throws Exception {

    CredentialsValidator validator = new CredentialsValidator();

    final TestSubscriber test1 = TestSubscriber.create();

    validator.validate("paul.mccartney@beatles.com", "beatleswerethebest", true)
        .subscribe(test1);

    test1.assertError(AccountValidationException.class);
    assertEquals(4, ((AccountValidationException) test1.getOnErrorEvents()
        .get(0)).getCode());
    test1.assertNotCompleted();

    final TestSubscriber test2 = TestSubscriber.create();

    validator.validate("paul.mccartney@beatles.com", "123321432413241", true)
        .subscribe(test2);

    test2.assertError(AccountValidationException.class);
    assertEquals(4, ((AccountValidationException) test2.getOnErrorEvents()
        .get(0)).getCode());
    test2.assertNotCompleted();
  }
}
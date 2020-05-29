package cm.aptoide.accountmanager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.validateMockitoUsage;

/**
 * Created by franciscocalado on 02/03/18.
 */

public class CredentialsValidatorTest {

  private CredentialsValidator credentialsValidator;
  private AptoideCredentials validCredentials;
  private AptoideCredentials invalidPasswordCredentials;
  private AptoideCredentials emptyEmailCredentials;
  private AptoideCredentials emptyPasswordCredentials;
  private AptoideCredentials emptyCredentials;
  private int result;

  @Before public void setupCredentialsValidator() {
    MockitoAnnotations.initMocks(this);

    credentialsValidator = new CredentialsValidator();
    result = -2;
  }

  @Test public void validateSuccessTest() {
    validCredentials = new AptoideCredentials("test@test.com", "mypasstest1", true, "", "");

    result = credentialsValidator.validateFields(validCredentials);
    assertEquals(-1, result);
  }

  @Test public void validateEmptyCredentialsErrorTest() {
    emptyCredentials = new AptoideCredentials("", "", true, "", "");

    result = credentialsValidator.validateFields(emptyCredentials);
    assertEquals(AccountValidationException.EMPTY_EMAIL_AND_CODE, result);
  }

  @Test public void validateEmptyEmailCredentialsErrorTest() {
    emptyEmailCredentials = new AptoideCredentials("", "test1", true, "", "");

    result = credentialsValidator.validateFields(emptyEmailCredentials);
    assertEquals(AccountValidationException.EMPTY_EMAIL, result);
  }

  @Test public void validateEmptyPasswordCredentialsErrorTest() {
    emptyPasswordCredentials = new AptoideCredentials("test@test.com", "", true, "", "");

    result = credentialsValidator.validateFields(emptyPasswordCredentials);
    assertEquals(AccountValidationException.EMPTY_CODE, result);
  }

  @After public void teardownCredentialsValidator() {
    credentialsValidator = null;
    result = -2;
  }

  @After public void validate() {
    validateMockitoUsage();
  }
}

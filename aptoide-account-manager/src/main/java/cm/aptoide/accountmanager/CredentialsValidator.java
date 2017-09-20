package cm.aptoide.accountmanager;

import rx.Completable;

public class CredentialsValidator {
  /**
   * Returns true if email and password are not empty. If validate password content is enable
   * returns true if password is at least 8 characters long and has at least 1 number and 1 letter.
   *
   * @param credentials
   * @param validatePassword whether password content should be validated.
   */
  public Completable validate(AptoideCredentials credentials, boolean validatePassword) {
    return Completable.defer(() -> {
      if (isEmpty(credentials.getEmail()) && isEmpty(credentials.getPassword())) {
        return Completable.error(
            new AccountValidationException(AccountValidationException.EMPTY_EMAIL_AND_PASSWORD));
      } else if (isEmpty(credentials.getPassword())) {
        return Completable.error(
            new AccountValidationException(AccountValidationException.EMPTY_PASSWORD));
      } else if (isEmpty(credentials.getEmail())) {
        return Completable.error(
            new AccountValidationException(AccountValidationException.EMPTY_EMAIL));
      } else if (validatePassword && (credentials.getPassword()
          .length() < 8 || !has1number1letter(credentials.getPassword()))) {
        return Completable.error(
            new AccountValidationException(AccountValidationException.INVALID_PASSWORD));
      }
      return Completable.complete();
    });
  }

  private boolean has1number1letter(String password) {
    boolean hasLetter = false;
    boolean hasNumber = false;

    for (char c : password.toCharArray()) {
      if (!hasLetter && Character.isLetter(c)) {
        if (hasNumber) return true;
        hasLetter = true;
      } else if (!hasNumber && Character.isDigit(c)) {
        if (hasLetter) return true;
        hasNumber = true;
      }
    }
    if (password.contains("!")
        || password.contains("@")
        || password.contains("#")
        || password.contains("$")
        || password.contains("#")
        || password.contains("*")) {
      hasNumber = true;
    }

    return hasNumber && hasLetter;
  }

  private boolean isEmpty(CharSequence str) {
    return str == null || str.length() == 0;
  }
}
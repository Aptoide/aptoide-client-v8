package cm.aptoide.pt;

/**
 * Created by jose_messejana on 15-11-2017.
 */

public class TestType {
  public static TestTypes types = TestTypes.PHOTOSUCCESS;

  public enum TestTypes {
    REGULAR, SIGNSIGNUPTESTS, SIGNINWRONG, LOGGEDIN, USEDEMAIL, INVALIDEMAIL, PHOTOMAX, PHOTOMIN, PHOTOSUCCESS, MATURE
  }
}

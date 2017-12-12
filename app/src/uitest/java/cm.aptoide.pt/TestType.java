package cm.aptoide.pt;

/**
 * Created by jose_messejana on 15-11-2017.
 */

public class TestType {
  public static TestTypes types = TestTypes.REGULAR;
  public static TestTypes initialization = TestTypes.REGULAR; //LOGGEDIN / LOGGEDINWITHSTORE

  public enum TestTypes {
    REGULAR, SIGNSIGNUPTESTS, SIGNINWRONG, LOGGEDIN, USEDEMAIL, INVALIDEMAIL, PHOTOMAX, PHOTOMIN, PHOTOSUCCESS, MATURE, LOGGEDINWITHSTORE
  }
}

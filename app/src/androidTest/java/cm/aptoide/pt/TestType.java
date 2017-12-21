package cm.aptoide.pt;

/**
 * Created by jose_messejana on 15-11-2017.
 */

public class TestType {
  public static TestTypes types = TestTypes.REGULAR;
  public static TestTypes initialization = TestTypes.REGULAR; //LOGGEDIN / LOGGEDINWITHSTORE

  public enum TestTypes {
    REGULAR, SIGNSIGNUPTESTS, SIGNINWRONG, LOGGEDIN, USEDEMAIL, INVALIDEMAIL, MATURE, LOGGEDINWITHSTORE, PHOTOSUCCESS, ERRORDECONDINGTEST, MIN_HEIGHTTEST, MIN_WIDTHTEST, MAX_HEIGHTTEST, MAX_WIDTHTEST, MAX_IMAGE_SIZETEST
  }
}

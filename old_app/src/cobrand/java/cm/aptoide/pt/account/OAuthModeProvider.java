package cm.aptoide.pt.account;

public class OAuthModeProvider {

  public String getAuthMode(String mode){
    switch (mode) {
      case "GOOGLE":
        return "google";
      case "FACEBOOK":
        return "facebook_cobrand";
      case "ABAN":
        return "aban";
    }
    return null;
  }
}

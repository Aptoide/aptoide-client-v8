package com.mopub.nativeads;

public class InMobiGDPR {
  private static String gdpr = "0";
  private static boolean consent = false;
  private static boolean consentUpdated = false;

  /**
   * Call InMobiGDPR.grantConsent() to provide GDPR consent for the user on each request basis.
   */
  public static void grantConsent() {
    consentUpdated = true;
    consent = true;
  }

  /**
   * Call InMobiGDPR.revokeConsent() to remove GDPR consent for the user on each request basis.
   */
  public static void revokeConsent() {
    consentUpdated = true;
    consent = false;
  }

  /**
   * Call InMobiGDPR.isGDPRApplicable() to let InMobi know if GDPR is applicable for the user.
   */
  public static void isGDPRApplicable(final boolean isGDPRApplicable) {
    consentUpdated = true;
    if (isGDPRApplicable) {
      gdpr = "1";
    } else {
      gdpr = "0";
    }
  }

  static String isGDPR() {
    return gdpr;
  }

  static boolean getConsent() {
    return consent;
  }

  public static boolean isConsentUpdated() {
    return consentUpdated;
  }
}

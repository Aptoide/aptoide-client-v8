package cm.aptoide.pt.preferences;

import android.content.pm.PackageManager;
import cm.aptoide.pt.appview.PreferencesPersister;
import cm.aptoide.pt.utils.AptoideUtils;

import static cm.aptoide.pt.preferences.managed.ManagedKeys.APTOIDE_MD5;

public class AptoideMd5Manager {

  private PreferencesPersister preferencesPersister;

  private PackageManager packageManager;
  private String packageName;
  private int vercode;

  private String cachedMd5;

  public AptoideMd5Manager(PreferencesPersister preferencesPersister, PackageManager packageManager,
      String packageName, int vercode) {
    this.preferencesPersister = preferencesPersister;
    this.packageManager = packageManager;
    this.packageName = packageName;
    this.vercode = vercode;
  }

  /**
   * Retrieves the Aptoide MD5.
   *
   * There's three different strategies employed to retrieve it:
   *
   * 1) Checks memory cache. If it exists, returns immediately.
   * 2) Checks SharedPreferences. If it's stored, saves to memory cache and returns it.
   * 3) Calculates the md5 of package and stores it to SharedPreferences and memory cache.
   *
   * Note that if there's a version update, the SharedPreferences value is invalidated.
   *
   * @return Aptoide's md5
   */
  public String getAptoideMd5() {
    if (cachedMd5 != null) return cachedMd5;

    String savedMd5 = parseMd5(preferencesPersister.get(APTOIDE_MD5, ""), vercode);

    return savedMd5;
  }

  public Void calculateMd5Sum() {
    if (cachedMd5 != null) return null;
    if (!parseMd5(preferencesPersister.get(APTOIDE_MD5, ""), vercode).isEmpty()) return null;
    try {
      cachedMd5 = AptoideUtils.AlgorithmU.computeMd5(packageManager.getPackageInfo(packageName, 0));
      persistAptoideMd5(vercode, cachedMd5);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void persistAptoideMd5(int vercode, String md5) {
    preferencesPersister.save(APTOIDE_MD5, vercode + "_" + md5);
  }

  /**
   * Verifies if there's a md5 for the matching vercode.
   * This checks for strings matching "vercode_md5"
   *
   * If there's any problems matching, returns an empty string.
   */
  private String parseMd5(String s, int vercode) {
    if (s != null && !s.isEmpty()) {
      String[] split = s.split("_", 2);
      try {
        if (split.length == 2 && Integer.parseInt(split[0]) == vercode) {
          return split[1];
        }
      } catch (NumberFormatException ignored) {
      }
    }
    return "";
  }
}

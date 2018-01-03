package cm.aptoide.pt.utils.q;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Base64;
import android.view.WindowManager;

import static cm.aptoide.pt.utils.AptoideUtils.ScreenU;
import static cm.aptoide.pt.utils.AptoideUtils.SystemU;

/**
 * Created by neuro on 12-05-2017.
 */
public class QManager {

  private final GlExtensionsManager glExtensionsManager;
  private final Resources resources;
  private final ActivityManager activityManager;
  private final WindowManager windowManager;
  private Integer minSdk;
  private String cpuAbi;
  private String screenSize;
  private String glEs;
  private Integer densityDpi;
  private String cachedFilters;

  public QManager(SharedPreferences sharedPreferences, Resources resources,
      ActivityManager activityManager, WindowManager windowManager) {
    this.glExtensionsManager = new GlExtensionsManager(sharedPreferences);
    this.resources = resources;
    this.activityManager = activityManager;
    this.windowManager = windowManager;
  }

  public Integer getMinSdk() {
    if (minSdk == null) {
      minSdk = computeMinSdk();
    }
    return minSdk;
  }

  public String getCpuAbi() {
    if (cpuAbi == null) {
      cpuAbi = computeCpuAbi();
    }
    return cpuAbi;
  }

  public String getScreenSize() {
    if (screenSize == null) {
      screenSize = computeScreenSize();
    }
    return screenSize;
  }

  public String getGlEs() {
    if (glEs == null) {
      glEs = computeGlEs();
    }
    return glEs;
  }

  public Integer getDensityDpi() {
    if (densityDpi == null) {
      densityDpi = computeDensityDpi();
    }
    return densityDpi;
  }

  private int computeMinSdk() {
    return SystemU.getSdkVer();
  }

  private String computeScreenSize() {
    return ScreenU.getScreenSize(resources);
  }

  private String computeGlEs() {
    return SystemU.getGlEsVer(activityManager);
  }

  private int computeDensityDpi() {
    return ScreenU.getDensityDpi(windowManager);
  }

  private String computeCpuAbi() {
    return SystemU.getAbis();
  }

  public String getSupportedOpenGlExtensionsManager() {
    return glExtensionsManager.getSupportedExtensions();
  }

  public boolean isSupportedExtensionsDefined() {
    return glExtensionsManager.isSupportedExtensionsDefined();
  }

  public String getFilters(boolean hwSpecsFilter) {
    if (!hwSpecsFilter) {
      return null;
    }

    if (cachedFilters == null) {
      cachedFilters = computeFilters();
    }

    return cachedFilters;
  }

  private String computeFilters() {
    String filters = "maxSdk="
        + getMinSdk()
        + "&maxScreen="
        + getScreenSize()
        + "&maxGles="
        + getGlEs()
        + "&myCPU="
        + getCpuAbi()
        + "&myDensity="
        + getDensityDpi()
        + (getSupportedOpenGlExtensionsManager().equals("") ? ""
        : "&myGLTex=" + getSupportedOpenGlExtensionsManager());

    return Base64.encodeToString(filters.getBytes(), 0)
        .replace("=", "")
        .replace("/", "*")
        .replace("+", "_")
        .replace("\n", "");
  }

  private void invalidate() {
    cachedFilters = null;
  }

  public void setSupportedOpenGLExtensions(String openGLExtensions) {
    glExtensionsManager.setSupportedOpenGLExtensions(openGLExtensions);
    invalidate();
  }
}

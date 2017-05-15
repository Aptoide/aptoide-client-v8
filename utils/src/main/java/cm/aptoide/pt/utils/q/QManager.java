package cm.aptoide.pt.utils.q;

import android.util.Base64;
import cm.aptoide.pt.utils.Invalidate;
import lombok.Getter;

import static cm.aptoide.pt.utils.AptoideUtils.ScreenU;
import static cm.aptoide.pt.utils.AptoideUtils.SystemU;

/**
 * Created by neuro on 12-05-2017.
 */
public class QManager implements Invalidate {

  @Getter(lazy = true) private final int minSdk = computeMinSdk();
  @Getter(lazy = true) private final String screenSize = computeScreenSize();
  @Getter(lazy = true) private final String glEs = computeGlEs();
  @Getter(lazy = true) private final int densityDpi = computeDensityDpi();
  @Getter(lazy = true) private final String cpuAbi = computeCpuAbi();
  private final GlExtensionsManager glExtensionsManager;

  private String cachedFilters;

  public QManager() {
    this.glExtensionsManager = GlExtensionsManager.getInstance();
  }

  private int computeMinSdk() {
    return SystemU.getSdkVer();
  }

  private String computeScreenSize() {
    return ScreenU.getScreenSize();
  }

  private String computeGlEs() {
    return SystemU.getGlEsVer();
  }

  private int computeDensityDpi() {
    return ScreenU.getDensityDpi();
  }

  private String computeCpuAbi() {
    return SystemU.getAbis();
  }

  public String getGlExtensionsManager() {
    return glExtensionsManager.get();
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
        + (getGlExtensionsManager().equals("") ? "" : "&myGLTex=" + getGlExtensionsManager());

    return Base64.encodeToString(filters.getBytes(), 0)
        .replace("=", "")
        .replace("/", "*")
        .replace("+", "_")
        .replace("\n", "");
  }

  @Override public void invalidate() {
    cachedFilters = null;
  }

  public void setOpenGLExtensions(String openGLExtensions) {
    glExtensionsManager.setOpenGLExtensions(openGLExtensions);
    invalidate();
  }
}

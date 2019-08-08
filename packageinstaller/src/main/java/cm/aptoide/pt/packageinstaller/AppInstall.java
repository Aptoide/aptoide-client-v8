package cm.aptoide.pt.packageinstaller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppInstall {

  private String packageName;
  private File baseApk;
  private List<File> splitApks;

  private AppInstall(InstallBuilder builder) {
    packageName = builder.packageName;
    baseApk = builder.baseApk;
    splitApks = builder.splitApks;
  }

  public static InstallBuilder builder() {
    return new InstallBuilder();
  }

  public String getPackageName() {
    return packageName;
  }

  public File getBaseApk() {
    return baseApk;
  }

  public List<File> getSplitApks() {
    return splitApks;
  }

  public static final class InstallBuilder {
    private String packageName;
    private File baseApk;
    private List<File> splitApks;

    private InstallBuilder() {
      this.splitApks = new ArrayList<>();
    }

    public InstallBuilder setPackageName(String packageName) {
      this.packageName = packageName;
      return this;
    }

    public InstallBuilder setBaseApk(File baseApk) {
      this.baseApk = baseApk;
      return this;
    }

    public InstallBuilder addApkSplit(File splitApk) {
      this.splitApks.add(splitApk);
      return this;
    }

    public AppInstall build() {
      if (packageName == null) {
        throw new IllegalArgumentException("AppInstall needs to specify a package name");
      }
      if (baseApk == null) {
        throw new IllegalArgumentException("AppInstall needs a base apk to install");
      }
      return new AppInstall(this);
    }
  }
}

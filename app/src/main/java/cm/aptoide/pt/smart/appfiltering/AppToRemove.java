package cm.aptoide.pt.smart.appfiltering;

class AppToRemove {
    private String appPackage = "";
    private String version = "";

    public AppToRemove(String appPackage, String version) {
        this.appPackage = appPackage;
        this.version = version;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public String getVersion() {
        return version;
    }
}

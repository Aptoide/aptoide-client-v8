package cm.aptoide.pt.dataprovider.model.smart;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FilteredApp {

    @JsonProperty("package")
    private String pkg;

    @JsonProperty("version")
    private String version;

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "FilteredApp{pkg='" + pkg + "', version='" + version + "'}";
    }
}
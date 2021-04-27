package cm.aptoide.pt.dataprovider.model.smart;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RemovedApp {

    @JsonProperty("package")
    private String pkg;

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    @Override
    public String toString() {
        return "RemovedItem{package = '" + pkg + "'}";
    }
}
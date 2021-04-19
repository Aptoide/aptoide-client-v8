
package cm.aptoide.pt.smart.appfiltering.data;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Versions {

    @SerializedName("info")
    private Info mInfo;
    @SerializedName("list")
    private java.util.List<Version> mVersion;

    public Info getInfo() {
        return mInfo;
    }

    public void setInfo(Info info) {
        mInfo = info;
    }

    public java.util.List<Version> getList() {
        return mVersion;
    }

    public void setList(java.util.List<Version> Version) {
        mVersion = Version;
    }

}

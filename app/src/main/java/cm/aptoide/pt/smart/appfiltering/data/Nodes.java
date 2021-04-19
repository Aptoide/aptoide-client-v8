
package cm.aptoide.pt.smart.appfiltering.data;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Nodes {

    @SerializedName("groups")
    private Groups mGroups;
    @SerializedName("meta")
    private Meta mMeta;
    @SerializedName("versions")
    private Versions mVersions;

    public Groups getGroups() {
        return mGroups;
    }

    public void setGroups(Groups groups) {
        mGroups = groups;
    }

    public Meta getMeta() {
        return mMeta;
    }

    public void setMeta(Meta meta) {
        mMeta = meta;
    }

    public Versions getVersions() {
        return mVersions;
    }

    public void setVersions(Versions versions) {
        mVersions = versions;
    }

}

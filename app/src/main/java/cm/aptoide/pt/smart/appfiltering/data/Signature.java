
package cm.aptoide.pt.smart.appfiltering.data;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Signature {

    @SerializedName("owner")
    private String mOwner;
    @SerializedName("sha1")
    private String mSha1;

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public String getSha1() {
        return mSha1;
    }

    public void setSha1(String sha1) {
        mSha1 = sha1;
    }

}

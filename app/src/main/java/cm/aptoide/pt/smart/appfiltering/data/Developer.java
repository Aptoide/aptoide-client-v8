
package cm.aptoide.pt.smart.appfiltering.data;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Developer {

    @SerializedName("email")
    private String mEmail;
    @SerializedName("id")
    private Long mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("privacy")
    private String mPrivacy;
    @SerializedName("website")
    private String mWebsite;

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPrivacy() {
        return mPrivacy;
    }

    public void setPrivacy(String privacy) {
        mPrivacy = privacy;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(String website) {
        mWebsite = website;
    }

}

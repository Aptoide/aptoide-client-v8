
package cm.aptoide.pt.smart.appfiltering.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class File {

    @SerializedName("added")
    private String mAdded;
    @SerializedName("filesize")
    private Long mFilesize;
    @SerializedName("flags")
    private Flags mFlags;
    @SerializedName("hardware")
    private Hardware mHardware;
    @SerializedName("malware")
    private Malware mMalware;
    @SerializedName("md5sum")
    private String mMd5sum;
    @SerializedName("path")
    private String mPath;
    @SerializedName("path_alt")
    private String mPathAlt;
    @SerializedName("signature")
    private Signature mSignature;
    @SerializedName("tags")
    private List<String> mTags;
    @SerializedName("used_features")
    private List<String> mUsedFeatures;
    @SerializedName("used_permissions")
    private List<String> mUsedPermissions;
    @SerializedName("vercode")
    private Long mVercode;
    @SerializedName("vername")
    private String mVername;

    public String getAdded() {
        return mAdded;
    }

    public void setAdded(String added) {
        mAdded = added;
    }

    public Long getFilesize() {
        return mFilesize;
    }

    public void setFilesize(Long filesize) {
        mFilesize = filesize;
    }

    public Flags getFlags() {
        return mFlags;
    }

    public void setFlags(Flags flags) {
        mFlags = flags;
    }

    public Hardware getHardware() {
        return mHardware;
    }

    public void setHardware(Hardware hardware) {
        mHardware = hardware;
    }

    public Malware getMalware() {
        return mMalware;
    }

    public void setMalware(Malware malware) {
        mMalware = malware;
    }

    public String getMd5sum() {
        return mMd5sum;
    }

    public void setMd5sum(String md5sum) {
        mMd5sum = md5sum;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getPathAlt() {
        return mPathAlt;
    }

    public void setPathAlt(String pathAlt) {
        mPathAlt = pathAlt;
    }

    public Signature getSignature() {
        return mSignature;
    }

    public void setSignature(Signature signature) {
        mSignature = signature;
    }

    public List<String> getTags() {
        return mTags;
    }

    public void setTags(List<String> tags) {
        mTags = tags;
    }

    public List<String> getUsedFeatures() {
        return mUsedFeatures;
    }

    public void setUsedFeatures(List<String> usedFeatures) {
        mUsedFeatures = usedFeatures;
    }

    public List<String> getUsedPermissions() {
        return mUsedPermissions;
    }

    public void setUsedPermissions(List<String> usedPermissions) {
        mUsedPermissions = usedPermissions;
    }

    public Long getVercode() {
        return mVercode;
    }

    public void setVercode(Long vercode) {
        mVercode = vercode;
    }

    public String getVername() {
        return mVername;
    }

    public void setVername(String vername) {
        mVername = vername;
    }

}

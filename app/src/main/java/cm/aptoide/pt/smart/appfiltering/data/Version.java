
package cm.aptoide.pt.smart.appfiltering.data;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Version {

    @SerializedName("added")
    private String mAdded;
    @SerializedName("appcoins")
    private Appcoins mAppcoins;
    @SerializedName("file")
    private File mFile;
    @SerializedName("graphic")
    private Object mGraphic;
    @SerializedName("icon")
    private Object mIcon;
    @SerializedName("id")
    private Long mId;
    @SerializedName("modified")
    private String mModified;
    @SerializedName("name")
    private String mName;
    @SerializedName("obb")
    private Object mObb;
    @SerializedName("package")
    private String mPackage;
    @SerializedName("parent")
    private Parent mParent;
    @SerializedName("size")
    private Long mSize;
    @SerializedName("stats")
    private Stats mStats;
    @SerializedName("store")
    private Store mStore;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("uname")
    private String mUname;
    @SerializedName("updated")
    private String mUpdated;
    @SerializedName("uptype")
    private String mUptype;

    public String getAdded() {
        return mAdded;
    }

    public void setAdded(String added) {
        mAdded = added;
    }

    public Appcoins getAppcoins() {
        return mAppcoins;
    }

    public void setAppcoins(Appcoins appcoins) {
        mAppcoins = appcoins;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public Object getGraphic() {
        return mGraphic;
    }

    public void setGraphic(Object graphic) {
        mGraphic = graphic;
    }

    public Object getIcon() {
        return mIcon;
    }

    public void setIcon(Object icon) {
        mIcon = icon;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getModified() {
        return mModified;
    }

    public void setModified(String modified) {
        mModified = modified;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Object getObb() {
        return mObb;
    }

    public void setObb(Object obb) {
        mObb = obb;
    }

    public String getPackage() {
        return mPackage;
    }

    public void setPackage(String appPackage) {
        mPackage = appPackage;
    }

    public Parent getParent() {
        return mParent;
    }

    public void setParent(Parent parent) {
        mParent = parent;
    }

    public Long getSize() {
        return mSize;
    }

    public void setSize(Long size) {
        mSize = size;
    }

    public Stats getStats() {
        return mStats;
    }

    public void setStats(Stats stats) {
        mStats = stats;
    }

    public Store getStore() {
        return mStore;
    }

    public void setStore(Store store) {
        mStore = store;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUname() {
        return mUname;
    }

    public void setUname(String uname) {
        mUname = uname;
    }

    public String getUpdated() {
        return mUpdated;
    }

    public void setUpdated(String updated) {
        mUpdated = updated;
    }

    public String getUptype() {
        return mUptype;
    }

    public void setUptype(String uptype) {
        mUptype = uptype;
    }

}


package cm.aptoide.pt.smart.appfiltering.data;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Stats {

    @SerializedName("apps")
    private Long mApps;
    @SerializedName("downloads")
    private Long mDownloads;
    @SerializedName("groups")
    private Long mGroups;
    @SerializedName("items")
    private Long mItems;
    @SerializedName("pdownloads")
    private Long mPdownloads;
    @SerializedName("prating")
    private Prating mPrating;
    @SerializedName("rating")
    private Rating mRating;
    @SerializedName("subscribers")
    private Long mSubscribers;

    public Long getApps() {
        return mApps;
    }

    public void setApps(Long apps) {
        mApps = apps;
    }

    public Long getDownloads() {
        return mDownloads;
    }

    public void setDownloads(Long downloads) {
        mDownloads = downloads;
    }

    public Long getGroups() {
        return mGroups;
    }

    public void setGroups(Long groups) {
        mGroups = groups;
    }

    public Long getItems() {
        return mItems;
    }

    public void setItems(Long items) {
        mItems = items;
    }

    public Long getPdownloads() {
        return mPdownloads;
    }

    public void setPdownloads(Long pdownloads) {
        mPdownloads = pdownloads;
    }

    public Prating getPrating() {
        return mPrating;
    }

    public void setPrating(Prating prating) {
        mPrating = prating;
    }

    public Rating getRating() {
        return mRating;
    }

    public void setRating(Rating rating) {
        mRating = rating;
    }

    public Long getSubscribers() {
        return mSubscribers;
    }

    public void setSubscribers(Long subscribers) {
        mSubscribers = subscribers;
    }

}

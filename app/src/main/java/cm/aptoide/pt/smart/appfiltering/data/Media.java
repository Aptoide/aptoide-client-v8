
package cm.aptoide.pt.smart.appfiltering.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Media {

    @SerializedName("description")
    private String mDescription;
    @SerializedName("keywords")
    private List<String> mKeywords;
    @SerializedName("news")
    private String mNews;
    @SerializedName("screenshots")
    private List<Screenshot> mScreenshots;
    @SerializedName("summary")
    private String mSummary;
    @SerializedName("videos")
    private List<Object> mVideos;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public List<String> getKeywords() {
        return mKeywords;
    }

    public void setKeywords(List<String> keywords) {
        mKeywords = keywords;
    }

    public String getNews() {
        return mNews;
    }

    public void setNews(String news) {
        mNews = news;
    }

    public List<Screenshot> getScreenshots() {
        return mScreenshots;
    }

    public void setScreenshots(List<Screenshot> screenshots) {
        mScreenshots = screenshots;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public List<Object> getVideos() {
        return mVideos;
    }

    public void setVideos(List<Object> videos) {
        mVideos = videos;
    }

}

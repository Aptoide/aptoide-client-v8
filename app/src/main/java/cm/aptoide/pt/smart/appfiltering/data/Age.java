
package cm.aptoide.pt.smart.appfiltering.data;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Age {

    @SerializedName("name")
    private String mName;
    @SerializedName("pegi")
    private String mPegi;
    @SerializedName("rating")
    private Long mRating;
    @SerializedName("title")
    private String mTitle;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPegi() {
        return mPegi;
    }

    public void setPegi(String pegi) {
        mPegi = pegi;
    }

    public Long getRating() {
        return mRating;
    }

    public void setRating(Long rating) {
        mRating = rating;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

}

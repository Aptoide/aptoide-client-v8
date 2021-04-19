
package cm.aptoide.pt.smart.appfiltering.data;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Appearance {

    @SerializedName("description")
    private String mDescription;
    @SerializedName("theme")
    private String mTheme;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getTheme() {
        return mTheme;
    }

    public void setTheme(String theme) {
        mTheme = theme;
    }

}

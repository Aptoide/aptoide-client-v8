
package cm.aptoide.pt.smart.appfiltering.data;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Parent {

    @SerializedName("graphic")
    private Object mGraphic;
    @SerializedName("icon")
    private Object mIcon;
    @SerializedName("id")
    private Long mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("title")
    private String mTitle;

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

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

}

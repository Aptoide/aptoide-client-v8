
package cm.aptoide.pt.smart.appfiltering.data;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Groups {

    @SerializedName("datalist")
    private Datalist mDatalist;
    @SerializedName("info")
    private Info mInfo;

    public Datalist getDatalist() {
        return mDatalist;
    }

    public void setDatalist(Datalist datalist) {
        mDatalist = datalist;
    }

    public Info getInfo() {
        return mInfo;
    }

    public void setInfo(Info info) {
        mInfo = info;
    }

}

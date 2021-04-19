
package cm.aptoide.pt.smart.appfiltering.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Scanned {

    @SerializedName("av_info")
    private List<AvInfo> mAvInfo;
    @SerializedName("date")
    private String mDate;
    @SerializedName("status")
    private String mStatus;

    public List<AvInfo> getAvInfo() {
        return mAvInfo;
    }

    public void setAvInfo(List<AvInfo> avInfo) {
        mAvInfo = avInfo;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

}

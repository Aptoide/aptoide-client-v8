
package cm.aptoide.pt.smart.appfiltering.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Appcoins {

    @SerializedName("advertising")
    private Boolean mAdvertising;
    @SerializedName("billing")
    private Boolean mBilling;
    @SerializedName("flags")
    private List<Object> mFlags;

    public Boolean getAdvertising() {
        return mAdvertising;
    }

    public void setAdvertising(Boolean advertising) {
        mAdvertising = advertising;
    }

    public Boolean getBilling() {
        return mBilling;
    }

    public void setBilling(Boolean billing) {
        mBilling = billing;
    }

    public List<Object> getFlags() {
        return mFlags;
    }

    public void setFlags(List<Object> flags) {
        mFlags = flags;
    }

}

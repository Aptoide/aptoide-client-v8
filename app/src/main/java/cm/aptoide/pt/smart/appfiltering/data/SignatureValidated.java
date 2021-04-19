
package cm.aptoide.pt.smart.appfiltering.data;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class SignatureValidated {

    @SerializedName("date")
    private String mDate;
    @SerializedName("signature_from")
    private String mSignatureFrom;
    @SerializedName("status")
    private String mStatus;

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getSignatureFrom() {
        return mSignatureFrom;
    }

    public void setSignatureFrom(String signatureFrom) {
        mSignatureFrom = signatureFrom;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

}

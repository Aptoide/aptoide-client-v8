
package cm.aptoide.pt.smart.appfiltering.data;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Reason {

    @SerializedName("scanned")
    private Scanned mScanned;
    @SerializedName("signature_validated")
    private SignatureValidated mSignatureValidated;

    public Scanned getScanned() {
        return mScanned;
    }

    public void setScanned(Scanned scanned) {
        mScanned = scanned;
    }

    public SignatureValidated getSignatureValidated() {
        return mSignatureValidated;
    }

    public void setSignatureValidated(SignatureValidated signatureValidated) {
        mSignatureValidated = signatureValidated;
    }

}


package cm.aptoide.pt.smart.appfiltering.data;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Urls {

    @SerializedName("m")
    private String mM;
    @SerializedName("w")
    private String mW;

    public String getM() {
        return mM;
    }

    public void setM(String m) {
        mM = m;
    }

    public String getW() {
        return mW;
    }

    public void setW(String w) {
        mW = w;
    }

}

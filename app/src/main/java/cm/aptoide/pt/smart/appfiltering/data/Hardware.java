
package cm.aptoide.pt.smart.appfiltering.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Hardware {

    @SerializedName("cpus")
    private List<String> mCpus;
    @SerializedName("densities")
    private List<Object> mDensities;
    @SerializedName("dependencies")
    private List<Object> mDependencies;
    @SerializedName("gles")
    private Long mGles;
    @SerializedName("screen")
    private String mScreen;
    @SerializedName("sdk")
    private Long mSdk;

    public List<String> getCpus() {
        return mCpus;
    }

    public void setCpus(List<String> cpus) {
        mCpus = cpus;
    }

    public List<Object> getDensities() {
        return mDensities;
    }

    public void setDensities(List<Object> densities) {
        mDensities = densities;
    }

    public List<Object> getDependencies() {
        return mDependencies;
    }

    public void setDependencies(List<Object> dependencies) {
        mDependencies = dependencies;
    }

    public Long getGles() {
        return mGles;
    }

    public void setGles(Long gles) {
        mGles = gles;
    }

    public String getScreen() {
        return mScreen;
    }

    public void setScreen(String screen) {
        mScreen = screen;
    }

    public Long getSdk() {
        return mSdk;
    }

    public void setSdk(Long sdk) {
        mSdk = sdk;
    }

}

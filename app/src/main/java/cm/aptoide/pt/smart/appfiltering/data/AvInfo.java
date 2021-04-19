
package cm.aptoide.pt.smart.appfiltering.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class AvInfo {

    @SerializedName("infections")
    private List<Object> mInfections;
    @SerializedName("name")
    private String mName;

    public List<Object> getInfections() {
        return mInfections;
    }

    public void setInfections(List<Object> infections) {
        mInfections = infections;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

}

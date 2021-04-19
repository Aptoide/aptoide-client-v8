
package cm.aptoide.pt.smart.appfiltering.data;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class GetAppResponse {

    @SerializedName("info")
    private Info mInfo;
    @SerializedName("nodes")
    private Nodes mNodes;

    public Info getInfo() {
        return mInfo;
    }

    public void setInfo(Info info) {
        mInfo = info;
    }

    public Nodes getNodes() {
        return mNodes;
    }

    public void setNodes(Nodes nodes) {
        mNodes = nodes;
    }

}

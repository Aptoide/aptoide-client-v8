
package cm.aptoide.pt.smart.appfiltering.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Rating {

    @SerializedName("avg")
    private Long mAvg;
    @SerializedName("total")
    private Long mTotal;
    @SerializedName("votes")
    private List<Vote> mVotes;

    public Long getAvg() {
        return mAvg;
    }

    public void setAvg(Long avg) {
        mAvg = avg;
    }

    public Long getTotal() {
        return mTotal;
    }

    public void setTotal(Long total) {
        mTotal = total;
    }

    public List<Vote> getVotes() {
        return mVotes;
    }

    public void setVotes(List<Vote> votes) {
        mVotes = votes;
    }

}

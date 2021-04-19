
package cm.aptoide.pt.smart.appfiltering.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Flags {

    @SerializedName("votes")
    private List<Vote> mVotes;

    public List<Vote> getVotes() {
        return mVotes;
    }

    public void setVotes(List<Vote> votes) {
        mVotes = votes;
    }

}

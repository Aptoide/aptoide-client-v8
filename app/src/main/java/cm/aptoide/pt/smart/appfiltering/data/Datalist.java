
package cm.aptoide.pt.smart.appfiltering.data;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Datalist {

    @SerializedName("count")
    private Long mCount;
    @SerializedName("hidden")
    private Long mHidden;
    @SerializedName("limit")
    private Long mLimit;
    @SerializedName("list")
    private java.util.List<Version> mVersion;
    @SerializedName("loaded")
    private Boolean mLoaded;
    @SerializedName("next")
    private Long mNext;
    @SerializedName("offset")
    private Long mOffset;
    @SerializedName("total")
    private Long mTotal;

    public Long getCount() {
        return mCount;
    }

    public void setCount(Long count) {
        mCount = count;
    }

    public Long getHidden() {
        return mHidden;
    }

    public void setHidden(Long hidden) {
        mHidden = hidden;
    }

    public Long getLimit() {
        return mLimit;
    }

    public void setLimit(Long limit) {
        mLimit = limit;
    }

    public java.util.List<Version> getList() {
        return mVersion;
    }

    public void setList(java.util.List<Version> Version) {
        mVersion = Version;
    }

    public Boolean getLoaded() {
        return mLoaded;
    }

    public void setLoaded(Boolean loaded) {
        mLoaded = loaded;
    }

    public Long getNext() {
        return mNext;
    }

    public void setNext(Long next) {
        mNext = next;
    }

    public Long getOffset() {
        return mOffset;
    }

    public void setOffset(Long offset) {
        mOffset = offset;
    }

    public Long getTotal() {
        return mTotal;
    }

    public void setTotal(Long total) {
        mTotal = total;
    }

}

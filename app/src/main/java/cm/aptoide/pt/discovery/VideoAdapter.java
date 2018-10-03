package cm.aptoide.pt.discovery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder> {
  private static final int LOADING = R.layout.progress_item;
  private static final int VIDEO = R.layout.fragment_videos_list_item;
  private final LoadMoreVideos loadMore;
  private List<Video> videosList;

  public VideoAdapter(List<Video> videosList) {
    this.videosList = videosList;
    this.loadMore = new LoadMoreVideos("", "", 0, "", "");
  }

  @Override public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case VIDEO:
        return new VideoViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(VIDEO, parent, false));
      case LOADING:
        return new LoadMoreVideosViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(LOADING, parent, false));
      default:
        throw new IllegalStateException("Invalid video view type");
    }
  }

  @Override public void onBindViewHolder(VideoViewHolder holder, int position) {
    holder.setContent(videosList.get(position));
    holder.setAppName(videosList.get(position));
    holder.setAppScore(videosList.get(position));
    holder.setAppInfoBackgroundColour(videosList.get(position));
  }

  @Override public int getItemViewType(int position) {
    if (videosList.get(position) instanceof LoadMoreVideos) {
      return LOADING;
    } else {
      return VIDEO;
    }
  }

  @Override public int getItemCount() {
    return videosList.size();
  }

  public void add(List<Video> videosList) {
    this.videosList = videosList;
    notifyDataSetChanged();
  }

  public void addMore(List<Video> videos) {
    this.videosList.addAll(videos);
    notifyDataSetChanged();
  }

  public void addLoadMore() {
    if (getLoadingPosition() < 0) {
      videosList.add(loadMore);
      notifyItemInserted(videosList.size() - 1);
    }
  }

  public void removeLoadMore() {
    int loadingPosition = getLoadingPosition();
    if (loadingPosition >= 0) {
      videosList.remove(loadingPosition);
      notifyItemRemoved(loadingPosition);
    }
  }

  public synchronized int getLoadingPosition() {
    for (int i = videosList.size() - 1; i >= 0; i--) {
      Video video = videosList.get(i);
      if (video instanceof LoadMoreVideos) {
        return i;
      }
    }
    return -1;
  }
}

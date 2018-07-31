package cm.aptoide.pt.discovery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder> {

  private List<String> videosList;

  public VideoAdapter(List<String> videosList) {
    this.videosList = videosList;
  }

  @Override public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new VideoViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_videos_list_item, parent, false));
  }

  @Override public void onBindViewHolder(VideoViewHolder holder, int position) {
    holder.setContent(videosList.get(position));
  }

  @Override public int getItemCount() {
    return videosList.size();
  }

  public void add(List<String> videosList) {
    this.videosList = videosList;
    notifyDataSetChanged();
  }
}

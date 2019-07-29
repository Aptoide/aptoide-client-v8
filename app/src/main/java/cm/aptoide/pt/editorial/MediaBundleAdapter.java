package cm.aptoide.pt.editorial;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.pt.R;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by D01 on 29/08/2018.
 */

class MediaBundleAdapter extends RecyclerView.Adapter<MediaViewHolder> {
  private List<EditorialMedia> media;
  private PublishSubject<EditorialEvent> uiEventListener;

  public MediaBundleAdapter(List<EditorialMedia> media,
      PublishSubject<EditorialEvent> uiEventListener) {
    this.media = media;
    this.uiEventListener = uiEventListener;
  }

  @Override public MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new MediaViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.media_layout, parent, false), uiEventListener);
  }

  @Override public void onBindViewHolder(MediaViewHolder mediaViewHolder, int position) {
    mediaViewHolder.setVisibility(media.get(position));
  }

  @Override public int getItemCount() {
    return media.size();
  }

  public void add(List<EditorialMedia> media) {
    this.media.addAll(media);
    notifyDataSetChanged();
  }
}

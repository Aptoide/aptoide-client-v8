package cm.aptoide.pt.app.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;
import cm.aptoide.pt.R;

/**
 * Created by D01 on 29/08/2018.
 */

class MediaViewHolder extends RecyclerView.ViewHolder {

  private ImageView image;
  private VideoView video;

  public MediaViewHolder(View view) {
    super(view);
    image = (ImageView) view.findViewById(R.id.image_item);
    video = (VideoView) view.findViewById(R.id.video_item);
  }

  public void setVisibility(EditorialMedia editorialMedia, int position) {

  }
}

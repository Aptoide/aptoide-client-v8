package cm.aptoide.pt.discovery;

import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.MainActivity;

public class VideoViewHolder extends RecyclerView.ViewHolder {

  private VideoView videoContent;
  private TextView textView;

  public VideoViewHolder(View itemView) {
    super(itemView);
    videoContent = (VideoView) itemView.findViewById(R.id.video_viewholder);
    textView = (TextView) itemView.findViewById(R.id.video_desc);
  }

  public void setContent(Video video) {
    videoContent.setVideoPath(video.getVideoUrl());
    videoContent.requestFocus();
    videoContent.start();
    Log.d("Holder", "setContent: started");
  }

  public void setText(Video video){
    textView.setText(video.getVideoDescription());
  }

  /*public void playVideo() {
    videoContent.play();
  }*/
}

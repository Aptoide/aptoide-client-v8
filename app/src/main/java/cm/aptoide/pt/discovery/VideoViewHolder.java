package cm.aptoide.pt.discovery;

import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.MainActivity;

public class VideoViewHolder extends RecyclerView.ViewHolder {

  private VideoView videoContent;
  private TextView appTitle;
  private TextView appScore;
  private ImageView appIcon;

  public VideoViewHolder(View itemView) {
    super(itemView);
    videoContent = (VideoView) itemView.findViewById(R.id.app_video);
    appTitle = (TextView) itemView.findViewById(R.id.app_title);
    appScore = (TextView) itemView.findViewById(R.id.app_score);
    appIcon = (ImageView) itemView.findViewById(R.id.app_icon_discovery);
  }

  public void setContent(Video video) {
    videoContent.setVideoPath(video.getVideoUrl());
    videoContent.requestFocus();
    videoContent.start();
    Log.d("Holder", "setContent: started");
  }

  public void setAppName(Video video){
    appTitle.setText(video.getVideoDescription());
  }

  public void setAppScore(Video video){
    appScore.setText(Double.toString(video.getScore()));
  }

  /*public void setAppIcon(Video video){
    appIcon.setImageBitmap();
  }*/
}

package cm.aptoide.pt.discovery;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.R;

public class VideoViewHolder extends RecyclerView.ViewHolder{

  private TextView videoContent;

  public VideoViewHolder(View itemView) {
    super(itemView);
    videoContent = (TextView) itemView.findViewById(R.id.video_viewholder);
  }

  public void setContent(String content) {
    videoContent.setText(content);
  }
}

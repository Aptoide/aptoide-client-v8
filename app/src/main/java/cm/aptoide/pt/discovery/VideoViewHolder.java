package cm.aptoide.pt.discovery;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import cm.aptoide.pt.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class VideoViewHolder extends RecyclerView.ViewHolder {

  private static final String TAG = "VIDEOVIEWHOLDER";
  private CoordinatorLayout appInfoBackground;
  private Context context;

  private VideoView videoContent;
  private TextView appTitle;
  private TextView appScore;
  private ImageView appIcon;
  private ImageView ratingStar;
  private Button installButton;

  private ImageView testImageView;

  private Palette.Swatch paletteSwatch;

  private Bitmap bitmap;

  public VideoViewHolder(View itemView) {
    super(itemView);
    videoContent = (VideoView) itemView.findViewById(R.id.app_video);
    appTitle = (TextView) itemView.findViewById(R.id.app_title);
    appScore = (TextView) itemView.findViewById(R.id.app_score);
    appIcon = (ImageView) itemView.findViewById(R.id.discovery_app_icon);
    ratingStar = (ImageView) itemView.findViewById(R.id.rating_star);
    appInfoBackground = (CoordinatorLayout) itemView.findViewById(R.id.app_info_bar);
    installButton = (Button) itemView.findViewById(R.id.install_button);

    this.context = itemView.getContext();
  }

  public void setContent(Video video) {
    videoContent.setVideoPath(video.getVideoUrl());
    videoContent.requestFocus();
    videoContent.start();
  }

  public void setAppName(Video video) {
    appTitle.setText(video.getVideoDescription());
  }

  public void setAppScore(Video video) {
    appScore.setText(Double.toString(video.getRating()));
  }

  public void setAppIcon(Video video) {
    Glide.with(this.context)
        .asBitmap()
        .load(video.getImageUrl())
        .into(appIcon);
  }

  public void setAppInfoBackgroundColour(Video video) {

    GlideApp.with(this.context)
        .asBitmap()
        .load(video.getImageUrl())
        .disallowHardwareConfig()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.mipmap.ic_launcher)
        .listener(new RequestListener<Bitmap>() {

          @Override public boolean onLoadFailed(@Nullable GlideException e, Object model,
              Target<Bitmap> target, boolean isFirstResource) {
            return false;
          }

          @Override
          public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
              DataSource dataSource, boolean isFirstResource) {

            if (resource != null) {
              Palette.Swatch p = Palette.from(resource)
                  .generate()
                  .getVibrantSwatch();
              appInfoBackground.setBackgroundColor(p.getRgb());
              installButton.setTextColor(p.getRgb());
            }
            return false;
          }
        })
        .into(appIcon);
  }

  public View getAppBackground() {
    return appInfoBackground;
  }

  public ImageView getAppIcon() {
    return appIcon;
  }
}

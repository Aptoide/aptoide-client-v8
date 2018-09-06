package cm.aptoide.pt.discovery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import cm.aptoide.pt.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import java.util.concurrent.ExecutionException;

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

  public void setAppName(Video video){
    appTitle.setText(video.getVideoDescription());
  }

  public void setAppScore(Video video){
    appScore.setText(Double.toString(video.getRating()));
  }

  public void setAppIcon(Video video){
    Glide.with(this.context).asBitmap().load(video.getImageUrl()).into(appIcon);
  }
  public void setRatingStar(){

    Glide.with(this.context)
        .load("https://png2.kisspng.com/sh/0458cb3c5d15b3ca97372bd4a7c771b1/L0KzQYm3VMA0N6Zpj5H0aYP"
            + "2gLBuTgVvcaVqfJ98dHH3dcS0hf1icZ0ygdDvb4LwccXwjB4ua5DyiNN3eT36eLr7hb10fJJ3RadqY0O0Q7a"
            + "6U8FjOGg9Rqs9NEC7QoiAUcUzOmg4TKIAMUKzQ4e1kP5o/kisspng-united-states-email-informatio"
            + "n-company-white-star-5ac313e331b078.9440827715227340512036.png")
        .into(ratingStar);
  }

  public void setAppInfoBackgroundColour(Video video) {

    GlideApp.with(this.context)
        .asBitmap()
        .load(video.getImageUrl())
        .disallowHardwareConfig()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.mipmap.ic_launcher)
        .listener(new RequestListener<Bitmap>() {

          @Override
          public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
            return false;
          }

          @Override
          public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

            if (resource != null) {
              Palette.Swatch p = Palette.from(resource).generate().getDarkVibrantSwatch();
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

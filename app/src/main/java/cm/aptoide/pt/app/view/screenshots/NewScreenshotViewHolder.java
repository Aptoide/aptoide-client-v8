package cm.aptoide.pt.app.view.screenshots;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.networking.image.ImageLoader;
import java.util.ArrayList;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 08/05/18.
 */

public class NewScreenshotViewHolder extends RecyclerView.ViewHolder {
  @LayoutRes static final int LAYOUT_ID = R.layout.row_item_screenshots_gallery;
  private static final String PORTRAIT = "PORTRAIT";
  private final PublishSubject<ScreenShotClickEvent> screenShotClick;
  private ImageView screenshot;
  private ImageView play_button;
  private FrameLayout media_layout;

  NewScreenshotViewHolder(View itemView, PublishSubject<ScreenShotClickEvent> screenShotClick) {
    super(itemView);
    assignViews(itemView);
    this.screenShotClick = screenShotClick;
  }

  protected void assignViews(View itemView) {
    screenshot = (ImageView) itemView.findViewById(R.id.screenshot_image_item);
    play_button = (ImageView) itemView.findViewById(R.id.play_button);
    media_layout = (FrameLayout) itemView.findViewById(R.id.media_layout);
  }

  public void bindView(GetAppMeta.Media.Video item) {

    final Context context = screenshot.getContext();
    if (context == null) {
      return;
    }

    ImageLoader.with(context)
        .load(item.getThumbnail(), R.drawable.placeholder_square, screenshot);

    if (isLollipopOrHigher()) {
      media_layout.setForeground(context.getResources()
          .getDrawable(R.color.overlay_black, context.getTheme()));
    } else {
      media_layout.setForeground(context.getResources()
          .getDrawable(R.color.overlay_black));
    }

    play_button.setVisibility(View.VISIBLE);

    itemView.setOnClickListener(
        __ -> screenShotClick.onNext(new ScreenShotClickEvent(Uri.parse(item.getUrl()))));
  }

  private boolean isLollipopOrHigher() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
  }

  public void bindView(GetAppMeta.Media.Screenshot item, final int position,
      final ArrayList<String> imagesUris) {

    final Context context = screenshot.getContext();
    if (context == null) {
      return;
    }

    media_layout.setForeground(null);
    play_button.setVisibility(View.GONE);

    ImageLoader.with(context)
        .loadScreenshotToThumb(item.getUrl(), item.getOrientation(),
            getPlaceholder(item.getOrientation()), screenshot);

    itemView.setOnClickListener(
        __ -> screenShotClick.onNext(new ScreenShotClickEvent(imagesUris, position)));
  }

  private int getPlaceholder(String orient) {
    if (viewIsInPortrait(orient)) {
      return R.drawable.placeholder_9_16;
    }
    return R.drawable.placeholder_16_9;
  }

  private boolean viewIsInPortrait(String orient) {
    return !TextUtils.isEmpty(orient) && orient.toUpperCase()
        .equals(PORTRAIT);
  }
}

package cm.aptoide.pt.v8engine.view.app.screenshots;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by gmartinsribeiro on 01/12/15.
 *
 * code migrated from v7
 */
public class ScreenshotsAdapter
    extends RecyclerView.Adapter<ScreenshotsAdapter.ScreenshotsViewHolder> {

  private final List<GetAppMeta.Media.Video> videos;
  private final List<GetAppMeta.Media.Screenshot> screenshots;
  private final ArrayList<String> imageUris;
  private final PublishSubject<ScreenShotClickEvent> screenShotClick;

  public ScreenshotsAdapter(List<GetAppMeta.Media.Video> videos,
      List<GetAppMeta.Media.Screenshot> screenshots) {
    this.videos = videos;
    this.screenshots = screenshots;
    this.screenShotClick = PublishSubject.create();

    imageUris = new ArrayList<>(screenshots.size());
    for (GetAppMeta.Media.Screenshot screenshot : screenshots) {
      imageUris.add(screenshot.getUrl());
    }
  }

  public Observable<ScreenShotClickEvent> getScreenShotClick() {
    return screenShotClick;
  }

  @Override public ScreenshotsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View inflate = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.row_item_screenshots_gallery, parent, false);

    return new ScreenshotsViewHolder(inflate, screenShotClick);
  }

  @Override public void onBindViewHolder(ScreenshotsViewHolder holder, int position) {
    if (isVideo(position)) {
      holder.bindView(videos.get(position));
      return;
    }

    if (isScreenShot(position)) {
      int videoListOffset = videos != null ? videos.size() : 0;
      holder.bindView(screenshots.get(position), position - videoListOffset, imageUris);
    }
  }

  @Override public int getItemViewType(int position) {
    return super.getItemViewType(position);
  }

  @Override public int getItemCount() {
    return (videos != null ? videos.size() : 0) + (screenshots != null ? screenshots.size() : 0);
  }

  private boolean isScreenShot(int position) {
    return screenshots != null && position < screenshots.size();
  }

  private boolean isVideo(int position) {
    return videos != null && position < videos.size();
  }

  static class ScreenshotsViewHolder extends RecyclerView.ViewHolder {

    private static final String PORTRAIT = "PORTRAIT";

    private final PublishSubject<ScreenShotClickEvent> screenShotClick;
    private ImageView screenshot;
    private ImageView play_button;
    private FrameLayout media_layout;

    ScreenshotsViewHolder(View itemView, PublishSubject<ScreenShotClickEvent> screenShotClick) {
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

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        media_layout.setForeground(context.getResources()
            .getDrawable(R.color.overlay_black, context.getTheme()));
      } else {
        media_layout.setForeground(context.getResources()
            .getDrawable(R.color.overlay_black));
      }

      play_button.setVisibility(View.VISIBLE);

      itemView.setOnClickListener(v -> {
        screenShotClick.onNext(new ScreenShotClickEvent(Uri.parse(item.getUrl())));
      });
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

      itemView.setOnClickListener(v -> {
        screenShotClick.onNext(new ScreenShotClickEvent(imagesUris, position));
      });
    }

    private int getPlaceholder(String orient) {
      if (orient != null && orient.equals(PORTRAIT)) {
        return R.drawable.placeholder_9_16;
      }
      return R.drawable.placeholder_16_9;
    }
  }
}

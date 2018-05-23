package cm.aptoide.pt.app.view.screenshots;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.view.app.AppScreenshot;
import cm.aptoide.pt.view.app.AppVideo;
import java.util.ArrayList;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 08/05/18.
 */

public class NewScreenshotsAdapter extends RecyclerView.Adapter<NewScreenshotViewHolder> {

  private List<AppVideo> videos;
  private List<AppScreenshot> screenshots;
  private ArrayList<String> imageUris;
  private PublishSubject<ScreenShotClickEvent> screenShotClick;

  public NewScreenshotsAdapter(List<AppScreenshot> screenshots, List<AppVideo> videos,
      PublishSubject<ScreenShotClickEvent> screenShotClick) {
    this.screenshots = screenshots;
    this.videos = videos;
    this.screenShotClick = screenShotClick;
  }

  @Override public NewScreenshotViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(viewGroup.getContext())
        .inflate(NewScreenshotViewHolder.LAYOUT_ID, viewGroup, false);
    return new NewScreenshotViewHolder(view, screenShotClick);
  }

  @Override public void onBindViewHolder(NewScreenshotViewHolder holder, int position) {
    if (isVideo(position)) {
      holder.bindView(videos.get(position));
      return;
    }

    position -= (videos != null) ? videos.size() : 0;
    if (isScreenShot(position)) {
      holder.bindView(screenshots.get(position), position, imageUris);
    }
  }

  @Override public int getItemCount() {
    return (videos != null ? videos.size() : 0) + (screenshots != null ? screenshots.size() : 0);
  }

  public void updateScreenshots(List<AppScreenshot> screenshots) {
    this.screenshots = screenshots;
    imageUris = new ArrayList<>(screenshots.size());
    for (AppScreenshot screenshot : screenshots) {
      imageUris.add(screenshot.getUrl());
    }
    notifyDataSetChanged();
  }

  public void updateVideos(List<AppVideo> videos) {
    this.videos = videos;
    notifyDataSetChanged();
  }

  private boolean isScreenShot(int position) {
    return screenshots != null && position < screenshots.size();
  }

  private boolean isVideo(int position) {
    return videos != null && position < videos.size();
  }
}

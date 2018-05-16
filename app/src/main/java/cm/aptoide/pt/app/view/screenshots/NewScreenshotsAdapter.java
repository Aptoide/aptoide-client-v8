package cm.aptoide.pt.app.view.screenshots;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import java.util.ArrayList;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 08/05/18.
 */

public class NewScreenshotsAdapter extends RecyclerView.Adapter<NewScreenshotViewHolder> {

  private List<GetAppMeta.Media.Video> videos;
  private List<GetAppMeta.Media.Screenshot> screenshots;
  private ArrayList<String> imageUris;
  private PublishSubject<ScreenShotClickEvent> screenShotClick;

  public NewScreenshotsAdapter(List<GetAppMeta.Media.Screenshot> screenshots,
      List<GetAppMeta.Media.Video> videos, PublishSubject<ScreenShotClickEvent> screenShotClick) {
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

  public void updateScreenshots(List<GetAppMeta.Media.Screenshot> screenshots) {
    this.screenshots = screenshots;
    imageUris = new ArrayList<>(screenshots.size());
    for (GetAppMeta.Media.Screenshot screenshot : screenshots) {
      imageUris.add(screenshot.getUrl());
    }
    notifyDataSetChanged();
  }

  public void updateVideos(List<GetAppMeta.Media.Video> videos) {
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

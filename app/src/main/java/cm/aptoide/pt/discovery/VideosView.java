package cm.aptoide.pt.discovery;

import cm.aptoide.pt.presenter.View;
import java.util.List;

public interface VideosView extends View {

  void showVideos(List<Video> videos);
}

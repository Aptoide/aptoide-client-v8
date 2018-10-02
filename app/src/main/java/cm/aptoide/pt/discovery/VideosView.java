package cm.aptoide.pt.discovery;

import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

public interface VideosView extends View {

  void showVideos(List<Video> videos);

  void showMoreVideos(List<Video> videos);

  Observable<Object> reachesBottom();
}

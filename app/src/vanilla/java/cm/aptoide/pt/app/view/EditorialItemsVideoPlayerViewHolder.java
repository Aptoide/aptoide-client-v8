package cm.aptoide.pt.app.view;

import android.view.View;
import cm.aptoide.pt.R;
import cm.aptoide.pt.VanillaApplication;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.playerUtils.FullScreenHelper;
import java.text.DecimalFormat;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 03/10/2018.
 */

public class EditorialItemsVideoPlayerViewHolder extends EditorialItemsViewHolder {

  private final YouTubePlayerView youTubePlayerView;
  private final FullScreenHelper fullScreenHelper;
  private final Observable<cm.aptoide.pt.presenter.View.LifecycleEvent> lifecycle;

  EditorialItemsVideoPlayerViewHolder(View view, DecimalFormat oneDecimalFormat,
      PublishSubject<EditorialEvent> uiEventListener) {
    super(view, oneDecimalFormat, uiEventListener);
    youTubePlayerView = view.findViewById(R.id.youtube_player_view);
    fullScreenHelper = new FullScreenHelper();
    lifecycle = ((VanillaApplication) view.getContext()
        .getApplicationContext()).getLifecycleEvent();
  }

  void handleVideo(EditorialMedia editorialMedia) {
    //initPlayerMenu();
    //
    //youTubePlayerView.initialize(youTubePlayer -> {
    //  getLifecycle().addObserver(youTubePlayerView);
    //  youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
    //    @Override public void onReady() {
    //      loadVideo(youTubePlayer, parseUrl(editorialMedia.getUrl()));
    //    }
    //  });
    //
    //  addFullScreenListenerToPlayer(youTubePlayer);
    //  setPlayNextVideoButtonClickListener(youTubePlayer);
    //}, true);
  }

  private void initPlayerMenu() {
    //youTubePlayerView.getPlayerUIController()
    //    .showMenuButton(true);
    //if (youTubePlayerView.getPlayerUIController()
    //    .getMenu() != null) {
    //  youTubePlayerView.getPlayerUIController()
    //      .getMenu()
    //      .addItem(new MenuItem("example", R.drawable.ic_action_settings_light,
    //          (view) -> youTubePlayerView.getPlayerUIController()
    //              .getMenu()
    //              .dismiss()));
    //}
  }

  private void loadVideo(YouTubePlayer youTubePlayer, String videoId) {
    // if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
    //   youTubePlayer.loadVideo(videoId, 0);
    // } else {
    //   youTubePlayer.cueVideo(videoId, 0);
    // }
    //
    // setVideoTitle(youTubePlayerView.getPlayerUIController(), videoId);
  }

  private void handleLifecycleEvents(YouTubePlayer youTubePlayer, String videoId) {
    lifecycle.flatMap(event -> {
      switch (event) {
        case PAUSE:
          youTubePlayer.pause();
          break;
        case DESTROY:
          youTubePlayerView.release();
          break;
        case RESUME:
          youTubePlayer.loadVideo(videoId, 0);
      }
      return null;
    })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe();
  }
}

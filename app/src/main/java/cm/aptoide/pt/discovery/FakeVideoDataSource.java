package cm.aptoide.pt.discovery;

import cm.aptoide.pt.R;
import java.util.ArrayList;
import java.util.List;
import rx.Single;

public class FakeVideoDataSource implements VideoDataSource {

  @Override public Single<VideosList> loadFreshVideos(int limit) {
    return getVideos();
  }

  @Override public Single<VideosList> loadNextVideos(int offset, int limit) {
    return loadFreshVideos(limit);
  }

  @Override public boolean hasMore(Integer offset, String title) {
    return true;
  }

  private Single<VideosList> getVideos() {
    return getFakeVideos();
  }

  public Single<VideosList> getFakeVideos() {
    List<Video> videoList = new ArrayList<>();

    String imgUrl1 =
        "https://lh3.googleusercontent.com/G0Kaa6WRog41pCTPQKlPIgLOiEl-RpmVDrUQySGMT3o9D"
            + "wOl4SSLENgEVTQIRQv9QQF-=s180-rw";
    String imgUrl2 =
        "https://lh3.googleusercontent.com/rFmTpCoFfUraFMJA1oVj3IzgHMcadJYn3C_H2FTTrgHVB"
            + "RVoyqXPsyX6uCzRmduxtaI=s180-rw";
    String imgUrl3 =
        "http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png";

    String videoUrl1 = "android.resource://cm.aptoide.pt.discovery/" + R.raw.video1;
    String videoUrl2 = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
    String videoUrl3 = "http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4";

    Video zombsAPP = new Video(videoUrl1, "Game #1", 4.2, imgUrl1, "non_streaming");
    videoList.add(zombsAPP);

    Video subwayAPP = new Video(videoUrl2, "Game #2", 3.9, imgUrl2, "non_streaming");
    videoList.add(subwayAPP);

    Video androidAPP = new Video(videoUrl3, "Game #3", 4.0, imgUrl3, "non_streaming");
    videoList.add(androidAPP);

    return Single.just(new VideosList(videoList, 0));
  }
}

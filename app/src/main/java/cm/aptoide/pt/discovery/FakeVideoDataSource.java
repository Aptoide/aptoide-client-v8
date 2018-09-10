package cm.aptoide.pt.discovery;

import java.util.ArrayList;
import java.util.List;

public class FakeVideoDataSource implements VideoDataSource {

  @Override
  public List<Video> loadFreshVideos(String key) {
    return getVideos();
  }

  @Override
  public List<Video> loadNextVideos(int offset, int limit, String key) {
    return loadFreshVideos(key);
  }

  @Override
  public boolean hasMore(Integer offset, String title) {
    return true;
  }


  private List<Video> getVideos() {
    return getFakeVideos();
  }

  public List<Video> getFakeVideos() {
    List<Video> videoList = new ArrayList<>();

    String imgUrl1="https://lh3.googleusercontent.com/G0Kaa6WRog41pCTPQKlPIgLOiEl-RpmVDrUQySGMT3o9DwOl4SSLENgEVTQIRQv9QQF-=s180-rw";
    String imgUrl2="https://lh3.googleusercontent.com/rFmTpCoFfUraFMJA1oVj3IzgHMcadJYn3C_H2FTTrgHVBRVoyqXPsyX6uCzRmduxtaI=s180-rw";
    String imgUrl3="http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png";

    String videoUrl1="https://www.demonuts.com/Demonuts/smallvideo.mp4";
    String videoUrl2="rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
    String videoUrl3="http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4";



    Video zombsAPP = new Video(videoUrl1, "Game #1",  4.2, imgUrl1);
    videoList.add(zombsAPP);

    Video subwayAPP = new Video(videoUrl2, "Game #2",  3.9, imgUrl2);
    videoList.add(subwayAPP);

    Video androidAPP = new Video(videoUrl3, "Game #3",  4.0, imgUrl3);
    videoList.add(androidAPP);

    for(int i = 0; i < 3; i++){
      videoList.add(zombsAPP);
      videoList.add(subwayAPP);
      videoList.add(androidAPP);
    }

    return videoList;
  }
}

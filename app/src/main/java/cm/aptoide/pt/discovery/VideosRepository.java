package cm.aptoide.pt.discovery;

import android.net.Uri;
import cm.aptoide.pt.R;
import java.util.ArrayList;
import java.util.List;

public class VideosRepository {

  public VideosRepository() {
    // TODO: 31/07/2018  
  }

  public List<Video> getVideos(){
    List<Video> videosList = new ArrayList<>();
    videosList.add(new Video("https://www.demonuts.com/Demonuts/smallvideo.mp4", "Game #1 - The Prequel", 3.2, "https://lh3.googleusercontent.com/G0Kaa6WRog41pCTPQKlPIgLOiEl-RpmVDrUQySGMT3o9DwOl4SSLENgEVTQIRQv9QQF-=s180-rw"));
    videosList.add(new Video("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov", "Game #2 - Big Buck Bunny", 4.7, "https://lh3.googleusercontent.com/rFmTpCoFfUraFMJA1oVj3IzgHMcadJYn3C_H2FTTrgHVBRVoyqXPsyX6uCzRmduxtaI=s180-rw"));
    videosList.add(new Video("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4", "Game #3 - Workout num. 479", 1.8, "http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png"));

    return videosList;
  }
}

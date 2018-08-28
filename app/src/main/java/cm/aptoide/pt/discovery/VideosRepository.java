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
    videosList.add(new Video("https://www.demonuts.com/Demonuts/smallvideo.mp4", "Haha yes!"));
    videosList.add(new Video("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov", "Okay, no"));
    videosList.add(new Video("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4", "This is a meme"));


    return videosList;
  }
}

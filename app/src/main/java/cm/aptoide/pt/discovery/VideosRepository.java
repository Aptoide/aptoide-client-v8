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
    videosList.add(new Video("https://www.demonuts.com/Demonuts/smallvideo.mp4", "Game #1 - The Prequel", 3.2));
    videosList.add(new Video("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov", "Game #2 - Big Buck Bunny", 4.7));
    videosList.add(new Video("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4", "Game #3 - Workout num. 479", 1.8));

    //videosList.add(new Video("https://www.demonuts.com/Demonuts/smallvideo.mp4", "Game #1 - The Prequel", 3.2));
    //videosList.add(new Video("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov", "Game #2 - Haha yes!", 4.7));
    //videosList.add(new Video("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4", "Game #3 - Exercise fam!", 1.8));

    return videosList;
  }
}

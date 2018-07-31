package cm.aptoide.pt.discovery;

import java.util.ArrayList;
import java.util.List;

public class VideosRepository {

  public VideosRepository() {
    // TODO: 31/07/2018  
  }

  public List<String> getVideos(){
    List<String> videosList = new ArrayList<>();
    for(int i = 1; i <= 10; i++){
      String element = "Element nr.: " + i;
      videosList.add(element);
    }
    return videosList;
  }
}

package cm.aptoide.pt.discovery;

import java.util.List;

interface VideosContract {


  interface View {

    void showVideos(List<Video> videos);

  }

  interface UserActionListener {

    void present();

  }

}

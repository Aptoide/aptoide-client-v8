package cm.aptoide.pt.discovery;

import java.util.List;

interface VideosContract {


  interface View {

    void showVideos(List<Video> videos);

    //void playVideos();

  }

  interface UserActionListener {

    void present();

  }

}

package cm.aptoide.pt.discovery;

import java.util.List;

interface VideosContract {


  interface View {

    void showVideos(List<String> videos);

  }

  interface UserActionListener {

    void present();

  }

}

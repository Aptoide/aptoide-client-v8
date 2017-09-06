package cm.aptoide.pt.spotandshareapp;

import android.net.Uri;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareAvatar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by filipe on 07-08-2017.
 */

public class SpotAndShareLocalAvatarsProvider {

  private String packageName;

  public SpotAndShareLocalAvatarsProvider(String packageName) {
    this.packageName = packageName;
  }

  public List<SpotAndShareAvatar> getAvailableAvatars() {
    List<SpotAndShareAvatar> spotAndShareAvatars = new LinkedList<>();

    spotAndShareAvatars.add(new SpotAndShareLocalAvatar(0,
        Uri.parse("android.resource://" + packageName + "/drawable/spotandshare_avatar_01")
            .toString(), false));

    spotAndShareAvatars.add(new SpotAndShareLocalAvatar(1,
        Uri.parse("android.resource://" + packageName + "/drawable/spotandshare_avatar_02")
            .toString(), false));

    spotAndShareAvatars.add(new SpotAndShareLocalAvatar(2,
        Uri.parse("android.resource://" + packageName + "/drawable/spotandshare_avatar_03")
            .toString(), false));

    spotAndShareAvatars.add(new SpotAndShareLocalAvatar(3,
        Uri.parse("android.resource://" + packageName + "/drawable/spotandshare_avatar_04")
            .toString(), false));
    return spotAndShareAvatars;
  }
}

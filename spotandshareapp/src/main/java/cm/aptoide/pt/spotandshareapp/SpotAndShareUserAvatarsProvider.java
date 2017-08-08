package cm.aptoide.pt.spotandshareapp;

import android.net.Uri;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareAvatar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by filipe on 07-08-2017.
 */

public class SpotAndShareUserAvatarsProvider {

  public List<SpotAndShareAvatar> getAvailableAvatars() {
    List<SpotAndShareAvatar> spotAndShareAvatars = new LinkedList<>();

    spotAndShareAvatars.add(new SpotAndShareUserAvatar(1,
        Uri.parse("android.resource://spotandshareapp.dev/drawable/spotandshare_avatar_01")
            .toString()));

    spotAndShareAvatars.add(new SpotAndShareUserAvatar(2,
        Uri.parse("android.resource://spotandshareapp.dev/drawable/spotandshare_avatar_02")
            .toString()));

    spotAndShareAvatars.add(new SpotAndShareUserAvatar(3,
        Uri.parse("android.resource://spotandshareapp.dev/drawable/spotandshare_avatar_03")
            .toString()));

    spotAndShareAvatars.add(new SpotAndShareUserAvatar(4,
        Uri.parse("android.resource://spotandshareapp.dev/drawable/spotandshare_avatar_04")
            .toString()));
    return spotAndShareAvatars;
  }
}

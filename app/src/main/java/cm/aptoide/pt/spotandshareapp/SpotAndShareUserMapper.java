package cm.aptoide.pt.spotandshareapp;

import android.net.Uri;
import cm.aptoide.pt.spotandshare.socket.entities.Friend;

/**
 * Created by filipe on 06-09-2017.
 */

public class SpotAndShareUserMapper {

  private DrawableBitmapMapper drawableBitmapMapper;

  public SpotAndShareUserMapper(DrawableBitmapMapper drawableBitmapMapper) {
    this.drawableBitmapMapper = drawableBitmapMapper;
  }

  public SpotAndShareUser getSpotAndShareUser(Friend friend) {
    return new SpotAndShareUser(friend.getUsername(),
        drawableBitmapMapper.convertBitmapToDrawable(friend.getAvatar()));
  }

  public SpotAndShareUser getSpotAndShareUser(SpotAndShareLocalUser localUser) {
    return new SpotAndShareUser(localUser.getUsername(), drawableBitmapMapper.convertUriToDrawable(
        Uri.parse(localUser.getAvatar()
            .getString())));
  }
}

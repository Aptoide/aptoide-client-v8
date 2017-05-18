package cm.aptoide.pt.spotandshareandroid;

import android.net.Uri;
import java.util.ArrayList;

/**
 * Created by Filipe on 12-02-2017.
 */

public class OutsideShareManager {

  private ArrayList<String> pathsFromOutsideShare;

  public OutsideShareManager() {
    pathsFromOutsideShare = new ArrayList<String>();
  }

  public void getApp(Uri uri) {
    String way = uri.getPath();
    pathsFromOutsideShare.add(way);
  }

  public void getMultipleApps(ArrayList<Uri> appsUriList) {
    for (int i = 0; i < appsUriList.size(); i++) {
      String way = appsUriList.get(i)
          .getPath();
      pathsFromOutsideShare.add(way);
    }
  }

  public ArrayList<String> getPathsFromOutsideShare() {
    return pathsFromOutsideShare;
  }
}

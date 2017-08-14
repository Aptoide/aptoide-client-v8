package cm.aptoide.pt.v8engine.view.app.screenshots;

import android.net.Uri;
import java.util.ArrayList;

public class ScreenShotClickEvent {
  private final ArrayList<String> imagesUris;
  private final int index;
  private final Uri uri;

  public ScreenShotClickEvent(ArrayList<String> imagesUris, int index) {
    this.imagesUris = imagesUris;
    this.index = index;
    this.uri = Uri.EMPTY;
  }

  public ScreenShotClickEvent(Uri uri) {
    imagesUris = new ArrayList<>();
    this.index = -1;
    this.uri = uri;
  }

  public ArrayList<String> getImagesUris() {
    return imagesUris;
  }

  public int getImagesIndex() {
    return index;
  }

  public Uri getUri() {
    return uri;
  }

  public boolean isVideo() {
    return uri != Uri.EMPTY;
  }
}

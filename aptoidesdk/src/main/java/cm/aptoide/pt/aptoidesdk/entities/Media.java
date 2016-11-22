package cm.aptoide.pt.aptoidesdk.entities;

import android.support.annotation.Nullable;
import cm.aptoide.pt.aptoidesdk.misc.Orientation;
import cm.aptoide.pt.model.v7.GetAppMeta;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by neuro on 22-11-2016.
 */
@Data @AllArgsConstructor public class Media {

  private final String description;
  private final List<Screenshot> screenshots;

  public static Media fromGetAppMetaMedia(GetAppMeta.Media getAppMetaMedia) {

    List<Screenshot> screenshots = new LinkedList<>();
    String description = getAppMetaMedia.getDescription();

    if (getAppMetaMedia.getScreenshots() != null) {
      for (GetAppMeta.Media.Screenshot screenshot : getAppMetaMedia.getScreenshots()) {
        screenshots.add(Screenshot.fromGetAppMetaMediaScreenshot(screenshot));
      }
    }

    return new Media(description, screenshots);
  }

  @Data public static class Screenshot {

    private final String url;
    private final int height;
    private final int width;

    public @Nullable static Screenshot fromGetAppMetaMediaScreenshot(
        GetAppMeta.Media.Screenshot getAppMetaMediaScreenshot) {

      if (getAppMetaMediaScreenshot != null) {

        String url = getAppMetaMediaScreenshot.getUrl();
        int height = getAppMetaMediaScreenshot.getHeight();
        int width = getAppMetaMediaScreenshot.getWidth();

        return new Screenshot(url, height, width);
      }

      return null;
    }

    public Orientation getOrientation() {
      return Orientation.getOrientation(height, width);
    }
  }
}

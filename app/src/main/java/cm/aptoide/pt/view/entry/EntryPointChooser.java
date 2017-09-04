package cm.aptoide.pt.view.entry;

import android.app.Activity;
import cm.aptoide.pt.view.MainActivity;
import cm.aptoide.pt.view.OpenGLES20Activity;

/**
 * Created by neuro on 15-05-2017.
 */

public class EntryPointChooser {

  private final SupportedExtensions supportedExtensions;

  public EntryPointChooser(SupportedExtensions supportedExtensions) {
    this.supportedExtensions = supportedExtensions;
  }

  public Class<? extends Activity> getEntryPoint() {
    if (!supportedExtensions.isDefined()) {
      return OpenGLES20Activity.class;
    } else {
      return MainActivity.class;
    }
  }
}

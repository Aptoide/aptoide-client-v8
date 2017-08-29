package cm.aptoide.pt.view.entry;

import android.app.Activity;
import cm.aptoide.pt.view.OpenGLES20Activity;
import cm.aptoide.pt.view.PartnersLaunchView;

/**
 * Created by diogoloureiro on 11/08/2017.
 *
 * Defines the entry point for the Entry Activity to launch
 */

public class EntryPointChooser {

  private final SupportedExtensions supportedExtensions;

  public EntryPointChooser(SupportedExtensions supportedExtensions) {
    this.supportedExtensions = supportedExtensions;
  }

  /**
   * Case the supported extensions aren't defined, start OpenGLES20Activity
   * else start PartnersLaunchView
   *
   * @return entry activity
   */
  Class<? extends Activity> getEntryPoint() {
    if (!supportedExtensions.isDefined()) {
      return OpenGLES20Activity.class;
    } else {
      return PartnersLaunchView.class;
    }
  }
}
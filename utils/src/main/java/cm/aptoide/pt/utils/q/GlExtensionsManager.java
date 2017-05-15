package cm.aptoide.pt.utils.q;

import cm.aptoide.pt.utils.Invalidate;
import java.util.Arrays;
import lombok.Getter;

/**
 * Created by neuro on 12-05-2017.
 */

class GlExtensionsManager implements Invalidate {

  @Getter private static final GlExtensionsManager instance = new GlExtensionsManager();

  private final String[] supportedOpenGLExtensions = {
      "GL_OES_compressed_ETC1_RGB8_texture", "GL_OES_compressed_paletted_texture",
      "GL_AMD_compressed_3DC_texture", "GL_AMD_compressed_ATC_texture",
      "GL_EXT_texture_compression_latc", "GL_EXT_texture_compression_dxt1",
      "GL_EXT_texture_compression_s3tc", "GL_ATI_texture_compression_atitc",
      "GL_IMG_texture_compression_pvrtc"
  };
  private String openGLExtensions = "";

  private String computedValue;

  private GlExtensionsManager() {
  }

  private String computeOpenGLExtensions() {
    String glExtensions = "";

    boolean extensionAdded = false;
    for (String extension : openGLExtensions.split(" ")) {
      if (Arrays.asList(supportedOpenGLExtensions)
          .contains(extension)) {
        if (extensionAdded) {
          glExtensions += ",";
        }

        glExtensions += extension;
        extensionAdded = true;
      }
    }
    return glExtensions;
  }

  void setOpenGLExtensions(String openGLExtensions) {
    this.openGLExtensions = openGLExtensions;
    invalidate();
  }

  @Override public void invalidate() {
    computedValue = null;
  }

  public String get() {
    if (computedValue == null) {
      computedValue = computeOpenGLExtensions();
    }

    return computedValue;
  }
}

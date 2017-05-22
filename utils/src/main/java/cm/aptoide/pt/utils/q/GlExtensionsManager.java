package cm.aptoide.pt.utils.q;

import android.content.SharedPreferences;
import cm.aptoide.pt.logger.Logger;
import java.util.Arrays;

import static android.content.ContentValues.TAG;
import static cm.aptoide.pt.utils.q.GlExtensionsManager.Keys.GL_TEXTURES;
import static cm.aptoide.pt.utils.q.GlExtensionsManager.Keys.GL_TEXTURES_DEFINED;

/**
 * Created by neuro on 12-05-2017.
 */

class GlExtensionsManager {

  private final SharedPreferences sharedPreferences;

  private final String[] openGLExtensions = {
      "GL_OES_compressed_ETC1_RGB8_texture", "GL_OES_compressed_paletted_texture",
      "GL_AMD_compressed_3DC_texture", "GL_AMD_compressed_ATC_texture",
      "GL_EXT_texture_compression_latc", "GL_EXT_texture_compression_dxt1",
      "GL_EXT_texture_compression_s3tc", "GL_ATI_texture_compression_atitc",
      "GL_IMG_texture_compression_pvrtc"
  };
  private String supportedOpenGLExtensions = "";

  private String computedValue;

  GlExtensionsManager(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  private String computeSupportedOpenGLExtensions() {
    String glExtensions = "";

    boolean extensionAdded = false;
    for (String extension : supportedOpenGLExtensions.split(" ")) {
      if (Arrays.asList(openGLExtensions)
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

  void setSupportedOpenGLExtensions(String SupportedOpenGLExtensions) {
    this.supportedOpenGLExtensions = SupportedOpenGLExtensions;
    setSetoredSupportedOpenGLExtensions(computedValue = computeSupportedOpenGLExtensions());
  }

  String getSupportedExtensions() {
    if (isGLTextutesStored()) {
      computedValue = getStoredGlTextures();
    }

    if (computedValue == null || "".equals(computedValue)) {
      Logger.w(TAG, "Supported OpenGL Extensions is empty!");
    }

    return computedValue != null ? computedValue : "";
  }

  boolean isSupportedExtensionsDefined() {
    return !"".equals(getSupportedExtensions());
  }

  private boolean isGLTextutesStored() {
    return sharedPreferences.getBoolean(GL_TEXTURES_DEFINED, false);
  }

  private String getStoredGlTextures() {
    return sharedPreferences.getString(GL_TEXTURES, null);
  }

  private void setSetoredSupportedOpenGLExtensions(String glTextures) {
    sharedPreferences.edit()
        .putString(GL_TEXTURES, glTextures)
        .putBoolean(GL_TEXTURES_DEFINED, true)
        .apply();
  }

  class Keys {
    static final String GL_TEXTURES = "glTextures";
    static final String GL_TEXTURES_DEFINED = "glTexturesDefined";
  }
}

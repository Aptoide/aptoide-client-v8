package cm.aptoide.pt.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.utils.q.QManager;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jdandrade on 14/11/2016.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

  private final QManager qManager;
  private final Context context;

  public MyGLRenderer(Context context, QManager qManager) {
    this.context = context;
    this.qManager = qManager;
  }

  @Override public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    qManager.setSupportedOpenGLExtensions(GLES20.glGetString(GLES20.GL_EXTENSIONS));
    Intent intent = new Intent(context, AptoideApplication.getActivityProvider()
        .getMainActivityFragmentClass());
    ((Activity) context).finish();
    context.startActivity(intent);
  }

  @Override public void onSurfaceChanged(GL10 gl, int width, int height) {

  }

  @Override public void onDrawFrame(GL10 gl) {

  }
}

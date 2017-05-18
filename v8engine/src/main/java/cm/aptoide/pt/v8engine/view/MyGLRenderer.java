package cm.aptoide.pt.v8engine.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.v8engine.V8Engine;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jdandrade on 14/11/2016.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

  private final QManager qManager;
  Context context;

  public MyGLRenderer(Context context) {
    this.context = context;

    qManager = V8Engine.getQManager();
  }

  @Override public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    qManager.setSupportedOpenGLExtensions(GLES20.glGetString(GLES20.GL_EXTENSIONS));
    Intent intent = new Intent(context, MainActivity.class);
    ((Activity) context).finish();
    context.startActivity(intent);
  }

  @Override public void onSurfaceChanged(GL10 gl, int width, int height) {

  }

  @Override public void onDrawFrame(GL10 gl) {

  }
}

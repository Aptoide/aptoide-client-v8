package cm.aptoide.pt.v8engine.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

/**
 * Created by jdandrade on 14/11/2016.
 */
public class MyGLSurfaceView extends GLSurfaceView {
  private final MyGLRenderer myGLRenderer;

  public MyGLSurfaceView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
    setZOrderOnTop(true);
    SurfaceHolder surfaceHolder = getHolder();
    surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
  }

  public MyGLSurfaceView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs);
    setEGLContextClientVersion(2);

    myGLRenderer = new MyGLRenderer(context);
    setRenderer(myGLRenderer);
  }
}

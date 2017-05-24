package cm.aptoide.pt.spotandshareandroid;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by filipegoncalves on 29-07-2016.
 */
public class CustomImageView extends ImageView {

  public CustomImageView(Context context) {
    super(context);
  }

  public CustomImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
  }
}

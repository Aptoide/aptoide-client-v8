package cm.aptoide.pt.v8engine.view;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LockedBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {

  public LockedBottomSheetBehavior() {
    super();
  }

  public LockedBottomSheetBehavior(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
    return false;
  }

  @Override public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
    return false;
  }

  @Override public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child,
      View directTargetChild, View target, int nestedScrollAxes) {
    return false;
  }

  @Override
  public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx,
      int dy, int[] consumed) {
  }

  @Override
  public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
  }

  @Override
  public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V child, View target,
      float velocityX, float velocityY) {
    return false;
  }
}

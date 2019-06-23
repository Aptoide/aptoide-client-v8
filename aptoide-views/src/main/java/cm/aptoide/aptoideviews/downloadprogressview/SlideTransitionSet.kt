package cm.aptoide.aptoideviews.downloadprogressview

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.transition.ChangeBounds
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionSet
import android.util.AttributeSet
import android.view.Gravity

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@TargetApi(Build.VERSION_CODES.KITKAT)
class SlideTransitionSet(context: Context, attrs: AttributeSet?) : TransitionSet() {

  init {
    ordering = TransitionSet.ORDERING_TOGETHER
    addTransition(Fade(Fade.OUT))
        .addTransition(Slide(Gravity.LEFT))
        .addTransition(ChangeBounds())
        .addTransition(Fade(Fade.IN))
  }

}
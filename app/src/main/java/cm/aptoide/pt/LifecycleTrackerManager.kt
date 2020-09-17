package cm.aptoide.pt

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

class LifecycleTrackerManager() {

  private var isAppInBackground: Boolean = false

  private val appLifecycleListener = object : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
      isAppInBackground = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
      isAppInBackground = true

    }
  }

  fun initialize() {
    ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleListener)
  }

  fun isAppInBackground(): Boolean {
    return isAppInBackground
  }

}
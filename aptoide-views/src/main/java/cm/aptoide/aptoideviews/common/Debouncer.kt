package cm.aptoide.aptoideviews.common

class Debouncer(val delayMs: Long) {

  private var lastClickTime: Long = 0

  fun execute(function: () -> Unit){
    val now = System.currentTimeMillis()
    val before = lastClickTime

    if(now - before > delayMs){
      lastClickTime = now
      function()
    }
  }

  fun reset(){
    lastClickTime = System.currentTimeMillis()
  }
}
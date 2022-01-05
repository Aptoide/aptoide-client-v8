package cm.aptoide.pt.search.view

import android.os.Handler
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class DiffUtilAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
  private val pendingUpdates: Queue<DiffRequest<T>> = ArrayDeque()

  var diffUtilThread: Thread? = Thread()
  val handler: Handler = Handler()

  fun applyDiffUtil(diffRequest: DiffRequest<T>) {
    pendingUpdates.add(diffRequest)
    if (pendingUpdates.size > 1) {
      return
    }
    internalApplyDiffUtil(diffRequest)
  }

  private fun internalApplyDiffUtil(diffRequest: DiffRequest<T>) {
    diffUtilThread = Thread(Runnable {
      val result = DiffUtil.calculateDiff(diffRequest.diffCallback)
      handler.post {
        pendingUpdates.remove()
        dispatchUpdates(diffRequest.newItems, result)
        if (pendingUpdates.size > 0) {
          internalApplyDiffUtil(pendingUpdates.peek())
        }
      }
    })
    diffUtilThread?.start()
  }

  data class DiffRequest<T>(val newItems: List<T>, val diffCallback: DiffUtil.Callback)

  abstract fun dispatchUpdates(newItems: List<T>, diffResult: DiffUtil.DiffResult)
}
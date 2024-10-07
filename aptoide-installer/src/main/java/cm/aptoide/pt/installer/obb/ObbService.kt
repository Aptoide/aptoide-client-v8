package cm.aptoide.pt.installer.obb

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import cm.aptoide.pt.installer.obb.ObbService.Companion.OBB_MOVE_FAIL
import cm.aptoide.pt.installer.obb.ObbService.Companion.OBB_MOVE_SUCCESS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

internal interface OBBServiceCallback {
  fun onSuccess()
  fun onError()
}

internal class ObbService : Service() {
  override fun onBind(intent: Intent): IBinder {
    return Messenger(OBBDataHandler()).binder
  }

  private class OBBDataHandler : Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message) {
      val replyMessenger = msg.replyTo

      try {
        when (msg.what) {
          MSG_OBB_DATA -> {
            val filePaths = msg.data.getStringArrayList(OBB_FILE_PATHS_EXTRA)
            val packageName = msg.data.getString(PACKAGE_NAME_EXTRA)

            if (!filePaths.isNullOrEmpty() && !packageName.isNullOrBlank()) {
              val files = filePaths.map(::File)

              CoroutineScope(Dispatchers.IO).launch {
                val obbResultMsg = try {
                  files.installOBBs(packageName = packageName, progress = {})
                  Message.obtain(null, OBB_MOVE_SUCCESS)
                } catch (e: Throwable) {
                  e.printStackTrace()
                  Message.obtain(null, OBB_MOVE_FAIL)
                }

                replyMessenger.send(obbResultMsg)
              }
            } else {
              throw IllegalStateException("Error moving OBB files: wrong input")
            }
          }

          else -> {
            super.handleMessage(msg)
            throw IllegalStateException("Error moving OBB files: wrong message type")
          }
        }
      } catch (e: Throwable) {
        e.printStackTrace()
        replyMessenger.send(Message.obtain(null, OBB_MOVE_FAIL))
      }
    }
  }

  companion object {
    const val OBB_FILE_PATHS_EXTRA = "obb_file_paths"
    const val PACKAGE_NAME_EXTRA = "package_name"

    const val MSG_OBB_DATA = 1

    const val OBB_MOVE_SUCCESS = 0
    const val OBB_MOVE_FAIL = -1

    internal suspend fun bindServiceAndWaitForResult(
      context: Context,
      packageName: String,
      obbFilePaths: List<String>,
    ): Boolean = suspendCancellableCoroutine { continuation ->
      val serviceIntent = Intent(context, ObbService::class.java)

      //Callback to handle OBB service results
      val obbServiceCallback = object : OBBServiceCallback {
        override fun onSuccess() = continuation.resume(true)
        override fun onError() = continuation.resume(false)
      }

      val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
          val serviceMessenger = Messenger(service)

          val obbDataMsg = Message.obtain(null, MSG_OBB_DATA)
          obbDataMsg.data = Bundle().apply {
            this.putStringArrayList(OBB_FILE_PATHS_EXTRA, ArrayList(obbFilePaths))
            this.putString(PACKAGE_NAME_EXTRA, packageName)
          }

          obbDataMsg.replyTo = Messenger(OBBServiceResponseHandler(obbServiceCallback))
          serviceMessenger.send(obbDataMsg)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
          if (!continuation.isCompleted) {
            continuation.cancel(
              cause = IllegalStateException("Error moving OBB files: service disconnected")
            )
          }
        }
      }

      // Bind to the service
      context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)

      // Handle cancellation of coroutine
      continuation.invokeOnCancellation {
        context.unbindService(connection)
      }
    }
  }
}

private class OBBServiceResponseHandler(
  val obbServiceCallback: OBBServiceCallback
) : Handler(Looper.getMainLooper()) {
  override fun handleMessage(msg: Message) {
    when (msg.what) {
      OBB_MOVE_FAIL -> obbServiceCallback.onError()
      OBB_MOVE_SUCCESS -> obbServiceCallback.onSuccess()

      else -> {
        super.handleMessage(msg)
        obbServiceCallback.onError()
      }
    }
  }
}

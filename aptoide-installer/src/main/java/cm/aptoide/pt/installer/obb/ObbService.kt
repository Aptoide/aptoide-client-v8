package cm.aptoide.pt.installer.obb

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import cm.aptoide.pt.installer.obb.ObbService.Companion.OBB_MOVE_FAIL
import cm.aptoide.pt.installer.obb.ObbService.Companion.OBB_MOVE_SUCCESS
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.io.File
import kotlin.coroutines.resume

internal interface OBBServiceCallback {
  fun onSuccess()
  fun onError()
}

internal class ObbService : Service() {
  private val handlerThread = HandlerThread("OBBHandlerThread").apply {
    start()
  }

  override fun onBind(intent: Intent): IBinder {
    return Messenger(OBBDataHandler(handlerThread.looper)).binder
  }

  override fun onDestroy() {
    super.onDestroy()
    handlerThread.quit()
  }

  private class OBBDataHandler(looper: Looper) : Handler(looper) {
    override fun handleMessage(msg: Message) {
      val replyMessenger = msg.replyTo

      try {
        when (msg.what) {
          MSG_OBB_DATA -> {
            val filePaths = msg.data.getStringArrayList(OBB_FILE_PATHS_EXTRA)
            val packageName = msg.data.getString(PACKAGE_NAME_EXTRA)

            if (!filePaths.isNullOrEmpty() && !packageName.isNullOrBlank()) {
              val files = filePaths.map(::File)

              val obbResultMsg = runBlocking {
                try {
                  files.installOBBs(packageName = packageName, progress = {})
                  Message.obtain(null, OBB_MOVE_SUCCESS)
                } catch (e: Throwable) {
                  Timber.e(e)
                  Message.obtain(null, OBB_MOVE_FAIL)
                }
              }

              replyMessenger.send(obbResultMsg)
            } else {
              replyMessenger.send(Message.obtain(null, OBB_MOVE_FAIL))
            }
          }

          else -> {
            super.handleMessage(msg)
            replyMessenger.send(Message.obtain(null, OBB_MOVE_FAIL))
          }
        }
      } catch (e: Throwable) {
        Timber.e(e)
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

      val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
          if (continuation.isCompleted) return

          val serviceMessenger = Messenger(service)

          //Callback to handle OBB service results
          val obbServiceCallback = object : OBBServiceCallback {
            override fun onSuccess() {
              if (continuation.isActive) {
                continuation.resume(true)
              }
              unbind(context)
            }

            override fun onError() {
              if (continuation.isActive) {
                continuation.resume(false)
              }
              unbind(context)
            }
          }

          val obbDataMsg = Message.obtain(null, MSG_OBB_DATA)
          obbDataMsg.data = Bundle().apply {
            this.putStringArrayList(OBB_FILE_PATHS_EXTRA, ArrayList(obbFilePaths))
            this.putString(PACKAGE_NAME_EXTRA, packageName)
          }

          obbDataMsg.replyTo = Messenger(OBBServiceResponseHandler(obbServiceCallback))
          serviceMessenger.send(obbDataMsg)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
          if (continuation.isActive) {
            continuation.cancel(
              cause = IllegalStateException("Error moving OBB files: service disconnected")
            )
          }
          unbind(context)
        }
      }

      // Bind to the service
      context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)

      // Handle cancellation of coroutine
      continuation.invokeOnCancellation {
        connection.unbind(context)
      }
    }
  }
}

private fun ServiceConnection.unbind(context: Context) = try {
  context.unbindService(this)
} catch (e: Throwable) {
  Timber.e(e)
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

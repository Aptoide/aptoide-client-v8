package com.aptoide.android.aptoidegames.gamegenie.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.Looper
import androidx.core.content.edit
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.get
import timber.log.Timber

class OverlayScreenshotManager(private val context: Context) {

  companion object {
    private const val TAG = "OverlayScreenshotMgr"
    private const val PREFS_NAME = "game_genie_prefs"
    private const val PREF_SCREENSHOT_REQUESTED = "screenshot_requested"
    private const val PREF_SCREENSHOT_PATH = "screenshot_path"
    private const val PREF_SCREENSHOT_TIMESTAMP = "screenshot_timestamp"

    private const val SCREENSHOT_SCALE = 0.5f
    private const val SCREENSHOT_QUALITY = 75
    private const val IMAGE_READER_MAX_IMAGES = 2
  }

  private var mediaProjection: MediaProjection? = null
  private var mediaProjectionManager: MediaProjectionManager? = null
  private var mediaProjectionResultCode: Int = 0
  private var mediaProjectionData: Intent? = null
  private var currentVirtualDisplay: VirtualDisplay? = null
  private var imageReader: ImageReader? = null

  private var virtualDisplayWidth: Int = 0
  private var virtualDisplayHeight: Int = 0

  var isCapturingScreenshot = false
    private set

  private var isVirtualDisplayReady = false
  private var firstFrameReceivedCallback: (() -> Unit)? = null
  
  var onMediaProjectionStopped: (() -> Unit)? = null

  fun setupMediaProjection(
    resultCode: Int,
    data: Intent?,
  ) {
    if (mediaProjection != null) {
      try {
        cleanupResources()
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }

    mediaProjectionResultCode = resultCode
    mediaProjectionData = data

    try {
      mediaProjectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE)
        as MediaProjectionManager

      mediaProjection = mediaProjectionData?.let {
        mediaProjectionManager?.getMediaProjection(mediaProjectionResultCode, it)
      }

      if (mediaProjection != null) {
        mediaProjection?.registerCallback(object : MediaProjection.Callback() {
          override fun onStop() {
            super.onStop()
            cleanupResources()
            onMediaProjectionStopped?.invoke()
          }
        }, Handler(Looper.getMainLooper()))
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun setupVirtualDisplay(
    screenWidth: Int,
    screenHeight: Int,
    onFirstFrameReady: (() -> Unit)? = null,
  ) {
    try {
      if (mediaProjection == null) {
        return
      }

      val maxDimension = maxOf(screenWidth, screenHeight)
      val width = maxDimension
      val height = maxDimension
      val density = context.resources.displayMetrics.densityDpi

      virtualDisplayWidth = width
      virtualDisplayHeight = height
      isVirtualDisplayReady = false
      firstFrameReceivedCallback = onFirstFrameReady

      currentVirtualDisplay?.release()
      currentVirtualDisplay = null
      imageReader?.close()
      imageReader = null

      val reader =
        ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, IMAGE_READER_MAX_IMAGES)
      imageReader = reader

      reader.setOnImageAvailableListener({
        if (!isVirtualDisplayReady) {
          isVirtualDisplayReady = true
          firstFrameReceivedCallback?.invoke()
          firstFrameReceivedCallback = null
        }
      }, Handler(Looper.getMainLooper()))

      val virtualDisplayCallback = object : VirtualDisplay.Callback() {
        override fun onPaused() {
          Timber.tag(TAG).d("VirtualDisplay: onPaused")
        }
        override fun onResumed() {
          Timber.tag(TAG).d("VirtualDisplay: onResumed")
        }
        override fun onStopped() {
          Timber.tag(TAG).d("VirtualDisplay: onStopped")
        }
      }

      currentVirtualDisplay = mediaProjection?.createVirtualDisplay(
        "ScreenCapture",
        width,
        height,
        density,
        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
        reader.surface,
        virtualDisplayCallback,
        Handler(Looper.getMainLooper())
      )

      if (currentVirtualDisplay == null) {
        imageReader?.close()
        imageReader = null
      }

    } catch (e: Exception) {
      e.printStackTrace()
      currentVirtualDisplay?.release()
      currentVirtualDisplay = null
      imageReader?.close()
      imageReader = null
    }
  }

  fun captureScreenBitmap(
    screenWidth: Int,
    screenHeight: Int,
    validateContent: Boolean = true,
  ): Bitmap? {
    if (mediaProjection == null || currentVirtualDisplay == null || imageReader == null) {
      return null
    }

    val vdWidth = virtualDisplayWidth
    val vdHeight = virtualDisplayHeight

    if (vdWidth <= 0 || vdHeight <= 0) {
      return null
    }

    try {
      val image = imageReader?.acquireLatestImage()

      if (image == null) {
        return null
      }

      try {
        val planes = image.planes
        if (planes.isEmpty()) {
          return null
        }

        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * vdWidth

        val bitmapWidth = vdWidth + rowPadding / pixelStride
        val bitmap = createBitmap(bitmapWidth, vdHeight)
        bitmap.copyPixelsFromBuffer(buffer)

        val squareBitmap = Bitmap.createBitmap(bitmap, 0, 0, vdWidth, vdHeight)
        bitmap.recycle()

        val cropX = (vdWidth - screenWidth) / 2
        val cropY = (vdHeight - screenHeight) / 2

        val croppedBitmap = Bitmap.createBitmap(
          squareBitmap,
          cropX.coerceAtLeast(0),  // x - center horizontally
          cropY.coerceAtLeast(0),  // y - center vertically
          screenWidth.coerceAtMost(vdWidth),   // width - current screen width
          screenHeight.coerceAtMost(vdHeight)  // height - current screen height
        )
        squareBitmap.recycle()

        if (validateContent && isBitmapPredominantlyBlack(croppedBitmap)) {
          croppedBitmap.recycle()
          return null
        }

        return croppedBitmap
      } finally {
        image.close()
      }
    } catch (e: Exception) {
      e.printStackTrace()
      return null
    }
  }

  private fun isBitmapPredominantlyBlack(bitmap: Bitmap): Boolean {
    try {
      val sampleSize = 50
      var blackPixelCount = 0
      var totalSampledPixels = 0

      val width = bitmap.width
      val height = bitmap.height

      for (y in 0 until height step sampleSize) {
        for (x in 0 until width step sampleSize) {
          val pixel = bitmap[x, y]
          val red = (pixel shr 16) and 0xFF
          val green = (pixel shr 8) and 0xFF
          val blue = pixel and 0xFF

          if (red < 20 && green < 20 && blue < 20) {
            blackPixelCount++
          }
          totalSampledPixels++
        }
      }

      return totalSampledPixels > 0 && (blackPixelCount.toFloat() / totalSampledPixels) > 0.95f
    } catch (e: Exception) {
      e.printStackTrace()
      return false
    }
  }

  fun saveBitmapAndNotify(bitmap: Bitmap): File? {
    return try {
      val screenshotFile = saveBitmapToCache(bitmap)

      if (screenshotFile != null) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit(commit = true) {
          putBoolean(PREF_SCREENSHOT_REQUESTED, true)
          putString(PREF_SCREENSHOT_PATH, screenshotFile.absolutePath)
          putLong(PREF_SCREENSHOT_TIMESTAMP, System.currentTimeMillis())
        }

        val broadcastIntent = Intent(ScreenshotBroadcastReceiver.ACTION_SCREENSHOT_CAPTURED)
        broadcastIntent.putExtra(ScreenshotBroadcastReceiver.EXTRA_PATH, screenshotFile.absolutePath)
        context.sendBroadcast(broadcastIntent)

        screenshotFile
      } else {
        null
      }
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  private fun saveBitmapToCache(bitmap: Bitmap): File? {
    return try {
      val newWidth = (bitmap.width * SCREENSHOT_SCALE).toInt()
      val newHeight = (bitmap.height * SCREENSHOT_SCALE).toInt()

      val scaledBitmap = bitmap.scale(newWidth, newHeight).also {
        bitmap.recycle()
      }

      val cacheDir = context.cacheDir
      val screenshotFile = File(cacheDir, "screenshot_${System.currentTimeMillis()}.jpg")

      FileOutputStream(screenshotFile).use { out ->
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, SCREENSHOT_QUALITY, out)
      }

      scaledBitmap.recycle()
      screenshotFile
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  fun setCapturingState(isCapturing: Boolean) {
    isCapturingScreenshot = isCapturing
  }

  fun needsRecreation(): Boolean {
    return currentVirtualDisplay == null || imageReader == null
  }

  fun hasMediaProjection(): Boolean {
    return mediaProjection != null
  }

  fun hasPermissionData(): Boolean {
    return mediaProjectionData != null && mediaProjectionResultCode != 0
  }

  fun isVirtualDisplayReady(): Boolean {
    return isVirtualDisplayReady
  }

  private fun cleanupResources() {
    currentVirtualDisplay?.release()
    currentVirtualDisplay = null
    imageReader?.close()
    imageReader = null
    mediaProjection?.stop()
    mediaProjection = null
    isVirtualDisplayReady = false
    firstFrameReceivedCallback = null
  }

  fun cleanup() {
    cleanupResources()
  }
}

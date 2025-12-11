package com.aptoide.android.aptoidegames.gamegenie.presentation

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OverlayDisplayMonitor(
  private val context: Context,
  private val scope: CoroutineScope,
  private val onDimensionChanged: () -> Unit,
) {

  private var displayListener: DisplayManager.DisplayListener? = null
  private var orientationCheckJob: Job? = null
  private var forcedChecksJob: Job? = null

  @Volatile
  private var lastWidth: Int = 0

  @Volatile
  private var lastHeight: Int = 0

  fun startMonitoring(
    currentWidth: Int,
    currentHeight: Int,
  ) {
    stop()
    lastWidth = currentWidth
    lastHeight = currentHeight

    setupDisplayListener()
    startPollingFallback()
  }

  private fun setupDisplayListener() {
    val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

    displayListener = object : DisplayManager.DisplayListener {
      override fun onDisplayAdded(displayId: Int) {}
      override fun onDisplayRemoved(displayId: Int) {}

      override fun onDisplayChanged(displayId: Int) {
        val displayMetrics = context.resources.displayMetrics
        val currentWidth = displayMetrics.widthPixels
        val currentHeight = displayMetrics.heightPixels

        if (currentWidth != lastWidth || currentHeight != lastHeight) {
          lastWidth = currentWidth
          lastHeight = currentHeight
          onDimensionChanged()
        }
      }
    }

    displayManager.registerDisplayListener(displayListener, Handler(Looper.getMainLooper()))
  }

  private fun startPollingFallback() {
    orientationCheckJob?.cancel()
    orientationCheckJob = scope.launch {
      while (true) {
        delay(1000)

        val displayMetrics = context.resources.displayMetrics
        val currentWidth = displayMetrics.widthPixels
        val currentHeight = displayMetrics.heightPixels

        if (currentWidth != lastWidth || currentHeight != lastHeight) {
          lastWidth = currentWidth
          lastHeight = currentHeight
          onDimensionChanged()
        }
      }
    }
  }

  fun performForcedChecks() {
    forcedChecksJob?.cancel()
    forcedChecksJob = scope.launch {
      repeat(5) { iteration ->
        delay(if (iteration == 0) 100L else 300L)

        val displayMetrics = context.resources.displayMetrics
        val currentWidth = displayMetrics.widthPixels
        val currentHeight = displayMetrics.heightPixels

        if (currentWidth != lastWidth || currentHeight != lastHeight) {
          lastWidth = currentWidth
          lastHeight = currentHeight
          onDimensionChanged()
          return@launch
        }
      }
    }.apply {
      invokeOnCompletion {
        if (this == forcedChecksJob) {
          forcedChecksJob = null
        }
      }
    }
  }

  fun stop() {
    displayListener?.let {
      val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
      displayManager.unregisterDisplayListener(it)
    }
    orientationCheckJob?.cancel()
    forcedChecksJob?.cancel()
    forcedChecksJob = null
  }
}

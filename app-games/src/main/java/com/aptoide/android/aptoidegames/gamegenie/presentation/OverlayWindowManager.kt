package com.aptoide.android.aptoidegames.gamegenie.presentation

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.MainThread
import androidx.compose.ui.platform.ComposeView

class OverlayWindowManager(private val context: Context) {

  companion object {
    const val FAB_SIZE_DP = 56
    const val EDGE_PADDING_DP = 8
    const val MENU_WIDTH_DP = 260
    const val MENU_HEIGHT_DP = 80
    const val MENU_REMOVE_HEIGHT_DP = 32
    const val MENU_SPACING_DP = 24
    const val MENU_LEFT_EXTENSION_DP =
      204
  }

  private val windowManager: WindowManager =
    context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

  var screenWidth: Int = 0
    private set
  var screenHeight: Int = 0
    private set
  
  private var previousScreenWidth: Int = 0
  private var previousScreenHeight: Int = 0

  private val edgePadding: Int
    get() = (EDGE_PADDING_DP * context.resources.displayMetrics.density).toInt()

  private val fabSize: Int
    get() = (FAB_SIZE_DP * context.resources.displayMetrics.density).toInt()

  init {
    updateScreenDimensions()
  }
  
  fun updateScreenDimensions() {
    previousScreenWidth = screenWidth
    previousScreenHeight = screenHeight
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val windowMetrics = windowManager.currentWindowMetrics
      val windowInsets = windowMetrics.windowInsets
      val insets = windowInsets.getInsetsIgnoringVisibility(
        android.view.WindowInsets.Type.systemBars()
      )
      val bounds = windowMetrics.bounds
      
      screenWidth = bounds.width() - insets.left - insets.right
      screenHeight = bounds.height() - insets.top - insets.bottom
    } else {
      @Suppress("DEPRECATION")
      val point = android.graphics.Point()
      windowManager.defaultDisplay.getSize(point)
      screenWidth = point.x
      screenHeight = point.y
    }
  }

  fun createFabLayoutParams(): WindowManager.LayoutParams {
    val params = WindowManager.LayoutParams(
      fabSize,
      fabSize,
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
      PixelFormat.TRANSLUCENT
    ).apply {
      gravity = Gravity.TOP or Gravity.START
      val (constrainedX, constrainedY) = constrainFabPosition(
        screenWidth - fabSize - edgePadding,
        screenHeight / 2 - fabSize / 2
      )
      x = constrainedX
      y = constrainedY
      windowAnimations = 0
    }
    return params
  }

  @MainThread
  fun addView(
    view: ComposeView,
    params: WindowManager.LayoutParams,
  ) {
    check(Looper.myLooper() == Looper.getMainLooper()) { "OverlayWindowManager.addView must run on main thread" }
    view.setBackgroundColor(Color.TRANSPARENT)
    windowManager.addView(view, params)
  }

  @MainThread
  fun removeView(view: ComposeView) {
    check(Looper.myLooper() == Looper.getMainLooper()) { "OverlayWindowManager.removeView must run on main thread" }
    if (view.parent != null) {
      windowManager.removeView(view)
    }
  }

  @MainThread
  fun updateViewLayout(
    view: ComposeView,
    params: WindowManager.LayoutParams,
  ) {
    check(Looper.myLooper() == Looper.getMainLooper()) { "OverlayWindowManager.updateViewLayout must run on main thread" }
    if (view.parent != null) {
      windowManager.updateViewLayout(view, params)
    }
  }

  fun constrainFabPosition(
    currentX: Int,
    currentY: Int,
  ): Pair<Int, Int> {
    val maxX = (screenWidth - fabSize - edgePadding).coerceAtLeast(edgePadding)
    val newX = currentX.coerceIn(edgePadding, maxX)

    val maxY = (screenHeight - fabSize - edgePadding).coerceAtLeast(edgePadding)
    val newY = currentY.coerceIn(edgePadding, maxY)

    return Pair(newX, newY)
  }

  fun adjustPositionForOrientationChange(
    currentX: Int,
    currentY: Int,
  ): Pair<Int, Int> {
    if (previousScreenWidth == 0 || previousScreenHeight == 0 ||
        (previousScreenWidth == screenWidth && previousScreenHeight == screenHeight)) {
      return constrainFabPosition(currentX, currentY)
    }

    val threshold = edgePadding * 2
    val wasOnLeftEdge = currentX <= threshold
    val wasOnRightEdge = currentX >= (previousScreenWidth - fabSize - threshold)

    val newX = when {
      wasOnLeftEdge -> edgePadding
      wasOnRightEdge -> screenWidth - fabSize - edgePadding
      else -> {
        val relativeX = currentX.toFloat() / previousScreenWidth.toFloat()
        (relativeX * screenWidth).toInt()
      }
    }

    val relativeY = currentY.toFloat() / previousScreenHeight.toFloat()
    val newY = (relativeY * screenHeight).toInt()

    return constrainFabPosition(newX, newY)
  }

  fun getMenuHeightPx(): Int {
    return (MENU_HEIGHT_DP * context.resources.displayMetrics.density).toInt()
  }

  fun getRemoveMenuHeightPx(): Int {
    return (MENU_REMOVE_HEIGHT_DP * context.resources.displayMetrics.density).toInt()
  }

  fun getMenuWidthPx(): Int {
    return (MENU_WIDTH_DP * context.resources.displayMetrics.density).toInt()
  }

  fun getFabSizePx(): Int = fabSize

  fun getEdgePaddingPx(): Int = edgePadding

  fun getMenuSpacingPx(): Int {
    return (MENU_SPACING_DP * context.resources.displayMetrics.density).toInt()
  }

  fun getMenuLeftExtensionPx(): Int {
    return (MENU_LEFT_EXTENSION_DP * context.resources.displayMetrics.density).toInt()
  }
}

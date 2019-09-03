package cm.aptoide.aptoideviews.skeleton

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import cm.aptoide.aptoideviews.R
import cm.aptoide.aptoideviews.childViews
import cm.aptoide.aptoideviews.skeleton.mask.*


class SkeletonGroup : FrameLayout {

  private var viewPreferences: SkeletonViewPreferences = SkeletonViewPreferences()
  private val masks = ArrayList<SkeletonMask>()
  private var isSkeleton: Boolean = false
  private var measuredLayout: Boolean = false

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    viewPreferences = getSkeletonViewPreferences(context, attrs, viewPreferences)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    masks.forEach { mask -> mask.draw(canvas) }
  }

  override fun dispatchDraw(canvas: Canvas?) {
    super.dispatchDraw(canvas)
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    measuredLayout = true
    if (isSkeleton) {
      showSkeleton()
    }
  }

  fun showOriginal() {
    isSkeleton = false
    childViews().forEach { it.visibility = View.VISIBLE }
    masks.clear()
  }

  fun showSkeleton() {
    isSkeleton = true
    if (childCount > 0) {
      childViews().forEach { it.visibility = View.INVISIBLE }
      setWillNotDraw(false)
      invalidateMasks()
    }

  }

  private fun invalidateMasks() {
    if (measuredLayout) {
      processViewGroupMasks(this, viewPreferences, 0f, 0f, masks)
    }
  }


  private fun processViewGroupMasks(view: View,
                                    viewPreferences: SkeletonViewPreferences,
                                    offsetLeft: Float,
                                    offsetTop: Float,
                                    masks: ArrayList<SkeletonMask>) {
    (view as? ViewGroup)?.let { viewGroup ->
      viewGroup.childViews().forEach { v ->
        if(v is RecyclerView) return@forEach //TODO

        if(v !is SkeletonGroup){
          processViewGroupMasks(v, getViewPreferences(v, viewPreferences), offsetLeft, offsetTop, masks)
        } else {
          v.processViewGroupMasks(v , getViewPreferences(v, viewPreferences), v.left.toFloat(), v.top.toFloat(), masks)
        }
      }
    } ?: getMaskView(view, viewPreferences, offsetLeft, offsetTop, masks)
  }

  private fun getMaskView(view: View, defaultPrefs: SkeletonViewPreferences,
                          offsetLeft: Float,
                          offsetTop: Float,
                          masks: ArrayList<SkeletonMask>) {
    if(view is RecyclerView) return

    val mask = SkeletonMask(view, offsetLeft, offsetTop, getViewPreferences(view, defaultPrefs))
    mask.mask()
    masks.add(mask)
  }

  private fun getViewPreferences(v: View,
                                 defaultPrefs: SkeletonViewPreferences): SkeletonViewPreferences {
    var prfs = defaultPrefs
    if(v is SkeletonGroup){
      prfs = v.viewPreferences
    } else if (v.layoutParams is LayoutParams) {
      prfs = (v.layoutParams as LayoutParams).skeletonViewPreferences
    }

    return prfs
  }

  /**
   * Layout Params section
   */

  override fun generateDefaultLayoutParams(): FrameLayout.LayoutParams {
    return LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT)
  }

  override fun generateLayoutParams(attrs: AttributeSet?): FrameLayout.LayoutParams {
    return LayoutParams(context, attrs)
  }

  override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
    if (lp is LayoutParams) {
      return LayoutParams(lp)
    }
    return super.generateLayoutParams(lp)
  }

  class LayoutParams : FrameLayout.LayoutParams {
    internal var skeletonViewPreferences = SkeletonViewPreferences()

    constructor(@NonNull c: Context, @Nullable attrs: AttributeSet?) : super(c, attrs) {
      skeletonViewPreferences = getSkeletonViewPreferences(c, attrs, skeletonViewPreferences)
    }

    constructor(width: Int, height: Int) : super(width, height)
    constructor(width: Int, height: Int, gravity: Int) : super(width, height, gravity)
    constructor(@NonNull source: ViewGroup.LayoutParams) : super(source)
    constructor(@NonNull source: MarginLayoutParams) : super(source)
    @TargetApi(Build.VERSION_CODES.KITKAT)
    constructor(@NonNull source: FrameLayout.LayoutParams) : super(source)

    @TargetApi(Build.VERSION_CODES.KITKAT)
    constructor(@NonNull source: LayoutParams) : super(source) {
      this.skeletonViewPreferences = source.skeletonViewPreferences
    }

  }

  companion object {
    private fun getSkeletonViewPreferences(c: Context, attrs: AttributeSet?,
                                           defaultPrefs: SkeletonViewPreferences): SkeletonViewPreferences {
      var skeletonViewPreferences = defaultPrefs
      attrs?.let {
        val t = c.obtainStyledAttributes(it, R.styleable.SkeletonGroup, 0, 0)
        val size = getSkeletonSize(c, t)
        val shape = getSkeletonShape(c, t, skeletonViewPreferences.shape)
        val border = getSkeletonBorder(c, t, skeletonViewPreferences.border)
        skeletonViewPreferences = SkeletonViewPreferences(size, shape, border)
        t.recycle()
      }
      return skeletonViewPreferences
    }

    private fun getSkeletonBorder(@NonNull context: Context,
                                  @NonNull t: TypedArray,
                                  defaultBorder: Border): Border {
      val borderColor =
          t.getColorStateList(R.styleable.SkeletonGroup_layout_skeleton_border_color)?.defaultColor
              ?: defaultBorder.color
      val borderThickness = t.getDimension(R.styleable.SkeletonGroup_layout_skeleton_border_thickness, 0f)
      return Border(borderThickness.toInt(), borderColor)
    }

    private fun getSkeletonShape(@NonNull context: Context,
                                 @NonNull t: TypedArray,
                                 defaultShape: Shape): Shape {
      var shape: Shape = defaultShape
      val skeletonColor =
          t.getColorStateList(R.styleable.SkeletonGroup_layout_skeleton_color)?.defaultColor
              ?: defaultShape.color
      val skeletonShape = t.getInt(R.styleable.SkeletonGroup_layout_skeleton_shape, 0)
      val skeletonCornerRadius =
          t.getDimension(R.styleable.SkeletonGroup_layout_skeleton_corner_radius, 0f)
      when (skeletonShape) {
        0 -> {
          shape = Shape.Rect(skeletonColor, skeletonCornerRadius.toInt())
        }
        1 -> {
          shape = Shape.Circle(skeletonColor)
        }
      }
      return shape
    }

    private fun getSkeletonSize(@NonNull context: Context,
                                @NonNull t: TypedArray): Size {
      var widthDimension: SizeDimension = SizeDimension.OriginalValue
      var heightDimension: SizeDimension = SizeDimension.OriginalValue

      val widthValue = TypedValue()
      if (t.getValue(R.styleable.SkeletonGroup_layout_skeleton_width, widthValue)) {
        if (widthValue.type == TypedValue.TYPE_DIMENSION) {
          widthDimension =
              SizeDimension.SpecificValue(widthValue.getDimension(context.resources.displayMetrics))
        } else if (widthValue.type == TypedValue.TYPE_FRACTION) {
          widthDimension = SizeDimension.PercentValue(widthValue.getFraction(1f, 1f))
        }
      }
      val heightValue = TypedValue()
      if (t.getValue(R.styleable.SkeletonGroup_layout_skeleton_height, heightValue)) {
        if (heightValue.type == TypedValue.TYPE_DIMENSION) {
          heightDimension =
              SizeDimension.SpecificValue(heightValue.getDimension(context.resources.displayMetrics))
        } else if (heightValue.type == TypedValue.TYPE_FRACTION) {
          heightDimension = SizeDimension.PercentValue(heightValue.getFraction(1f, 1f))
        }
      }
      return Size(widthDimension, heightDimension)
    }
  }
}
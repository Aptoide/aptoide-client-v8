package cm.aptoide.pt.v8engine.view.recycler.displayable;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.view.LifecycleSchim;
import cm.aptoide.pt.v8engine.view.recycler.widget.WidgetFactory;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class Displayable implements LifecycleSchim {

  private boolean fixedPerLineCount;
  private int defaultPerLineCount;
  private boolean isVisible = false;

  /**
   * Needed for reflective {@link Class#newInstance()}.
   */
  public Displayable() {
    Configs config = getConfig();
    fixedPerLineCount = config.isFixedPerLineCount();
    defaultPerLineCount = config.getDefaultPerLineCount();
  }

  public boolean isFixedPerLineCount() {
    return fixedPerLineCount;
  }

  public int getDefaultPerLineCount() {
    return defaultPerLineCount;
  }

  public boolean isVisible() {
    return isVisible;
  }

  public void setVisible(boolean visible) {
    isVisible = visible;
  }

  @Partners protected abstract Configs getConfig();

  @Partners @LayoutRes public abstract int getViewLayout();

  public int getSpanSize(WindowManager windowManager, Resources resources) {
    return WidgetFactory.getColumnSize(resources, windowManager) / getPerLineCount(windowManager,
        resources);
  }

  public int getPerLineCount(WindowManager windowManager, Resources resources) {

    int tmp;

    if (isFixedPerLineCount()) {
      tmp = getDefaultPerLineCount();
    } else {
      tmp = (int) (AptoideUtils.ScreenU.getScreenWidthInDip(windowManager, resources)
          / AptoideUtils.ScreenU.REFERENCE_WIDTH_DPI * getDefaultPerLineCount());
    }

    return tmp != 0 ? tmp : 1;
  }

  /**
   * Sets visibility of this component to visible. Schimmed component lifecycle from the using
   * adapter.
   */
  public void onResume() {
    isVisible = true;
  }

  /**
   * Sets visibility of this component to invisible. Schimmed component lifecycle from the using
   * adapter.
   */
  public void onPause() {
    isVisible = false;
  }

  /**
   * Optional method. Schimmed component lifecycle from the using adapter.
   */
  @Override public void onViewCreated() {

  }

  /**
   * Optional method. Schimmed component lifecycle from the using adapter.
   */
  @Override public void onDestroyView() {

  }

  /**
   * Optional method. Schimmed component lifecycle from the using adapter.
   */
  public void onSaveInstanceState(Bundle outState) {

  }

  /**
   * Optional method. Schimmed component lifecycle from the using adapter.
   */
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {

  }

  public Displayable setFullRow() {
    defaultPerLineCount = 1;
    fixedPerLineCount = true;
    return this;
  }

  public class Configs {
    private final int defaultPerLineCount;
    private final boolean fixedPerLineCount;

    public Configs(int defaultPerLineCount, boolean fixedPerLineCount) {
      this.defaultPerLineCount = defaultPerLineCount;
      this.fixedPerLineCount = fixedPerLineCount;
    }

    public int getDefaultPerLineCount() {
      return this.defaultPerLineCount;
    }

    public boolean isFixedPerLineCount() {
      return this.fixedPerLineCount;
    }
  }
}

package cm.aptoide.pt.view.recycler.displayable;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import cm.aptoide.pt.annotation.Ignore;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.dataprovider.model.v7.Type;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.LifecycleSchim;
import cm.aptoide.pt.view.recycler.widget.WidgetFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by neuro on 14-04-2016.
 */
@Ignore @Accessors(chain = true) public abstract class Displayable implements LifecycleSchim {

  @Getter CompositeSubscription subscriptions;
  @Getter private boolean fixedPerLineCount;
  @Getter private int defaultPerLineCount;
  @Setter @Getter private boolean isVisible = false;

  /**
   * Needed for reflective {@link Class#newInstance()}.
   */
  public Displayable() {
    Configs config = getConfig();
    fixedPerLineCount = config.isFixedPerLineCount();
    defaultPerLineCount = config.getDefaultPerLineCount();
  }

  //public abstract Type getType();

  @Partners protected abstract Configs getConfig();

  @Partners @LayoutRes public abstract int getViewLayout();

  public int getSpanSize(WindowManager windowManager, Resources resources) {
    return WidgetFactory.getColumnSize(resources, windowManager) / getPerLineCount(windowManager,
        resources);
  }

  //
  // LifecycleSchim interface
  // optional methods

  /**
   * Same code as in {@link Type#getPerLineCount()} todo: terminar este doc
   *
   * @param windowManager
   * @param resources
   */
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

  @Getter public class Configs {
    private final int defaultPerLineCount;
    private final boolean fixedPerLineCount;

    public Configs(int defaultPerLineCount, boolean fixedPerLineCount) {
      this.defaultPerLineCount = defaultPerLineCount;
      this.fixedPerLineCount = fixedPerLineCount;
    }
  }
}

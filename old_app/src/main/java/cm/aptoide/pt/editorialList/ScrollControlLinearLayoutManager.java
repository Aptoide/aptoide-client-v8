package cm.aptoide.pt.editorialList;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ScrollControlLinearLayoutManager extends LinearLayoutManager {
  private boolean isScrollEnabled = true;

  public ScrollControlLinearLayoutManager(Context context) {
    super(context);
  }

  public void setScrollEnabled(boolean flag) {
    this.isScrollEnabled = flag;
  }

  @Override public boolean canScrollVertically() {
    return isScrollEnabled && super.canScrollVertically();
  }
}
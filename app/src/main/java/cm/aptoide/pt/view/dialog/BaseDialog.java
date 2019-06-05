package cm.aptoide.pt.view.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import com.trello.rxlifecycle.components.support.RxDialogFragment;

public abstract class BaseDialog extends RxDialogFragment {
  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    WindowManager.LayoutParams layoutParams = getDialog().getWindow()
        .getAttributes();
    layoutParams.dimAmount = 0.6f;
    getDialog().getWindow()
        .setAttributes(layoutParams);
    getDialog().getWindow()
        .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
  }
}

package cm.aptoide.pt.v8engine.view.store;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by pedroribeiro on 26/01/17.
 */

public class StoreAutoCompleteTextView extends AutoCompleteTextView {

  private static final int MESSAGE_TEXT_CHANGED = 100;
  private static final int DEFAULT_AUTOCOMPLETE_DELAY = 2000;
  private final Handler mHandler = new Handler() {
    @Override public void handleMessage(Message msg) {
      StoreAutoCompleteTextView.super.performFiltering((CharSequence) msg.obj, msg.arg1);
    }
  };
  private int mAutoCompleteDelay = DEFAULT_AUTOCOMPLETE_DELAY;

  public StoreAutoCompleteTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void performFiltering(CharSequence text, int keyCode) {
    mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
    mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text),
        mAutoCompleteDelay);
  }

  @Override public void onFilterComplete(int count) {
    super.onFilterComplete(count);
  }
}

package cm.aptoide.pt.view.navigator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.view.leak.LeakFragment;
import com.jakewharton.rxrelay.BehaviorRelay;
import java.util.Map;

/**
 * Created by trinkes on 08/09/2017.
 */

public class NavigateFragment extends LeakFragment {

  public static final String REQUEST_CODE_KEY = "request_code_key";
  public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;
  public static final int RESULT_OK = Activity.RESULT_OK;
  private int requestCode;
  private ActivityResultNavigator activityResultNavigator;

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (!(activity instanceof ActivityResultNavigator)) {
      throw new IllegalStateException(this.getClass()
          .getSimpleName()
          + " has to be an instance of "
          + ActivityResultNavigator.class.getSimpleName());
    }
    this.activityResultNavigator = (ActivityResultNavigator) activity;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle arguments = getArguments();
    if (arguments != null) {
      requestCode = arguments.getInt(REQUEST_CODE_KEY);
    }
  }

  public BehaviorRelay<Map<Integer, Result>> getFragmentResultRelay() {
    return activityResultNavigator.getFragmentResultRelay();
  }

  protected Map<Integer, Result> getFragmentResultMap() {
    return activityResultNavigator.getFragmentResultMap();
  }

  protected void finishWithResult(int resultCode) {
    finishWithResult(resultCode, null);
  }

  protected void finishWithResult(int resultCode, @Nullable Intent data) {
    ((ActivityResultNavigator) getActivity()).getFragmentNavigator()
        .popWithResult(new Result(requestCode, resultCode, data));
  }
}

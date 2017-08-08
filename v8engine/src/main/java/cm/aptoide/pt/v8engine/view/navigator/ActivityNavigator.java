package cm.aptoide.pt.v8engine.view.navigator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import rx.Observable;

public interface ActivityNavigator {

  void navigateForResult(Class<? extends Activity> activityClass, int requestCode, Bundle bundle);

  void navigateForResult(Class<? extends Activity> activityClass, int requestCode);

  Observable<Result> results(int requestCode);

  Observable<Result> navigateForResult(String action, Uri uri, int requestCode);

  Observable<Result> navigateForResultWithOutput(String action, Uri outputUri, int requestCode);

  void navigateTo(Class<? extends Activity> activityClass);

  void navigateTo(Class<? extends Activity> activityClass, Bundle bundle);

  void navigateBackWithResult(int resultCode, Bundle bundle);

  void navigateBack();

  void navigateTo(Uri uri);

  class Result {

    private final int requestCode;
    private final int resultCode;
    private final Intent data;

    public Result(int requestCode, int resultCode, Intent data) {
      this.requestCode = requestCode;
      this.resultCode = resultCode;
      this.data = data;
    }

    public int getRequestCode() {
      return requestCode;
    }

    public int getResultCode() {
      return resultCode;
    }

    public Intent getData() {
      return data;
    }
  }
}

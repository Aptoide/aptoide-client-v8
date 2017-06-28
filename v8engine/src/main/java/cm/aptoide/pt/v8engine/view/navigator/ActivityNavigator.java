package cm.aptoide.pt.v8engine.view.navigator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import rx.Observable;

public interface ActivityNavigator {

  Observable<Result> navigateForResult(Class<? extends AppCompatActivity> activityClass, int requestCode);

  void navigateTo(Class<? extends AppCompatActivity> activityClass);

  void navigateTo(Class<? extends AppCompatActivity> activityClass, Bundle bundle);

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

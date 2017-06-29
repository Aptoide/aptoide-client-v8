package cm.aptoide.pt.v8engine.view.navigator;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import cm.aptoide.pt.v8engine.view.leak.LeakActivity;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

public abstract class ActivityResultNavigator extends LeakActivity implements ActivityNavigator {

  private PublishRelay<Result> resultRelay;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    resultRelay = PublishRelay.create();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    resultRelay.call(new Result(requestCode, resultCode, data));
  }

  @Override public void finish(int code, Bundle bundle) {
    setResult(code, new Intent().putExtras(bundle));
    finish();
  }

  @Override
  public Observable<Result> navigateForResult(Class<? extends AppCompatActivity> activityClass,
      int requestCode) {
    final Intent intent = new Intent();
    intent.setComponent(new ComponentName(this, activityClass));
    startActivityForResult(intent, requestCode);
    return resultRelay.filter(result -> result.getRequestCode() == requestCode);
  }

  @Override public Observable<Result> navigateForResult(String action, Uri uri, int requestCode) {
    startActivityForResult(new Intent(action, uri), requestCode);
    return resultRelay.filter(result -> result.getRequestCode() == requestCode);
  }

  @Override public Observable<Result> navigateForResultWithOutput(String action, Uri outputUri,
      int requestCode) {
    Intent intent = new Intent(action);
    if (intent.resolveActivity(getPackageManager()) != null) {
      intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
      startActivityForResult(intent, requestCode);
      return resultRelay.filter(result -> result.getRequestCode() == requestCode);
    }
    return Observable.empty();
  }

  @Override public void navigateTo(Class<? extends AppCompatActivity> activityClass) {
    final Intent intent = new Intent();
    intent.setComponent(new ComponentName(this, activityClass));
    startActivity(intent);
  }

  @Override
  public void navigateTo(Class<? extends AppCompatActivity> activityClass, Bundle bundle) {
    final Intent intent = new Intent();
    intent.setComponent(new ComponentName(this, activityClass));
    intent.putExtras(bundle);
    startActivity(intent);
  }
}

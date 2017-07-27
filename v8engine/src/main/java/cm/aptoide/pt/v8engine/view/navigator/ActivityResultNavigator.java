package cm.aptoide.pt.v8engine.view.navigator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import cm.aptoide.pt.v8engine.NavigationProvider;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.leak.LeakActivity;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

public abstract class ActivityResultNavigator extends LeakActivity
    implements ActivityNavigator, NavigationProvider {

  private PublishRelay<Result> resultRelay;
  private FragmentNavigator fragmentNavigator;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    fragmentNavigator =
        new FragmentNavigator(getSupportFragmentManager(), R.id.fragment_placeholder,
            android.R.anim.fade_in, android.R.anim.fade_out,
            ((V8Engine) getApplicationContext()).getDefaultSharedPreferences());
    // super.onCreate handles fragment creation using FragmentManager.
    // Make sure navigator instances are already created when fragments are created,
    // else getFragmentNavigator and getActivityNavigator will return null.
    super.onCreate(savedInstanceState);
    resultRelay = PublishRelay.create();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    resultRelay.call(new Result(requestCode, resultCode, data));
  }

  @Override
  public Observable<Result> navigateForResult(Class<? extends AppCompatActivity> activityClass,
      int requestCode) {
    final Intent intent = new Intent();
    intent.setComponent(new ComponentName(this, activityClass));
    startActivityForResult(intent, requestCode);
    return resultRelay.filter(result -> result.getRequestCode() == requestCode);
  }

  @Override public void navigateForResult(Class<? extends Activity> activityClass, int requestCode,
      Bundle bundle) {
    final Intent intent = new Intent();
    intent.setComponent(new ComponentName(this, activityClass));
    intent.putExtras(bundle);
    startActivityForResult(intent, requestCode);
  }

  @Override public Observable<Result> results(int requestCode) {
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

  @Override public void finish(int code, Bundle bundle) {
    setResult(code, new Intent().putExtras(bundle));
    finish();
  }

  @Override public ActivityNavigator getActivityNavigator() {
    return this;
  }

  @Override public FragmentNavigator getFragmentNavigator() {
    return fragmentNavigator;
  }
}

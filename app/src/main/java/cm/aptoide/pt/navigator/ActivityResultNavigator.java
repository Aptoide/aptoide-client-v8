package cm.aptoide.pt.navigator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.view.BaseActivity;
import cm.aptoide.pt.view.fragment.FragmentView;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;

public abstract class ActivityResultNavigator extends BaseActivity implements ActivityNavigator {

  @Inject AccountNavigator accountNavigator;
  @Inject @Named("marketName") String marketName;
  private PublishRelay<Result> resultRelay;
  private FragmentNavigator fragmentNavigator;
  private BehaviorRelay<Map<Integer, Result>> fragmentResultRelay;
  private Map<Integer, Result> fragmentResultMap;

  public BehaviorRelay<Map<Integer, Result>> getFragmentResultRelay() {
    return fragmentResultRelay;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    fragmentResultRelay = ((AptoideApplication) getApplicationContext()).getFragmentResultRelay();
    fragmentResultMap = ((AptoideApplication) getApplicationContext()).getFragmentResulMap();
    fragmentNavigator =
        new FragmentResultNavigator(getSupportFragmentManager(), R.id.fragment_placeholder,
            android.R.anim.fade_in, android.R.anim.fade_out, fragmentResultMap,
            fragmentResultRelay);
    // super.onCreate handles fragment creation using FragmentManager.
    // Make sure navigator instances are already created when fragments are created,
    // else getFragmentNavigator and getActivityNavigator will return null.
    super.onCreate(savedInstanceState);
    resultRelay = PublishRelay.create();
    getActivityComponent().inject(this);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    resultRelay.call(new Result(requestCode, resultCode, data));

    Fragment fragment = getFragmentNavigator().getFragment();
    if (fragment != null
        && fragment instanceof FragmentView
        && !((FragmentView) fragment).isStartActivityForResultCalled()) {
      fragment.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override public void navigateForResult(Class<? extends Activity> activityClass, int requestCode,
      Bundle bundle) {
    final Intent intent = new Intent();
    intent.setComponent(new ComponentName(this, activityClass));
    intent.putExtras(bundle);
    startActivityForResult(intent, requestCode);
  }

  @Override public void navigateForResult(Intent intent, int requestCode) {
    startActivityForResult(intent, requestCode);
  }

  @Override public Observable<Result> results(int requestCode) {
    return resultRelay.filter(result -> result.getRequestCode() == requestCode);
  }

  @Override public Observable<Result> navigateForResult(String action, Uri uri, int requestCode) {
    startActivityForResult(new Intent(action, uri), requestCode);
    return resultRelay.filter(result -> result.getRequestCode() == requestCode);
  }

  @Override public void navigateForResult(String action, Uri uri, int requestCode, String extraId,
      String extraValue) {
    Intent intent = new Intent(action);
    intent.setData(uri);
    intent.putExtra(extraId, extraValue);
    startActivityForResult(intent, requestCode);
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

  @Override public void navigateTo(Class<? extends Activity> activityClass) {
    final Intent intent = new Intent();
    intent.setComponent(new ComponentName(this, activityClass));
    startActivity(intent);
  }

  @Override public void navigateTo(Class<? extends Activity> activityClass, Bundle bundle) {
    final Intent intent = new Intent();
    intent.setComponent(new ComponentName(this, activityClass));
    intent.putExtras(bundle);
    startActivity(intent);
  }

  @Override public void navigateBackWithResult(int resultCode, Bundle bundle) {
    setResult(resultCode, new Intent().putExtras(bundle));
    finish();
  }

  @Override public void navigateBack() {
    finish();
  }

  @Override public void navigateTo(Uri uri) {
    final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

  @Override public Observable<Result> results() {
    return resultRelay;
  }

  @Override public Activity getActivity() {
    return this;
  }

  public ActivityNavigator getActivityNavigator() {
    return this;
  }

  public FragmentNavigator getFragmentNavigator() {
    return fragmentNavigator;
  }

  public Map<Integer, Result> getFragmentResultMap() {
    return fragmentResultMap;
  }

  public AccountNavigator getAccountNavigator() {
    return accountNavigator;
  }
}

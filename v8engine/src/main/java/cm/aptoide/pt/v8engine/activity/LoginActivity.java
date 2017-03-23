package cm.aptoide.pt.v8engine.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.LoginSignUpFragment;

public class LoginActivity extends ActivityView {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());

    getSupportFragmentManager().beginTransaction()
        .replace(R.id.replace_fragment, LoginSignUpFragment.newInstance(false, true, false))
        .commit();
  }

  @LayoutRes private int getLayoutId() {
    return R.layout.activity_login;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home: {
        finish();
        break;
      }
    }
    return super.onOptionsItemSelected(item);
  }
}

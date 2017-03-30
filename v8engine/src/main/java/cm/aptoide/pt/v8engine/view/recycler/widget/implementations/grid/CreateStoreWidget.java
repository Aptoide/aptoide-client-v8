package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.account.AccountNavigator;
import cm.aptoide.pt.v8engine.activity.CreateStoreActivity;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CreateStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by trinkes on 02/12/2016.
 */

public class CreateStoreWidget extends Widget<CreateStoreDisplayable> {

  private Button button;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;

  public CreateStoreWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    button = (Button) itemView.findViewById(R.id.create_store_action);
  }

  @Override public void bindView(CreateStoreDisplayable displayable) {
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    accountNavigator =
        new AccountNavigator(getFragmentNavigator(), accountManager, getActivityNavigator());
    if (accountManager.isLoggedIn()) {
      button.setText(R.string.create_store_displayable_button);
    } else {
      button.setText(R.string.login);
    }
    RxView.clicks(button).subscribe(aVoid -> {
      if (accountManager.isLoggedIn()) {
        button.setText(R.string.create_store_displayable_button);
        Intent intent = new Intent(getContext(), CreateStoreActivity.class);
        getContext().startActivity(intent);
      } else {
        button.setText(R.string.login);
        accountNavigator.navigateToAccountView();
      }
    });
  }
}

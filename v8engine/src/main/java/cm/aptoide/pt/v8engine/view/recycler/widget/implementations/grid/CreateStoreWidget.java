package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.accounts.AccountManager;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.CreateStoreActivity;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CreateStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by trinkes on 02/12/2016.
 */

public class CreateStoreWidget extends Widget<CreateStoreDisplayable> {

  private Button button;
  private AptoideAccountManager accountManager;

  public CreateStoreWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    button = (Button) itemView.findViewById(R.id.create_store_action);
  }

  @Override public void bindView(CreateStoreDisplayable displayable) {
    accountManager = AptoideAccountManager.getInstance(getContext(), Application.getConfiguration(),
        new SecureCoderDecoder.Builder(getContext().getApplicationContext()).create(),
        AccountManager.get(getContext().getApplicationContext()), new IdsRepositoryImpl(
            SecurePreferencesImplementation.getInstance(),
            getContext().getApplicationContext()));
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
        accountManager.openAccountManager(getContext());
      }
    });
  }
}

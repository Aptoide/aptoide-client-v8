package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.view.View;
import android.widget.Button;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by trinkes on 02/12/2016.
 */

public class CreateStoreWidget extends Widget<CreateStoreDisplayable> {

  private Button button;

  public CreateStoreWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    button = (Button) itemView.findViewById(R.id.create_store_action);
  }

  @Override public void bindView(CreateStoreDisplayable displayable) {
    if (AptoideAccountManager.isLoggedIn()) {
      button.setText(R.string.create_store_displayable_button);
    } else {
      button.setText(R.string.login);
    }
    RxView.clicks(button).subscribe(aVoid -> {
      if (AptoideAccountManager.isLoggedIn()) {
        button.setText(R.string.create_store_displayable_button);
        ShowMessage.asToast(itemView.getContext(), "create store");
      } else {
        button.setText(R.string.login);
        AptoideAccountManager.openAccountManager(getContext());
      }
    });
  }
}

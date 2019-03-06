package cm.aptoide.pt.editorialList;

import android.os.Bundle;
import cm.aptoide.pt.editorial.EditorialFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.view.settings.MyAccountFragment;

public class EditorialListNavigator {

  private final FragmentNavigator fragmentNavigator;

  public EditorialListNavigator(FragmentNavigator fragmentNavigator) {

    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToEditorial(String cardId) {
    Bundle bundle = new Bundle();
    bundle.putString("cardId", cardId);
    bundle.putBoolean("fromHome", false);
    EditorialFragment fragment = new EditorialFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateToMyAccount() {
    fragmentNavigator.navigateTo(MyAccountFragment.newInstance(), true);
  }
}

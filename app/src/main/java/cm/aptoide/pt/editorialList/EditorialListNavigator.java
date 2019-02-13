package cm.aptoide.pt.editorialList;

import android.os.Bundle;
import cm.aptoide.pt.app.view.EditorialFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;

public class EditorialListNavigator {

  private final FragmentNavigator fragmentNavigator;

  public EditorialListNavigator(FragmentNavigator fragmentNavigator) {

    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToEditorial(String cardId) {
    Bundle bundle = new Bundle();
    bundle.putString("cardId", cardId);
    EditorialFragment fragment = new EditorialFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }
}

package cm.aptoide.pt.view;

import cm.aptoide.pt.account.view.LoginSignUpCredentialsFragment;
import cm.aptoide.pt.account.view.MyAccountFragment;
import cm.aptoide.pt.account.view.store.ManageStoreFragment;
import cm.aptoide.pt.account.view.user.ManageUserFragment;
import cm.aptoide.pt.addressbook.view.AddressBookFragment;
import cm.aptoide.pt.updates.view.rollback.RollbackFragment;
import dagger.Subcomponent;

/**
 * Created by jose_messejana on 29-11-2017.
 */

@FragmentScopeTest @Subcomponent(modules = { FragmentModuleTest.class })
public interface FragmentComponentTest extends FragmentComponent{

  void inject(AddressBookFragment addressBookFragment);

  void inject(RollbackFragment rollbackFragment);

  void inject(LoginSignUpCredentialsFragment loginSignUpCredentialsFragment);

  void inject(ManageUserFragment manageUserFragment);

  void inject(ManageStoreFragment manageStoreFragment);

  void inject(MyAccountFragment myAccountFragment);
}
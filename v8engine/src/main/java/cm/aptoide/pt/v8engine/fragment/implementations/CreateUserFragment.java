package cm.aptoide.pt.v8engine.fragment.implementations;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.BaseToolbarFragment;

/**
 * Created by pedroribeiro on 18/11/16.
 */

public class CreateUserFragment extends BaseToolbarFragment {

  public static CreateUserFragment newInstance() {
    CreateUserFragment createUserFragment = new CreateUserFragment();
    return createUserFragment;
  }

  @Override public int getContentViewId() {
    return R.layout.activity_create_user;
  }
}

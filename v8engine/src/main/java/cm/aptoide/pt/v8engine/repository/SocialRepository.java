package cm.aptoide.pt.v8engine.repository;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by jdandrade on 21/11/2016.
 */
public class SocialRepository {
  public SocialRepository() {

  }

  public void share(Context context) {
    if (!AptoideAccountManager.isLoggedIn()) {
      ShowMessage.asSnack((Activity) context, R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> {
            AptoideAccountManager.openAccountManager(snackView.getContext());
          });
      return;
    }
    GenericDialogs.createGenericShareCancelMessage(context, "", "Share card with your followers?")
        .subscribe(eResponse -> {
          switch (eResponse) {
            case YES:
              Toast.makeText(context, "Sharing card with your followers...", Toast.LENGTH_SHORT)
                  .show();
              //SHARE CARD WS
              break;
            case NO:
            case CANCEL:
            default:
              break;
          }
        });
  }

  public void like() {

  }
}


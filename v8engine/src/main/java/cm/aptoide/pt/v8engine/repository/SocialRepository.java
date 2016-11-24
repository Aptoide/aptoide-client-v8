package cm.aptoide.pt.v8engine.repository;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.ShareCardRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import java.util.Date;
import java.util.List;
import rx.schedulers.Schedulers;

/**
 * Created by jdandrade on 21/11/2016.
 */
public class SocialRepository {
  public SocialRepository() {

  }

  public void share(Context context, String cardType, List<App> relatedToAppsList, String url,
      String articleTitle, String thumbnailUrl, String title, String developerLinkUrl,
      String avatarUrl, Date date, String cardId, String ownerHash) {
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
              ShareCardRequest.of("ARTICLE", relatedToAppsList, url, articleTitle, thumbnailUrl,
                  title, developerLinkUrl, avatarUrl, date, cardId, ownerHash,
                  AptoideAccountManager.getAccessToken(),
                  new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                      DataProvider.getContext()).getAptoideClientUUID(),
                  AptoideAccountManager.getUserEmail())
                  .observe()
                  .observeOn(Schedulers.io())
                  .subscribe(baseV7Response -> Logger.d(this.getClass().getSimpleName(),
                      baseV7Response.toString()));
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


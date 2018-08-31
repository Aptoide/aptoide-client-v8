package cm.aptoide.pt.app.view;

import android.content.SharedPreferences;
import android.util.Log;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.EditorialCard;
import cm.aptoide.pt.dataprovider.model.v7.EditorialCard.Data;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.listapp.File;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

import static cm.aptoide.pt.dataprovider.model.v7.EditorialCard.Content;

/**
 * Created by D01 on 29/08/2018.
 */

public class EditorialService {

  private final BodyInterceptor<BaseBody> bodyInterceptorPoolV7;
  private final OkHttpClient okHttpClient;
  private final TokenInvalidator tokenInvalidator;
  private final Converter.Factory converterFactory;
  private final SharedPreferences sharedPreferences;
  private boolean loading;

  public EditorialService(BodyInterceptor<BaseBody> bodyInterceptorPoolV7,
      OkHttpClient okHttpClient, TokenInvalidator tokenInvalidator,
      Converter.Factory converterFactory, SharedPreferences sharedPreferences) {

    this.bodyInterceptorPoolV7 = bodyInterceptorPoolV7;
    this.okHttpClient = okHttpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.converterFactory = converterFactory;
    this.sharedPreferences = sharedPreferences;
  }

  public Single<EditorialViewModel> loadEditorialViewModel(String cardId) {
    if (loading) {
      return Single.just(new EditorialViewModel(true));
    }
    return EditorialRequest.of(cardId, bodyInterceptorPoolV7, okHttpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe()
        .doOnSubscribe(() -> loading = true)
        .doOnUnsubscribe(() -> loading = false)
        .doOnTerminate(() -> loading = false)
        .flatMap(editorialCard -> mapEditorial(editorialCard))
        .toSingle()
        .onErrorReturn(throwable -> createErrorEditorialModel(throwable));
  }

  private EditorialViewModel createErrorEditorialModel(Throwable throwable) {
    Log.d("TAG123", throwable.toString());
    if (throwable instanceof NoNetworkConnectionException) {
      return new EditorialViewModel(EditorialViewModel.Error.NETWORK);
    } else {
      return new EditorialViewModel(EditorialViewModel.Error.GENERIC);
    }
  }

  private Observable<EditorialViewModel> mapEditorial(EditorialCard editorialCard) {
    if (editorialCard.isOk()) {
      Data card = editorialCard.getData();
      List<Content> contentList = card.getContent();
      String cardType = card.getType();
      App app = card.getApp();
      long appId = app.getId();
      String appName = app.getName();
      String packageName = app.getPackageName();
      long size = app.getSize();
      String icon = app.getIcon();
      String graphic = app.getGraphic();
      String uptype = app.getUptype();
      Store store = app.getStore();
      Obb obb = app.getObb();
      long storeId = store.getId();
      String storeName = store.getName();
      String storeAvatar = store.getAvatar();
      String storeTheme = store.getAppearance()
          .getTheme();
      File file = app.getFile();
      String vername = file.getVername();
      int vercode = file.getVercode();
      long fileSize = file.getFilesize();
      String path = file.getPath();
      String backgroundImage = card.getBackground_image();
      return Observable.just(
          new EditorialViewModel(contentList, cardType, appId, appName, packageName, size, icon,
              graphic, uptype, obb, storeId, storeName, storeAvatar, storeTheme, vername, vercode,
              fileSize, path, backgroundImage));
    } else {
      return Observable.error(new IllegalStateException("Could not obtain request from server."));
    }
  }
}

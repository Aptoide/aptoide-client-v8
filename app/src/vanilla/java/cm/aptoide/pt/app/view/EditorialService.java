package cm.aptoide.pt.app.view;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.EditorialCard;
import cm.aptoide.pt.dataprovider.model.v7.EditorialCard.Data;
import cm.aptoide.pt.dataprovider.model.v7.EditorialCard.Media;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.listapp.File;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import java.util.ArrayList;
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
    if (throwable instanceof NoNetworkConnectionException) {
      return new EditorialViewModel(EditorialViewModel.Error.NETWORK);
    } else {
      return new EditorialViewModel(EditorialViewModel.Error.GENERIC);
    }
  }

  private Observable<EditorialViewModel> mapEditorial(EditorialCard editorialCard) {
    if (editorialCard.isOk()) {
      Data card = editorialCard.getData();
      String cardType = card.getType();

      App app = card.getApp();
      long appId = app.getId();
      String appName = app.getName();
      String icon = app.getIcon();
      String rating = Float.toString(app.getStats()
          .getRating()
          .getAvg());
      List<Content> contentList = card.getContent();
      List<EditorialContent> editorialContentList = new ArrayList<>();
      editorialContentList =
          mapEditorialContent(contentList, editorialContentList, appName, icon, rating);

      String packageName = app.getPackageName();
      long size = app.getSize();
      String graphic = app.getGraphic();
      Store store = app.getStore();
      Obb obb = app.getObb();
      long storeId = store.getId();
      String storeName = store.getName();
      String storeTheme = store.getAppearance()
          .getTheme();
      File file = app.getFile();
      String vername = file.getVername();
      int vercode = file.getVercode();
      String path = file.getPath();
      String pathAlt = file.getPathAlt();
      String md5 = file.getMd5sum();
      String backgroundImage = card.getBackgroundImage();
      return Observable.just(
          new EditorialViewModel(editorialContentList, cardType, appId, appName, packageName, size,
              icon, graphic, obb, storeId, storeName, storeTheme, vername, vercode, path,
              backgroundImage, pathAlt, md5));
    } else {
      return Observable.error(new IllegalStateException("Could not obtain request from server."));
    }
  }

  private List<EditorialContent> mapEditorialContent(List<Content> contentList,
      List<EditorialContent> editorialContentList, String appName, String icon, String rating) {
    if (contentList != null) {
      for (Content content : contentList) {
        List<Media> mediaList = content.getMedia();
        List<EditorialMedia> editorialMediaList = new ArrayList<>();
        if (mediaList != null) {
          for (Media media : mediaList) {
            EditorialMedia editorialMedia =
                new EditorialMedia(media.getType(), media.getDescription(), media.getUrl());
            editorialMediaList.add(editorialMedia);
          }
        }
        EditorialContent editorialContent =
            new EditorialContent(content.getTitle(), editorialMediaList, content.getMessage(),
                content.getType(), appName, icon, rating);
        editorialContentList.add(editorialContent);
      }
    }
    return editorialContentList;
  }
}

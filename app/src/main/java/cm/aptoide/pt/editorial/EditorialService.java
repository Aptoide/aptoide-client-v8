package cm.aptoide.pt.editorial;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.EditorialCard;
import cm.aptoide.pt.dataprovider.model.v7.EditorialCard.Data;
import cm.aptoide.pt.dataprovider.model.v7.EditorialCard.Media;
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

import static cm.aptoide.pt.dataprovider.model.v7.EditorialCard.Action;
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

      List<Content> contentList = card.getContent();
      List<EditorialContent> editorialContentList = new ArrayList<>();
      editorialContentList = mapEditorialContent(contentList, editorialContentList);
      int placeHolderPosition = getPlaceHolderPosition(editorialContentList);
      App app = null;
      if (placeHolderPosition != -1) {
        app = contentList.get(placeHolderPosition)
            .getApp();
      }
      if (app != null) {
        Store store = app.getStore();
        File file = app.getFile();
        return Observable.just(
            new EditorialViewModel(editorialContentList, cardType, card.getTitle(), app.getId(),
                card.getCaption(), app.getName(), app.getStats()
                .getRating()
                .getAvg(), app.getPackageName(), app.getSize(), app.getIcon(), app.getGraphic(),
                app.getObb(), store.getId(), store.getName(), store.getName(), file.getVername(),
                file.getVercode(), file.getPath(), card.getBackground(), file.getPathAlt(),
                file.getMd5sum(), placeHolderPosition));
      } else {
        return Observable.just(
            new EditorialViewModel(editorialContentList, cardType, card.getTitle(),
                card.getCaption(), card.getBackground(), placeHolderPosition));
      }
    } else {
      return Observable.error(new IllegalStateException("Could not obtain request from server."));
    }
  }

  private int getPlaceHolderPosition(List<EditorialContent> editorialContentList) {
    if (editorialContentList != null) {
      for (int i = 0; i < editorialContentList.size(); i++) {
        EditorialContent editorialContent = editorialContentList.get(i);
        if (editorialContent.isPlaceHolderType()) {
          return i;
        }
      }
    }
    return -1;
  }

  private List<EditorialContent> mapEditorialContent(List<Content> contentList,
      List<EditorialContent> editorialContentList) {
    if (contentList != null) {
      for (Content content : contentList) {
        List<Media> mediaList = content.getMedia();
        List<EditorialMedia> editorialMediaList = new ArrayList<>();
        if (mediaList != null) {
          for (Media media : mediaList) {
            EditorialMedia editorialMedia =
                new EditorialMedia(media.getType(), media.getDescription(), media.getThumbnail(),
                    media.getImage());
            editorialMediaList.add(editorialMedia);
          }
        }
        EditorialContent editorialContent;
        App app = content.getApp();
        Action action = content.getAction();
        String actionTitle = "";
        String actionUrl = "";
        if (action != null) {
          actionTitle = action.getTitle();
          actionUrl = action.getUrl();
        }
        String appName = null;
        String icon = null;
        float rating = 0;
        if (app != null) {
          appName = app.getName();
          icon = app.getIcon();
          rating = app.getStats()
              .getRating()
              .getAvg();
        }
        editorialContent =
            new EditorialContent(content.getTitle(), editorialMediaList, content.getMessage(),
                content.getType(), appName, icon, rating, actionTitle, actionUrl);
        editorialContentList.add(editorialContent);
      }
    }
    return editorialContentList;
  }
}

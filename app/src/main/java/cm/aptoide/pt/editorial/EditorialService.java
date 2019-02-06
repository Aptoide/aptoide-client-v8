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

      List<Content> contentList = card.getContent();

      List<EditorialContent> editorialContentList = mapEditorialContent(contentList);

      List<Integer> placeHolderPositions = getPlaceHolderPositions(editorialContentList);

      List<EditorialContent> placeHolderContent =
          buildPlaceHolderContent(editorialContentList, placeHolderPositions);

      return Observable.just(
          new EditorialViewModel(editorialContentList, card.getTitle(), card.getCaption(),
              card.getBackground(), placeHolderPositions, placeHolderContent));
    } else {
      return Observable.error(new IllegalStateException("Could not obtain request from server."));
    }
  }

  private List<EditorialContent> mapEditorialContent(List<Content> contentList) {
    List<EditorialContent> editorialContentList = new ArrayList<>();
    if (contentList != null) {
      for (int position = 0; position < contentList.size(); position++) {
        Content content = contentList.get(position);
        List<EditorialMedia> editorialMediaList = buildMediaList(content.getMedia());
        App app = content.getApp();
        Action action = content.getAction();
        EditorialContent editorialContent =
            buildEditorialContent(content, editorialMediaList, app, action, position);
        editorialContentList.add(editorialContent);
      }
    }
    return editorialContentList;
  }

  private List<Integer> getPlaceHolderPositions(List<EditorialContent> editorialContentList) {
    List<Integer> placeHolderPositions = new ArrayList<>();
    if (editorialContentList != null) {
      for (int contendIndex = 0; contendIndex < editorialContentList.size(); contendIndex++) {
        EditorialContent editorialContent = editorialContentList.get(contendIndex);
        if (editorialContent.isPlaceHolderType()) {
          placeHolderPositions.add(contendIndex);
        }
      }
    }
    return placeHolderPositions;
  }

  private List<EditorialContent> buildPlaceHolderContent(
      List<EditorialContent> editorialContentList, List<Integer> placeHolderPositions) {
    List<EditorialContent> placeHolderContent = new ArrayList<>();
    for (Integer placeHolderPosition : placeHolderPositions) {
      placeHolderContent.add(editorialContentList.get(placeHolderPosition));
    }
    return placeHolderContent;
  }

  private List<EditorialMedia> buildMediaList(List<Media> mediaList) {
    List<EditorialMedia> editorialMediaList = new ArrayList<>();
    if (mediaList != null) {
      for (Media media : mediaList) {
        EditorialMedia editorialMedia =
            new EditorialMedia(media.getType(), media.getDescription(), media.getThumbnail(),
                media.getImage());
        editorialMediaList.add(editorialMedia);
      }
    }
    return editorialMediaList;
  }

  private EditorialContent buildEditorialContent(Content content,
      List<EditorialMedia> editorialMediaList, App app, Action action, int position) {
    if (action != null && app != null) {
      Store store = app.getStore();
      File file = app.getFile();
      return new EditorialContent(content.getTitle(), editorialMediaList, content.getMessage(),
          content.getType(), app.getId(), app.getName(), app.getIcon(), app.getStats()
          .getRating()
          .getAvg(), app.getPackageName(), app.getSize(), app.getGraphic(), app.getObb(),
          store.getId(), store.getName(), file.getVername(), file.getVercode(), file.getPath(),
          file.getPathAlt(), file.getMd5sum(), action.getTitle(), action.getUrl(), position);
    }
    if (app != null) {
      Store store = app.getStore();
      File file = app.getFile();
      return new EditorialContent(content.getTitle(), editorialMediaList, content.getMessage(),
          content.getType(), app.getId(), app.getName(), app.getIcon(), app.getStats()
          .getRating()
          .getAvg(), app.getPackageName(), app.getSize(), app.getGraphic(), app.getObb(),
          store.getId(), store.getName(), file.getVername(), file.getVercode(), file.getPath(),
          file.getPathAlt(), file.getMd5sum(), position);
    }
    if (action != null) {
      return new EditorialContent(content.getTitle(), editorialMediaList, content.getMessage(),
          content.getType(), action.getTitle(), action.getUrl(), position);
    }
    return new EditorialContent(content.getTitle(), editorialMediaList, content.getMessage(),
        content.getType(), position);
  }
}

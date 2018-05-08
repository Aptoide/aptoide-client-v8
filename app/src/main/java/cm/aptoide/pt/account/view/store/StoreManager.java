package cm.aptoide.pt.account.view.store;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import cm.aptoide.accountmanager.SocialLink;
import cm.aptoide.pt.account.view.exception.InvalidImageException;
import cm.aptoide.pt.account.view.exception.SocialLinkException;
import cm.aptoide.pt.account.view.exception.StoreCreationException;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CheckUserCredentialsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SetStoreImageRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SimpleSetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.store.StoreUtilsProxy;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class StoreManager implements cm.aptoide.accountmanager.StoreManager {

  private static final String ERROR_CODE_2 = "WOP-2";
  private static final String ERROR_CODE_3 = "WOP-3";
  private static final String ERROR_API_1 = "API-1";
  private static final String ERROR_STORE_9 = "STORE-9";

  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final BodyInterceptor<HashMapNotNull<String, RequestBody>> multipartBodyInterceptor;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorV7;
  private final SharedPreferences sharedPreferences;
  private final TokenInvalidator tokenInvalidator;
  private final RequestBodyFactory requestBodyFactory;
  private final ObjectMapper objectMapper;
  private final StoreRepository storeRepository;
  private final StoreUtilsProxy storeUtilsProxy;

  public StoreManager(OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor<HashMapNotNull<String, RequestBody>> multipartBodyInterceptor,
      BodyInterceptor<BaseBody> bodyInterceptorV3,
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorV7,
      SharedPreferences sharedPreferences, TokenInvalidator tokenInvalidator,
      RequestBodyFactory requestBodyFactory, ObjectMapper objectMapper,
      StoreRepository storeRepository, StoreUtilsProxy storeUtilsProxy) {
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.multipartBodyInterceptor = multipartBodyInterceptor;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.bodyInterceptorV7 = bodyInterceptorV7;
    this.sharedPreferences = sharedPreferences;
    this.tokenInvalidator = tokenInvalidator;
    this.requestBodyFactory = requestBodyFactory;
    this.objectMapper = objectMapper;
    this.storeRepository = storeRepository;
    this.storeUtilsProxy = storeUtilsProxy;
  }

  public Completable createOrUpdate(String storeName, String storeDescription,
      String storeImagePath, boolean hasNewAvatar, String storeThemeName, boolean storeExists,
      List<SocialLink> storeLinksList, List<Store.SocialChannelType> storeDeleteLinksList) {
    return Completable.defer(() -> {
      if (storeExists) {
        return updateStore(storeName, storeDescription, storeImagePath, hasNewAvatar,
            storeThemeName, socialLinkToStoreLink(storeLinksList), storeDeleteLinksList);
      }
      return createStore(storeName, storeDescription, storeImagePath, hasNewAvatar, storeThemeName,
          socialLinkToStoreLink(storeLinksList), storeDeleteLinksList);
    })
        .onErrorResumeNext(err -> getOnErrorCompletable(err));
  }

  public Observable<Boolean> isSubscribed(long storeId) {
    return storeRepository.isSubscribed(storeId);
  }

  public Observable<Boolean> isSubscribed(String storeName) {
    return storeRepository.isSubscribed(storeName);
  }

  public Observable<GetStoreMeta> subscribeStore(String storeName) {
    return storeUtilsProxy.subscribeStoreObservable(storeName);
  }

  @NonNull private Completable getOnErrorCompletable(Throwable err) {
    if (err instanceof StoreCreationException || err instanceof InvalidImageException) {
      return Completable.error(err);
    }
    if (err instanceof AptoideWsV7Exception) {
      if (((AptoideWsV7Exception) err).getBaseResponse()
          .getErrors()
          .get(0)
          .getCode()
          .equals(ERROR_API_1)) {
        return Completable.error(new InvalidImageException(
            Collections.singletonList(InvalidImageException.ImageError.API_ERROR)));
      } else if (((AptoideWsV7Exception) err).getBaseResponse()
          .getErrors()
          .get(0)
          .getCode()
          .equals(ERROR_STORE_9)) {
        return Completable.error(new SocialLinkException(
            ((AptoideWsV7Exception) err).getBaseResponse()
                .getErrors()
                .get(0)
                .getDetails()
                .getStoreLinks()));
      } else {
        return Completable.error(new InvalidImageException(
            Collections.singletonList(InvalidImageException.ImageError.API_ERROR),
            err.getMessage()));
      }
    }

    // it's an unknown error
    return Completable.error(err);
  }

  private List<SimpleSetStoreRequest.StoreLinks> socialLinkToStoreLink(
      List<SocialLink> socialLinksList) {
    List<SimpleSetStoreRequest.StoreLinks> storeLinks = new ArrayList<>();
    for (SocialLink socialLink : socialLinksList) {
      storeLinks.add(
          new SimpleSetStoreRequest.StoreLinks(socialLink.getType(), socialLink.getUrl()));
    }
    return storeLinks;
  }

  private Completable createStore(String storeName, String storeDescription, String storeImage,
      boolean hasNewAvatar, String storeThemeName,
      List<SimpleSetStoreRequest.StoreLinks> storeLinksList,
      List<Store.SocialChannelType> storeDeleteSocialLinksList) {

    if (TextUtils.isEmpty(storeName)) {
      return Completable.error(new StoreValidationException(StoreValidationException.EMPTY_NAME));
    }

    /*
     * To create a store we need to call WS CheckUserCredentials so we can associate a
     * user to a newly created store.
     *
     * Then, if we have more data we either use a SetStore with multi-part request if we have
     * a store image, or a SetStore without image. This is the edit store use case {@link
     * #updateStore}.
     */
    return CheckUserCredentialsRequest.toCreateStore(bodyInterceptorV3, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences, storeName)
        .observe()
        .toSingle()
        .flatMap(data -> {
          final List<ErrorResponse> errors = data.getErrors();
          if (errors != null && !errors.isEmpty() && errors.get(0).code.equals(ERROR_CODE_2)) {
            return Single.error(new StoreCreationException());
          } else if (errors != null && errors.size() > 0 && errors.get(0).code.equals(
              ERROR_CODE_3)) {
            return Single.error(new StoreCreationException(errors.get(0).code));
          }

          return Single.just(data);
        })
        .flatMapCompletable(data -> {
          if (needToUploadMoreStoreData(storeDescription, storeImage, hasNewAvatar,
              storeThemeName)) {
            return updateStore(storeName, storeDescription, storeImage, hasNewAvatar,
                storeThemeName, storeLinksList, storeDeleteSocialLinksList);
          }
          return Completable.complete();
        });
  }

  private boolean needToUploadMoreStoreData(String storeDescription, String storeImage,
      boolean hasNewAvatar, String storeThemeName) {
    return !TextUtils.isEmpty(storeDescription) || (hasNewAvatar && !TextUtils.isEmpty(storeImage)
        || !storeThemeName.equals(StoreTheme.DEFAULT.toString()
        .toLowerCase()));
  }

  private Completable updateStore(String storeName, String storeDescription, String storeImage,
      boolean hasNewAvatar, String storeThemeName,
      List<SimpleSetStoreRequest.StoreLinks> storeLinksList,
      List<Store.SocialChannelType> socialDeleteLinksList) {

    if (TextUtils.isEmpty(storeName)) {
      return Completable.error(new StoreValidationException(StoreValidationException.EMPTY_NAME));
    } else if (hasNewAvatar && TextUtils.isEmpty(storeImage)) {
      return Completable.error(new StoreValidationException(StoreValidationException.EMPTY_AVATAR));
    }

    /*
     * If we have more data we either use a SetStore with multi-part request if we have
     * a store image, or a SetStore without image.
     */
    if (hasNewAvatar) {
      return updateStoreWithAvatar(storeName, storeDescription, storeThemeName, storeImage,
          storeLinksList, socialDeleteLinksList);
    }

    return updateStoreWithoutAvatar(storeName, storeDescription, storeThemeName, storeLinksList,
        socialDeleteLinksList);
  }

  private Completable updateStoreWithoutAvatar(String storeName, String storeDescription,
      String storeThemeName, List<SimpleSetStoreRequest.StoreLinks> storeLinksList,
      List<Store.SocialChannelType> storeDeleteSocialLinksList) {
    return SimpleSetStoreRequest.of(storeName, storeThemeName, storeDescription, bodyInterceptorV7,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences, storeLinksList,
        storeDeleteSocialLinksList)
        .observe()
        .toCompletable();
  }

  private Completable updateStoreWithAvatar(String storeName, String storeDescription,
      String storeThemeName, String storeImagePath,
      List<SimpleSetStoreRequest.StoreLinks> storeLinksList,
      List<Store.SocialChannelType> socialDeleteLinksList) {
    return SetStoreImageRequest.of(storeName, storeThemeName, storeDescription, storeImagePath,
        multipartBodyInterceptor, httpClient, converterFactory, requestBodyFactory, objectMapper,
        sharedPreferences, tokenInvalidator, storeLinksList, socialDeleteLinksList)
        .observe()
        .toSingle()
        .toCompletable();
  }
}

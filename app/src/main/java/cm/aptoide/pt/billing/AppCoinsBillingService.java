package cm.aptoide.pt.billing;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import cm.aptoide.pt.PackageRepository;
import cm.aptoide.pt.R;
import cm.aptoide.pt.billing.exception.ProductNotFoundException;
import cm.aptoide.pt.billing.product.InAppProduct;
import cm.aptoide.pt.billing.product.InAppPurchase;
import cm.aptoide.pt.billing.product.PaidAppProduct;
import cm.aptoide.pt.billing.product.PaidAppPurchase;
import cm.aptoide.pt.billing.product.ProductFactory;
import cm.aptoide.pt.billing.product.SimplePurchase;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.dataprovider.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingAvailableRequest;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingPurchasesRequest;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingSkuDetailsRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by jose_messejana on 26-10-2017.
 */

public class AppCoinsBillingService implements BillingService {

  public static final String TAG = AppCoinsBillingService.class.getSimpleName();

  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final PurchaseMapper purchaseMapper;
  private final ProductFactory productFactory;
  private final PackageRepository packageRepository;
  private final PaymentMethodMapper paymentMethodMapper;
  private final Resources resources;
  private final BillingIdResolver idResolver;
  private final int apiVersion;
  private InAppPurchase inAppPurchase = null;
  private PurchaseFile purchaseFile;

  public AppCoinsBillingService(BodyInterceptor<BaseBody> bodyInterceptorV3,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      PurchaseMapper purchaseMapper, ProductFactory productFactory,
      PackageRepository packageRepository, PaymentMethodMapper paymentMethodMapper,
      Resources resources, BillingIdResolver idResolver, int apiVersion) {
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.purchaseMapper = purchaseMapper;
    this.productFactory = productFactory;
    this.packageRepository = packageRepository;
    this.paymentMethodMapper = paymentMethodMapper;
    this.resources = resources;
    this.idResolver = idResolver;
    this.apiVersion = apiVersion;

    String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        .getPath() + File.separator + "orders.json";
    Logger.v(TAG, "file exists: " + filePath + " " + FileUtils.fileExists(filePath));

    Gson gson = new GsonBuilder().create();
    try {
      //get file with all order ids
      purchaseFile = gson.fromJson(FileUtils.loadJSON(filePath), PurchaseFile.class);

      //get the first element of the list
      Logger.v(TAG, "length: " + purchaseFile.list.length);
      Logger.v(TAG, "id: " + purchaseFile.list[0].id);
    } catch (Exception e) {
      e.printStackTrace();
      Logger.e(TAG, "error: ", e);
    }
  }

  private void deleteFirst() {

    purchaseFile.list = Arrays.copyOfRange(purchaseFile.list, 1, purchaseFile.list.length);
  }

  @Override public Single<List<PaymentMethod>> getPaymentMethods(Product product) {
    List<PaymentMethod> paymentList = new ArrayList<>();
    paymentList.add(new PaymentMethod(12, "", "", R.drawable.appcoins_logo));
    return Single.just(paymentList);
  }

  @Override public Completable getBilling(String sellerId, String type) {
    return InAppBillingAvailableRequest.of(apiVersion, idResolver.resolvePackageName(sellerId),
        type, bodyInterceptorV3, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe()
        .toSingle()
        .flatMapCompletable(response -> {
          if (response != null && response.isOk()) {
            if (response.getInAppBillingAvailable()
                .isAvailable()) {
              return Completable.complete();
            } else {
              return Completable.error(new IllegalArgumentException(V3.getErrorMessage(response)));
            }
          } else {
            return Completable.error(new IllegalArgumentException(V3.getErrorMessage(response)));
          }
        });
  }

  @Override public Completable deletePurchase(String sellerId, String purchaseToken) {
    inAppPurchase = null;
    return Completable.complete();
  }

  @Override public Single<List<Purchase>> getPurchases(String sellerId) {
    if (inAppPurchase != null) {
      List<Purchase> paymentList = new ArrayList<>();
      paymentList.add(inAppPurchase);
      return Single.just(paymentList);
    } else {
      return Single.just(Collections.emptyList());
    }
  }

  @Override public Single<Purchase> getPurchase(String sellerId, String purchaseToken) {
    return Single.just(inAppPurchase);
  }

  @Override public Single<List<Product>> getProducts(String sellerId, List<String> productIds) {
    return getServerSKUs(apiVersion, idResolver.resolvePackageName(sellerId),
        idResolver.resolveSkus(productIds), false).flatMap(
        response -> mapToProducts(apiVersion, idResolver.resolvePackageName(sellerId), response));
  }

  @Override public Single<Purchase> getPurchase(Product product) {
    String signature =
        "O02IDIBMIkkM1YVQMM8vUWeTDq6ITLYN0xFa5YyS2B72K58dpyyQltZDBy3S+Q8nMp51jUXOXHX7mcuvaw+ZTohKJUTMUJtP31M260wUdoZNMjNq3/hev2BJMjfJLpRkxquvq3fTXYZi5PPrMCWbDPx2cOZqdwoGIxulEiJykltCdfE97j15Um2Nw0c33J5McQIi1gODs5xLXXkEeMKjDEjuB3wz4AkkyAWNwy2tBIXqGoJw3bPve5WjtXX/4BJ9PdglpEa+tJawDLdKkJ7AECAbFnf1U7Ma4CV3ngO2or38wNsQcWx0phk1ey6DHoHWqnOegiE3WxVHI2X9F92UGQ==";
    JSONObject json = new JSONObject();
    try {
      json.put("orderId", "26"); // 21
      json.put("packageName", "com.marceloporto.bombastic");
      json.put("productId", "com.marceloporto.bombastic.mediumpack");
      json.put("purchaseTime", 1509408000);
      json.put("purchaseToken", "Mjc");
      json.put("purchaseState", 0);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    String sku = "com.marceloporto.bombastic.mediumpack";
    String token = "Mjc";

    PurchaseFile.PurchaseOrderItem purchaseOrderItem = purchaseFile.list[0];
    signature = purchaseOrderItem.signature;
    String signatureData = purchaseOrderItem.data.toString();
    sku = purchaseOrderItem.product.sku;
    token = purchaseOrderItem.data.developer_purchase.purchaseToken;

    Logger.v(TAG, "signature: " + signature);
    Logger.v(TAG, "signatureData: " + signatureData);
    Logger.v(TAG, "sku: " + sku);
    Logger.v(TAG, "token: " + token);

    try {
      // after using the first element, delete it
      deleteFirst();
      Logger.v(TAG, "after deleting one item of the array: length: " + purchaseFile.list.length);
      //then save the file again
      boolean res = FileUtils.saveToFile(
          Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
              .getPath() + File.separator + "orders.json", new Gson().toJson(purchaseFile));
      Logger.v(TAG, "save to file: " + res);
    } catch (Exception e) {
      e.printStackTrace();
      Logger.e(TAG, "error: ", e);
    }

    if (product instanceof InAppProduct) {
      inAppPurchase =
          new InAppPurchase(signature, signatureData, sku, token, SimplePurchase.Status.COMPLETED,
              product.getId());
      return Single.just(inAppPurchase);
    } else if (product instanceof PaidAppProduct) {
      return Single.just(
          new PaidAppPurchase("done", SimplePurchase.Status.COMPLETED, product.getId()));
    }

    throw new IllegalArgumentException("Invalid product. Must be "
        + InAppProduct.class.getSimpleName()
        + " or "
        + PaidAppProduct.class.getSimpleName());
  }

  @Override public Single<Product> getProduct(String sellerId, String productId) {
    if (idResolver.isInAppId(productId)) {
      return getInAppProduct(sellerId, productId);
    }
    if (idResolver.isPaidAppId(productId)) {
      return getPaidAppProduct(productId);
    }

    return Single.error(new IllegalArgumentException("Invalid product id " + productId));
  }

  private Single<Product> getInAppProduct(String sellerId, String productId) {
    return getServerSKUs(apiVersion, idResolver.resolvePackageName(sellerId),
        Collections.singletonList(idResolver.resolveSku(productId)), false).flatMap(
        response -> mapToProducts(apiVersion, idResolver.resolvePackageName(sellerId), response))
        .flatMap(products -> {
          if (products.isEmpty()) {
            return Single.error(new ProductNotFoundException(
                "No product found for sku: " + idResolver.resolveSku(productId)));
          }
          return Single.just(products.get(0));
        });
  }

  public Single<Product> getPaidAppProduct(String productId) {
    return getServerPaidApp(false, idResolver.resolveAppId(productId)).map(
        paidApp -> productFactory.create(paidApp));
  }

  private Single<InAppBillingSkuDetailsResponse> getServerSKUs(int apiVersion, String packageName,
      List<String> skuList, boolean bypassCache) {
    return InAppBillingSkuDetailsRequest.of(apiVersion, packageName, skuList, bodyInterceptorV3,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe(bypassCache)
        .first()
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(response);
          } else {
            return Single.error(new IllegalArgumentException(V3.getErrorMessage(response)));
          }
        });
  }

  private Observable<InAppBillingPurchasesResponse> getServerInAppPurchase(int apiVersion,
      String packageName, boolean bypassCache) {
    return packageRepository.getPackageVersionCode(packageName)
        .flatMapObservable(
            packageVersionCode -> InAppBillingPurchasesRequest.of(apiVersion, packageName,
                bodyInterceptorV3, httpClient, converterFactory, tokenInvalidator,
                sharedPreferences, packageVersionCode)
                .observe(bypassCache)
                .flatMap(response -> {
                  if (response != null && response.isOk()) {
                    return Observable.just(response);
                  }
                  return Observable.error(
                      new IllegalArgumentException(V3.getErrorMessage(response)));
                }));
  }

  private boolean isDeletionItemNotFound(List<ErrorResponse> errors) {
    for (ErrorResponse error : errors) {
      if (error.code.equals("PRODUCT-201")) {
        return true;
      }
    }
    return false;
  }

  private Single<PaidApp> getServerPaidApp(boolean bypassCache, long appId) {
    return GetApkInfoRequest.of(appId, bodyInterceptorV3, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences, resources)
        .observe(bypassCache)
        .flatMap(response -> {
          if (response != null && response.isOk() && response.isPaid()) {
            return Observable.just(response);
          } else {
            return Observable.error(new IllegalArgumentException(V3.getErrorMessage(response)));
          }
        })
        .first()
        .toSingle();
  }

  private Single<List<Product>> mapToProducts(int apiVersion, String packageName,
      InAppBillingSkuDetailsResponse response) {
    return Single.zip(packageRepository.getPackageVersionCode(packageName),
        packageRepository.getPackageLabel(packageName),
        (packageVersionCode, applicationName) -> productFactory.create(apiVersion, packageName,
            response, packageVersionCode, applicationName));
  }
}

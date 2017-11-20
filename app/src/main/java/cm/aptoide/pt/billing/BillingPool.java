package cm.aptoide.pt.billing;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.billing.authorization.AuthorizationRepository;
import cm.aptoide.pt.billing.authorization.LocalIdGenerator;
import cm.aptoide.pt.billing.customer.AccountCustomer;
import cm.aptoide.pt.billing.external.ExternalBillingSerializer;
import cm.aptoide.pt.billing.networking.AuthorizationMapperV3;
import cm.aptoide.pt.billing.networking.AuthorizationMapperV7;
import cm.aptoide.pt.billing.networking.AuthorizationServiceV3;
import cm.aptoide.pt.billing.networking.AuthorizationServiceV7;
import cm.aptoide.pt.billing.networking.BillingIdManagerV3;
import cm.aptoide.pt.billing.networking.BillingIdManagerV7;
import cm.aptoide.pt.billing.networking.BillingServiceV3;
import cm.aptoide.pt.billing.networking.BillingServiceV7;
import cm.aptoide.pt.billing.networking.PaymentServiceMapper;
import cm.aptoide.pt.billing.networking.ProductMapperV3;
import cm.aptoide.pt.billing.networking.ProductMapperV7;
import cm.aptoide.pt.billing.networking.PurchaseMapperV3;
import cm.aptoide.pt.billing.networking.PurchaseMapperV7;
import cm.aptoide.pt.billing.networking.TransactionMapperV3;
import cm.aptoide.pt.billing.networking.TransactionMapperV7;
import cm.aptoide.pt.billing.networking.TransactionServiceV3;
import cm.aptoide.pt.billing.networking.TransactionServiceV7;
import cm.aptoide.pt.billing.payment.Adyen;
import cm.aptoide.pt.billing.payment.PaymentService;
import cm.aptoide.pt.billing.payment.SharedPreferencesPaymentServiceSelector;
import cm.aptoide.pt.billing.persistence.InMemoryTransactionPersistence;
import cm.aptoide.pt.billing.persistence.RealmAuthorizationMapper;
import cm.aptoide.pt.billing.persistence.RealmAuthorizationPersistence;
import cm.aptoide.pt.billing.purchase.Base64PurchaseTokenDecoder;
import cm.aptoide.pt.billing.purchase.PurchaseFactory;
import cm.aptoide.pt.billing.sync.BillingSyncFactory;
import cm.aptoide.pt.billing.sync.BillingSyncManager;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
import cm.aptoide.pt.billing.transaction.TransactionPersistence;
import cm.aptoide.pt.billing.transaction.TransactionRepository;
import cm.aptoide.pt.billing.transaction.TransactionService;
import cm.aptoide.pt.crashreports.CrashLogger;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.install.PackageRepository;
import cm.aptoide.pt.networking.AuthenticationPersistence;
import cm.aptoide.pt.preferences.Preferences;
import cm.aptoide.pt.sync.SyncScheduler;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.schedulers.Schedulers;

public class BillingPool {

  private final Map<String, Billing> pool;
  private final SharedPreferences sharedPreferences;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final AptoideAccountManager accountManager;
  private final Database database;
  private final Resources resources;
  private final PackageRepository packageRepository;
  private final TokenInvalidator tokenInvalidator;
  private final SyncScheduler syncScheduler;
  private final ExternalBillingSerializer externalBillingSerializer;
  private final BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7;
  private final BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody>
      accountSettingsBodyInterceptorPoolV7;
  private final Converter.Factory converterFactory;
  private final CrashLogger crashLogger;
  private final Adyen adyen;
  private final PurchaseFactory purchaseFactory;
  private final int minimumAPILevelPayPal;
  private final int minimumAPILevelAdyen;
  private final AuthenticationPersistence authenticationPersistence;
  private final Preferences preferences;

  private BillingSyncScheduler billingSyncSchedulerV7;
  private AuthorizationRepository inAppAuthorizationRepository;
  private TransactionRepository inAppTransactionRepository;
  private TransactionService transactionServiceV7;
  private BillingServiceV7 billingServiceV7;
  private BillingIdManager billingIdManagerV7;

  private BillingSyncScheduler billingSyncSchedulerV3;
  private AuthorizationRepository paidAppAuthorizationRepository;
  private TransactionRepository paidAppTransactionRepository;
  private TransactionService transactionServiceV3;
  private BillingService billingServiceV3;
  private BillingIdManager billingIdManagerV3;

  private PaymentServiceSelector serviceSelector;
  private AuthorizationPersistence authorizationPersistence;
  private TransactionPersistence transactionPersistence;
  private Customer customer;
  private TransactionFactory transactionFactory;
  private AuthorizationFactory authorizationFactory;
  private PurchaseTokenDecoder purchaseTokenDecoder;
  private TransactionMapperV3 transactionMapperV3;
  private LocalIdGenerator localIdGenerator;

  public BillingPool(SharedPreferences sharedPreferences,
      BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient httpClient,
      AptoideAccountManager accountManager, Database database, Resources resources,
      PackageRepository packageRepository, TokenInvalidator tokenInvalidator,
      SyncScheduler syncScheduler, ExternalBillingSerializer externalBillingSerializer,
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> accountSettingsBodyInterceptorPoolV7,
      HashMap<String, Billing> poll, Converter.Factory converterFactory, CrashReport crashLogger,
      Adyen adyen, PurchaseFactory purchaseFactory, int minimumAPILevelPayPal,
      int minimumAPILevelAdyen, AuthenticationPersistence authenticationPersistence,
      Preferences preferences) {
    this.sharedPreferences = sharedPreferences;
    this.pool = poll;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.httpClient = httpClient;
    this.accountManager = accountManager;
    this.database = database;
    this.resources = resources;
    this.packageRepository = packageRepository;
    this.tokenInvalidator = tokenInvalidator;
    this.syncScheduler = syncScheduler;
    this.externalBillingSerializer = externalBillingSerializer;
    this.bodyInterceptorPoolV7 = bodyInterceptorPoolV7;
    this.accountSettingsBodyInterceptorPoolV7 = accountSettingsBodyInterceptorPoolV7;
    this.converterFactory = converterFactory;
    this.crashLogger = crashLogger;
    this.adyen = adyen;
    this.purchaseFactory = purchaseFactory;
    this.minimumAPILevelPayPal = minimumAPILevelPayPal;
    this.minimumAPILevelAdyen = minimumAPILevelAdyen;
    this.authenticationPersistence = authenticationPersistence;
    this.preferences = preferences;
  }

  public Billing get(String merchantName) {
    if (!pool.containsKey(merchantName)) {
      pool.put(merchantName, create(merchantName));
    }
    return pool.get(merchantName);
  }

  public BillingIdManager getIdResolver(String merchantName) {
    if (merchantName.equals(BuildConfig.APPLICATION_ID)) {
      return getBillingIdManagerV3();
    } else {
      return getBillingIdManagerV7();
    }
  }

  private Billing create(String merchantName) {
    if (merchantName.equals(BuildConfig.APPLICATION_ID)) {
      return new Billing(merchantName, getBillingServiceV3(), getPaidAppTransactionRepository(),
          getPaidAppAuthorizationRepository(), getServiceSelector(), getCustomer(),
          getPurchaseTokenDecoder(), getBillingSyncSchedulerV3());
    } else {
      return new Billing(merchantName, getBillingServiceV7(), getInAppTransactionRepository(),
          getInAppAuthorizationRepository(), getServiceSelector(), getCustomer(),
          getPurchaseTokenDecoder(), getBillingSyncSchedulerV7());
    }
  }

  private TransactionRepository getPaidAppTransactionRepository() {
    if (paidAppAuthorizationRepository == null) {
      paidAppTransactionRepository =
          new TransactionRepository(getTransactionPersistence(), getBillingSyncSchedulerV3(),
              getCustomer(), getTransactionServiceV3());
    }
    return paidAppTransactionRepository;
  }

  private AuthorizationRepository getPaidAppAuthorizationRepository() {
    if (paidAppAuthorizationRepository == null) {
      paidAppAuthorizationRepository =
          new AuthorizationRepository(getBillingSyncSchedulerV3(), getCustomer(),
              getAuthorizationPersistence(), authorizationFactory);
    }
    return paidAppAuthorizationRepository;
  }

  private BillingService getBillingServiceV3() {
    if (billingServiceV3 == null) {
      billingServiceV3 =
          new BillingServiceV3(bodyInterceptorV3, httpClient, converterFactory, tokenInvalidator,
              sharedPreferences, new PurchaseMapperV3(purchaseFactory),
              new ProductMapperV3(getBillingIdManagerV3()), resources,
              new PaymentService(getBillingIdManagerV3().generateServiceId(1),
                  PaymentServiceMapper.PAYPAL, "PayPal", null, ""), getBillingIdManagerV3(),
              Build.VERSION.SDK_INT, minimumAPILevelPayPal);
    }
    return billingServiceV3;
  }

  private BillingService getBillingServiceV7() {
    if (billingServiceV7 == null) {
      billingServiceV7 =
          new BillingServiceV7(accountSettingsBodyInterceptorPoolV7, httpClient, converterFactory,
              tokenInvalidator, sharedPreferences,
              new PurchaseMapperV7(externalBillingSerializer, getBillingIdManagerV7(),
                  purchaseFactory), new ProductMapperV7(getBillingIdManagerV7()), packageRepository,
              new PaymentServiceMapper(crashLogger, getBillingIdManagerV7(), adyen,
                  Build.VERSION.SDK_INT, minimumAPILevelAdyen, minimumAPILevelPayPal),
              getBillingIdManagerV7(), purchaseFactory);
    }
    return billingServiceV7;
  }

  private AuthorizationRepository getInAppAuthorizationRepository() {
    if (inAppAuthorizationRepository == null) {
      inAppAuthorizationRepository =
          new AuthorizationRepository(getBillingSyncSchedulerV7(), getCustomer(),
              getAuthorizationPersistence(), authorizationFactory);
    }
    return inAppAuthorizationRepository;
  }

  private TransactionRepository getInAppTransactionRepository() {
    if (inAppTransactionRepository == null) {
      inAppTransactionRepository =
          new TransactionRepository(getTransactionPersistence(), getBillingSyncSchedulerV7(),
              getCustomer(), getTransactionServiceV7());
    }
    return inAppTransactionRepository;
  }

  private PurchaseTokenDecoder getPurchaseTokenDecoder() {
    if (purchaseTokenDecoder == null) {
      purchaseTokenDecoder = new Base64PurchaseTokenDecoder();
    }
    return purchaseTokenDecoder;
  }

  private PaymentServiceSelector getServiceSelector() {
    if (serviceSelector == null) {
      serviceSelector =
          new SharedPreferencesPaymentServiceSelector(BuildConfig.DEFAULT_PAYMENT_SERVICE_TYPE,
              preferences);
    }
    return serviceSelector;
  }

  private BillingSyncScheduler getBillingSyncSchedulerV7() {
    if (billingSyncSchedulerV7 == null) {
      billingSyncSchedulerV7 = new BillingSyncManager(
          new BillingSyncFactory(getCustomer(), getTransactionServiceV7(),
              new AuthorizationServiceV7(
                  new AuthorizationMapperV7(getAuthorizationFactory(), getBillingIdManagerV7()),
                  httpClient, WebService.getDefaultConverter(), tokenInvalidator, sharedPreferences,
                  bodyInterceptorPoolV7, getBillingIdManagerV7(), getAuthorizationFactory(),
                  authenticationPersistence), getTransactionPersistence(),
              getAuthorizationPersistence(), getLocalIdGenerator()), syncScheduler, new HashSet<>(),
          new HashMap<>());
    }
    return billingSyncSchedulerV7;
  }

  private BillingSyncScheduler getBillingSyncSchedulerV3() {
    if (billingSyncSchedulerV3 == null) {
      billingSyncSchedulerV3 = new BillingSyncManager(
          new BillingSyncFactory(getCustomer(), getTransactionServiceV3(),
              new AuthorizationServiceV3(getAuthorizationFactory(),
                  new AuthorizationMapperV3(getAuthorizationFactory()), getTransactionMapperV3(),
                  getTransactionPersistence(), bodyInterceptorV3, httpClient,
                  WebService.getDefaultConverter(), tokenInvalidator, sharedPreferences, customer,
                  resources, getBillingIdManagerV3()), getTransactionPersistence(),
              getAuthorizationPersistence(), getLocalIdGenerator()), syncScheduler, new HashSet<>(),
          new HashMap<>());
    }
    return billingSyncSchedulerV3;
  }

  private TransactionService getTransactionServiceV7() {
    if (transactionServiceV7 == null) {
      transactionServiceV7 = new TransactionServiceV7(
          new TransactionMapperV7(getTransactionFactory(), getBillingIdManagerV7()),
          bodyInterceptorPoolV7, WebService.getDefaultConverter(), httpClient, tokenInvalidator,
          sharedPreferences, getBillingIdManagerV7(), getTransactionFactory(),
          authenticationPersistence);
    }
    return transactionServiceV7;
  }

  private TransactionService getTransactionServiceV3() {
    if (transactionServiceV3 == null) {
      transactionServiceV3 = new TransactionServiceV3(getTransactionMapperV3(), bodyInterceptorV3,
          WebService.getDefaultConverter(), httpClient, tokenInvalidator, sharedPreferences,
          getTransactionFactory(), resources, getBillingIdManagerV3());
    }
    return transactionServiceV3;
  }

  private TransactionMapperV3 getTransactionMapperV3() {
    if (transactionMapperV3 == null) {
      transactionMapperV3 =
          new TransactionMapperV3(getTransactionFactory(), getBillingIdManagerV3());
    }
    return transactionMapperV3;
  }

  private AuthorizationPersistence getAuthorizationPersistence() {
    if (authorizationPersistence == null) {
      authorizationPersistence =
          new RealmAuthorizationPersistence(new HashMap<>(), PublishRelay.create(), database,
              new RealmAuthorizationMapper(getAuthorizationFactory()), Schedulers.io(),
              getAuthorizationFactory(), getLocalIdGenerator());
    }
    return authorizationPersistence;
  }

  private LocalIdGenerator getLocalIdGenerator() {
    if (localIdGenerator == null) {
      localIdGenerator = new LocalIdGenerator();
    }
    return localIdGenerator;
  }

  private TransactionPersistence getTransactionPersistence() {
    if (transactionPersistence == null) {
      transactionPersistence =
          new InMemoryTransactionPersistence(new HashMap<>(), PublishRelay.create());
    }
    return transactionPersistence;
  }

  private AuthorizationFactory getAuthorizationFactory() {
    if (authorizationFactory == null) {
      authorizationFactory = new AuthorizationFactory();
    }
    return authorizationFactory;
  }

  private Customer getCustomer() {
    if (customer == null) {
      customer = new AccountCustomer(accountManager);
    }
    return customer;
  }

  private TransactionFactory getTransactionFactory() {
    if (transactionFactory == null) {
      transactionFactory = new TransactionFactory();
    }
    return transactionFactory;
  }

  private BillingIdManager getBillingIdManagerV7() {
    if (billingIdManagerV7 == null) {
      billingIdManagerV7 = new BillingIdManagerV7(getLocalIdGenerator());
    }
    return billingIdManagerV7;
  }

  private BillingIdManager getBillingIdManagerV3() {
    if (billingIdManagerV3 == null) {
      billingIdManagerV3 = new BillingIdManagerV3(getLocalIdGenerator());
    }
    return billingIdManagerV3;
  }
}

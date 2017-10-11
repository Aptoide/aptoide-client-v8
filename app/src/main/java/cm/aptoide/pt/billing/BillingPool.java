package cm.aptoide.pt.billing;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.AuthorizationMapper;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.billing.authorization.AuthorizationRepository;
import cm.aptoide.pt.billing.authorization.AuthorizationServiceV3;
import cm.aptoide.pt.billing.authorization.AuthorizationServiceV7;
import cm.aptoide.pt.billing.authorization.RealmAuthorizationPersistence;
import cm.aptoide.pt.billing.external.ExternalBillingSerializer;
import cm.aptoide.pt.billing.product.ProductFactory;
import cm.aptoide.pt.billing.sync.BillingSyncFactory;
import cm.aptoide.pt.billing.sync.BillingSyncManager;
import cm.aptoide.pt.billing.transaction.InMemoryTransactionPersistence;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
import cm.aptoide.pt.billing.transaction.TransactionMapper;
import cm.aptoide.pt.billing.transaction.TransactionPersistence;
import cm.aptoide.pt.billing.transaction.TransactionRepository;
import cm.aptoide.pt.billing.transaction.TransactionService;
import cm.aptoide.pt.billing.transaction.TransactionServiceV3;
import cm.aptoide.pt.billing.transaction.TransactionServiceV7;
import cm.aptoide.pt.crashreports.CrashLogger;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.install.PackageRepository;
import cm.aptoide.pt.sync.SyncScheduler;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

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

  private BillingSyncScheduler inAppBillingSyncManager;
  private AuthorizationRepository inAppAuthorizationRepository;
  private TransactionRepository inAppTransactionRepository;
  private TransactionService transactionServiceV7;
  private BillingService v7BillingService;

  private BillingSyncScheduler paidAppBillingSyncManager;
  private AuthorizationRepository paidAppAuthorizationRepository;
  private TransactionRepository paidAppTransactionRepository;
  private TransactionService transactionServiceV3;
  private V3BillingService v3BillingService;

  private PaymentServiceSelector serviceSelector;
  private AuthorizationPersistence authorizationPersistence;
  private TransactionPersistence transactionPersistence;
  private AuthorizationMapper authorizationMapper;
  private AccountCustomer customer;
  private TransactionMapper transactionMapper;
  private TransactionFactory transactionFactory;
  private AuthorizationFactory authorizationFactory;
  private PurchaseTokenDecoder purchaseTokenDecoder;
  private PurchaseMapper purchaseMapper;
  private ProductFactory productFactory;

  public BillingPool(SharedPreferences sharedPreferences,
      BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient httpClient,
      AptoideAccountManager accountManager, Database database, Resources resources,
      PackageRepository packageRepository, TokenInvalidator tokenInvalidator,
      SyncScheduler syncScheduler, ExternalBillingSerializer externalBillingSerializer,
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorPoolV7,
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> accountSettingsBodyInterceptorPoolV7,
      HashMap<String, Billing> poll, Converter.Factory converterFactory, CrashReport crashLogger) {
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
  }

  public Billing get(String merchantName) {
    if (!pool.containsKey(merchantName)) {
      pool.put(merchantName, create(merchantName));
    }
    return pool.get(merchantName);
  }

  private Billing create(String merchantName) {

    if (merchantName.equals(BuildConfig.APPLICATION_ID)) {
      return new Billing(merchantName, getPaidAppBillingService(),
          getPaidAppTransactionRepository(), getPaidAppAuthorizationRepository(),
          getServiceSelector(), getCustomer(), getAuthorizationFactory(), getPurchaseTokenDecoder(),
          getPaidAppBillingSyncManager());
    } else {
      return new Billing(merchantName, getInAppBillingService(), getInAppTransactionRepository(),
          getInAppAuthorizationRepository(), getServiceSelector(), getCustomer(),
          getAuthorizationFactory(), getPurchaseTokenDecoder(), getInAppBillingSyncManager());
    }
  }

  private TransactionRepository getPaidAppTransactionRepository() {
    if (paidAppAuthorizationRepository == null) {
      paidAppTransactionRepository =
          new TransactionRepository(getTransactionPersistence(), getPaidAppBillingSyncManager(),
              getCustomer(), getTransactionServiceV3());
    }
    return paidAppTransactionRepository;
  }

  private AuthorizationRepository getPaidAppAuthorizationRepository() {
    if (paidAppAuthorizationRepository == null) {
      paidAppAuthorizationRepository =
          new AuthorizationRepository(getPaidAppBillingSyncManager(), getCustomer(),
              getAuthorizationPersistence());
    }
    return paidAppAuthorizationRepository;
  }

  private BillingService getPaidAppBillingService() {
    if (v7BillingService == null) {
      v3BillingService =
          new V3BillingService(bodyInterceptorV3, httpClient, converterFactory, tokenInvalidator,
              sharedPreferences, getPurchaseMapper(), getProductFactory(), resources,
              new PaymentService(1, PaymentServiceMapper.PAYPAL, "PayPal", null, ""));
    }
    return v3BillingService;
  }

  private ProductFactory getProductFactory() {
    if (productFactory == null) {
      productFactory = new ProductFactory();
    }
    return productFactory;
  }

  private PurchaseMapper getPurchaseMapper() {
    if (purchaseMapper == null) {
      purchaseMapper = new PurchaseMapper(externalBillingSerializer);
    }
    return purchaseMapper;
  }

  private BillingService getInAppBillingService() {
    if (v7BillingService == null) {
      v7BillingService =
          new V7BillingService(accountSettingsBodyInterceptorPoolV7, httpClient, converterFactory,
              tokenInvalidator, sharedPreferences, getPurchaseMapper(), getProductFactory(),
              packageRepository, new PaymentServiceMapper(crashLogger));
    }
    return v7BillingService;
  }

  private AuthorizationRepository getInAppAuthorizationRepository() {
    if (inAppAuthorizationRepository == null) {
      inAppAuthorizationRepository =
          new AuthorizationRepository(getInAppBillingSyncManager(), getCustomer(),
              getAuthorizationPersistence());
    }
    return inAppAuthorizationRepository;
  }

  private TransactionRepository getInAppTransactionRepository() {
    if (inAppTransactionRepository == null) {
      inAppTransactionRepository =
          new TransactionRepository(getTransactionPersistence(), getInAppBillingSyncManager(),
              getCustomer(), getTransactionServiceV7());
    }
    return inAppTransactionRepository;
  }

  private PurchaseTokenDecoder getPurchaseTokenDecoder() {
    if (purchaseTokenDecoder == null) {
      purchaseTokenDecoder = new OkioPurchaseTokenDecoder();
    }
    return purchaseTokenDecoder;
  }

  private PaymentServiceSelector getServiceSelector() {
    if (serviceSelector == null) {
      serviceSelector =
          new SharedPreferencesPaymentServiceSelector(BuildConfig.DEFAULT_PAYMENT_SERVICE_TYPE,
              sharedPreferences);
    }
    return serviceSelector;
  }

  private BillingSyncScheduler getInAppBillingSyncManager() {
    if (inAppBillingSyncManager == null) {
      inAppBillingSyncManager = new BillingSyncManager(
          new BillingSyncFactory(getCustomer(), getTransactionServiceV7(),
              new AuthorizationServiceV7(getAuthorizationMapper(), httpClient,
                  WebService.getDefaultConverter(), tokenInvalidator, sharedPreferences,
                  bodyInterceptorPoolV7), getTransactionPersistence(),
              getAuthorizationPersistence()), syncScheduler, new HashSet<>());
    }
    return inAppBillingSyncManager;
  }

  private BillingSyncScheduler getPaidAppBillingSyncManager() {
    if (paidAppBillingSyncManager == null) {
      paidAppBillingSyncManager = new BillingSyncManager(
          new BillingSyncFactory(getCustomer(), getTransactionServiceV3(),
              new AuthorizationServiceV3(getAuthorizationFactory(), getAuthorizationMapper(),
                  bodyInterceptorV3, httpClient, WebService.getDefaultConverter(), tokenInvalidator,
                  sharedPreferences, customer, resources), getTransactionPersistence(),
              getAuthorizationPersistence()), syncScheduler, new HashSet<>());
    }
    return paidAppBillingSyncManager;
  }

  private TransactionService getTransactionServiceV7() {
    if (transactionServiceV7 == null) {
      transactionServiceV7 = new TransactionServiceV7(getTransactionMapper(), bodyInterceptorPoolV7,
          WebService.getDefaultConverter(), httpClient, tokenInvalidator, sharedPreferences);
    }
    return transactionServiceV7;
  }

  private TransactionService getTransactionServiceV3() {
    if (transactionServiceV3 == null) {
      transactionServiceV3 = new TransactionServiceV3(getTransactionMapper(), bodyInterceptorV3,
          WebService.getDefaultConverter(), httpClient, tokenInvalidator, sharedPreferences,
          getTransactionFactory(), getAuthorizationPersistence(), getAuthorizationMapper(),
          getCustomer(), resources);
    }
    return transactionServiceV3;
  }

  private AuthorizationPersistence getAuthorizationPersistence() {
    if (authorizationPersistence == null) {
      authorizationPersistence =
          new RealmAuthorizationPersistence(new HashMap<>(), PublishRelay.create(), database,
              getAuthorizationMapper(), getAuthorizationFactory());
    }
    return authorizationPersistence;
  }

  private TransactionPersistence getTransactionPersistence() {
    if (transactionPersistence == null) {
      transactionPersistence =
          new InMemoryTransactionPersistence(new HashMap<>(), PublishRelay.create(),
              getTransactionFactory());
    }
    return transactionPersistence;
  }

  private AuthorizationMapper getAuthorizationMapper() {
    if (authorizationMapper == null) {
      authorizationMapper = new AuthorizationMapper(getAuthorizationFactory());
    }
    return authorizationMapper;
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

  private TransactionMapper getTransactionMapper() {
    if (transactionMapper == null) {
      transactionMapper = new TransactionMapper(getTransactionFactory());
    }
    return transactionMapper;
  }

  private TransactionFactory getTransactionFactory() {
    if (transactionFactory == null) {
      transactionFactory = new TransactionFactory();
    }
    return transactionFactory;
  }
}

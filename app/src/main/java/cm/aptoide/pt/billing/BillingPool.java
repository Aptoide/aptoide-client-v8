package cm.aptoide.pt.billing;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.AuthorizationMapperV3;
import cm.aptoide.pt.billing.authorization.AuthorizationMapperV7;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.billing.authorization.AuthorizationRepository;
import cm.aptoide.pt.billing.authorization.AuthorizationServiceV3;
import cm.aptoide.pt.billing.authorization.AuthorizationServiceV7;
import cm.aptoide.pt.billing.authorization.RealmAuthorizationMapper;
import cm.aptoide.pt.billing.authorization.RealmAuthorizationPersistence;
import cm.aptoide.pt.billing.external.ExternalBillingSerializer;
import cm.aptoide.pt.billing.product.ProductMapperV3;
import cm.aptoide.pt.billing.product.ProductMapperV7;
import cm.aptoide.pt.billing.sync.BillingSyncFactory;
import cm.aptoide.pt.billing.sync.BillingSyncManager;
import cm.aptoide.pt.billing.transaction.InMemoryTransactionPersistence;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
import cm.aptoide.pt.billing.transaction.TransactionMapperV3;
import cm.aptoide.pt.billing.transaction.TransactionMapperV7;
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

  private BillingSyncScheduler billingSyncSchedulerV7;
  private AuthorizationRepository inAppAuthorizationRepository;
  private TransactionRepository inAppTransactionRepository;
  private TransactionService transactionServiceV7;
  private BillingServiceV7 billingServiceV7;
  private IdResolver idResolverV7;

  private BillingSyncScheduler billingSyncSchedulerV3;
  private AuthorizationRepository paidAppAuthorizationRepository;
  private TransactionRepository paidAppTransactionRepository;
  private TransactionService transactionServiceV3;
  private BillingService billingServiceV3;
  private IdResolver idResolverV3;

  private PaymentServiceSelector serviceSelector;
  private AuthorizationPersistence authorizationPersistence;
  private TransactionPersistence transactionPersistence;
  private Customer customer;
  private TransactionFactory transactionFactory;
  private AuthorizationFactory authorizationFactory;
  private PurchaseTokenDecoder purchaseTokenDecoder;
  private TransactionMapperV3 transactionMapperV3;

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

  public IdResolver getIdResolver(String merchantName) {
    if (merchantName.equals(BuildConfig.APPLICATION_ID)) {
      return getIdResolverV3();
    } else {
      return getIdResolverV7();
    }
  }

  private Billing create(String merchantName) {
    if (merchantName.equals(BuildConfig.APPLICATION_ID)) {
      return new Billing(merchantName, getBillingServiceV3(), getPaidAppTransactionRepository(),
          getPaidAppAuthorizationRepository(), getServiceSelector(), getCustomer(),
          getAuthorizationFactory(), getPurchaseTokenDecoder(), getBillingSyncSchedulerV3());
    } else {
      return new Billing(merchantName, getBillingServiceV7(), getInAppTransactionRepository(),
          getInAppAuthorizationRepository(), getServiceSelector(), getCustomer(),
          getAuthorizationFactory(), getPurchaseTokenDecoder(), getBillingSyncSchedulerV7());
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
              getAuthorizationPersistence());
    }
    return paidAppAuthorizationRepository;
  }

  private BillingService getBillingServiceV3() {
    if (billingServiceV3 == null) {
      billingServiceV3 =
          new BillingServiceV3(bodyInterceptorV3, httpClient, converterFactory, tokenInvalidator,
              sharedPreferences, new PurchaseMapperV3(), new ProductMapperV3(getIdResolverV3()),
              resources, new PaymentService(getIdResolverV3().generateServiceId(1),
              PaymentServiceMapper.PAYPAL, "PayPal", null, ""), getIdResolverV3());
    }
    return billingServiceV3;
  }

  private BillingService getBillingServiceV7() {
    if (billingServiceV7 == null) {
      billingServiceV7 =
          new BillingServiceV7(accountSettingsBodyInterceptorPoolV7, httpClient, converterFactory,
              tokenInvalidator, sharedPreferences,
              new PurchaseMapperV7(externalBillingSerializer, getIdResolverV7()),
              new ProductMapperV7(getIdResolverV7()), packageRepository,
              new PaymentServiceMapper(crashLogger, getIdResolverV7()), getIdResolverV7());
    }
    return billingServiceV7;
  }

  private AuthorizationRepository getInAppAuthorizationRepository() {
    if (inAppAuthorizationRepository == null) {
      inAppAuthorizationRepository =
          new AuthorizationRepository(getBillingSyncSchedulerV7(), getCustomer(),
              getAuthorizationPersistence());
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
              sharedPreferences);
    }
    return serviceSelector;
  }

  private BillingSyncScheduler getBillingSyncSchedulerV7() {
    if (billingSyncSchedulerV7 == null) {
      billingSyncSchedulerV7 = new BillingSyncManager(
          new BillingSyncFactory(getCustomer(), getTransactionServiceV7(),
              new AuthorizationServiceV7(
                  new AuthorizationMapperV7(getAuthorizationFactory(), getIdResolverV7()),
                  httpClient, WebService.getDefaultConverter(), tokenInvalidator, sharedPreferences,
                  bodyInterceptorPoolV7, getIdResolverV7()), getTransactionPersistence(),
              getAuthorizationPersistence()), syncScheduler, new HashSet<>());
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
                  resources, getIdResolverV3()), getTransactionPersistence(),
              getAuthorizationPersistence()), syncScheduler, new HashSet<>());
    }
    return billingSyncSchedulerV3;
  }

  private TransactionService getTransactionServiceV7() {
    if (transactionServiceV7 == null) {
      transactionServiceV7 = new TransactionServiceV7(
          new TransactionMapperV7(getTransactionFactory(), getIdResolverV7()),
          bodyInterceptorPoolV7, WebService.getDefaultConverter(), httpClient, tokenInvalidator,
          sharedPreferences, getIdResolverV7(), getTransactionFactory());
    }
    return transactionServiceV7;
  }

  private TransactionService getTransactionServiceV3() {
    if (transactionServiceV3 == null) {
      transactionServiceV3 = new TransactionServiceV3(getTransactionMapperV3(), bodyInterceptorV3,
          WebService.getDefaultConverter(), httpClient, tokenInvalidator, sharedPreferences,
          getTransactionFactory(), getCustomer(), resources, getIdResolverV3());
    }
    return transactionServiceV3;
  }

  private TransactionMapperV3 getTransactionMapperV3() {
    if (transactionMapperV3 == null) {
      transactionMapperV3 = new TransactionMapperV3(getTransactionFactory(), getIdResolverV3());
    }
    return transactionMapperV3;
  }

  private AuthorizationPersistence getAuthorizationPersistence() {
    if (authorizationPersistence == null) {
      authorizationPersistence =
          new RealmAuthorizationPersistence(new HashMap<>(), PublishRelay.create(), database,
              new RealmAuthorizationMapper(getAuthorizationFactory()), Schedulers.io());
    }
    return authorizationPersistence;
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

  private IdResolver getIdResolverV7() {
    if (idResolverV7 == null) {
      idResolverV7 = new IdResolverV7();
    }
    return idResolverV7;
  }

  private IdResolver getIdResolverV3() {
    if (idResolverV3 == null) {
      idResolverV3 = new IdResolverV3();
    }
    return idResolverV3;
  }
}
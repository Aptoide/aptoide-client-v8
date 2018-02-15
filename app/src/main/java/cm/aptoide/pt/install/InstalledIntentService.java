package cm.aptoide.pt.install;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import javax.inject.Inject;

import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoredMinimalAdAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.root.RootAvailabilityManager;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.updates.UpdateRepository;
import cm.aptoide.pt.util.ReferrerUtils;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.q.QManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class InstalledIntentService extends IntentService {

    private static final String TAG = InstalledIntentService.class.getName();
    @Inject
    InstallAnalytics installAnalytics;
    private SharedPreferences sharedPreferences;
    private AdsRepository adsRepository;
    private UpdateRepository updatesRepository;
    private CompositeSubscription subscriptions;
    private OkHttpClient httpClient;
    private Converter.Factory converterFactory;
    private InstallManager installManager;
    private RootAvailabilityManager rootAvailabilityManager;
    private QManager qManager;
    private MinimalAdMapper adMapper;
    private PackageManager packageManager;

    public InstalledIntentService() {
        super("InstalledIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((AptoideApplication) getApplicationContext()).getApplicationComponent()
                .inject(this);
        adMapper = new MinimalAdMapper();
        sharedPreferences =
                ((AptoideApplication) getApplicationContext()).getDefaultSharedPreferences();
        qManager = ((AptoideApplication) getApplicationContext()).getQManager();
        httpClient = ((AptoideApplication) getApplicationContext()).getDefaultClient();
        converterFactory = WebService.getDefaultConverter();
        final SharedPreferences sharedPreferences =
                ((AptoideApplication) getApplicationContext()).getDefaultSharedPreferences();
        adsRepository = ((AptoideApplication) getApplicationContext()).getAdsRepository();
        updatesRepository = RepositoryFactory.getUpdateRepository(this, sharedPreferences);
        subscriptions = new CompositeSubscription();
        installManager =
                ((AptoideApplication) getApplicationContext()).getInstallManager();
        rootAvailabilityManager =
                ((AptoideApplication) getApplicationContext()).getRootAvailabilityManager();
        packageManager = getPackageManager();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String packageName = intent.getData()
                    .getEncodedSchemeSpecificPart();


            if (!TextUtils.equals(action, Intent.ACTION_PACKAGE_REPLACED) && intent.getBooleanExtra(
                    Intent.EXTRA_REPLACING, false)) {
                // do nothing if its a replacement ongoing. we are only interested in
                // already replaced apps
                return;
            }

            switch (action) {
                case Intent.ACTION_PACKAGE_ADDED:
                    onPackageAdded(packageName);
                    break;
                case Intent.ACTION_PACKAGE_REPLACED:
                    onPackageReplaced(packageName);
                    break;
                case Intent.ACTION_PACKAGE_REMOVED:
                    onPackageRemoved(packageName);
                    break;
            }
        }
    }

    protected void onPackageAdded(String packageName) {
        Logger.d(TAG, "Package added: " + packageName);

        PackageInfo packageInfo = databaseOnPackageAdded(packageName);
        checkAndBroadcastReferrer(packageName);
        sendInstallEvent(packageName, packageInfo);
    }

    protected void onPackageReplaced(String packageName) {
        Logger.d(TAG, "Packaged replaced: " + packageName);
        PackageInfo packageInfo = databaseOnPackageReplaced(packageName);
        sendInstallEvent(packageName, packageInfo);
    }

    protected void onPackageRemoved(String packageName) {
        Logger.d(TAG, "Packaged removed: " + packageName);
        databaseOnPackageRemoved(packageName);
    }

    private PackageInfo databaseOnPackageAdded(String packageName) {
        PackageInfo packageInfo = AptoideUtils.SystemU.getPackageInfo(packageName, getPackageManager());

        if (checkAndLogNullPackageInfo(packageInfo, packageName)) {
            return packageInfo;
        }
        Installed installed = new Installed(packageInfo, packageManager);
        installManager.onAppInstalled(installed)
                .subscribe(() -> {
                }, throwable -> CrashReport.getInstance()
                        .log(throwable));
        return packageInfo;
    }

    private void checkAndBroadcastReferrer(String packageName) {
        StoredMinimalAdAccessor storedMinimalAdAccessor = AccessorFactory.getAccessorFor(
                ((AptoideApplication) getApplicationContext().getApplicationContext()).getDatabase(),
                StoredMinimalAd.class);
        Subscription unManagedSubscription = storedMinimalAdAccessor.get(packageName)
                .flatMapCompletable(storeMinimalAd -> {
                    if (storeMinimalAd != null) {
                        return knockCpi(packageName, storedMinimalAdAccessor, storeMinimalAd);
                    } else {
                        return extractReferrer(packageName);
                    }
                })
                .subscribe(__ -> { /* do nothing */ }, err -> {
                    CrashReport.getInstance()
                            .log(err);
                });

        subscriptions.add(unManagedSubscription);
    }

    private void sendInstallEvent(String packageName, PackageInfo packageInfo) {
        if (packageInfo != null) {
            installAnalytics.installCompleted(packageName, packageInfo.versionCode,
                    rootAvailabilityManager.isRootAvailable()
                            .toBlocking()
                            .value(), ManagerPreferences.allowRootInstallation(sharedPreferences));
            return;
        }

        // information about the package is null so we don't broadcast an event
        CrashReport.getInstance()
                .log(new NullPointerException("PackageInfo is null."));
    }

    private PackageInfo databaseOnPackageReplaced(String packageName) {
        final Update update = updatesRepository.get(packageName)
                .first()
                .doOnError(throwable -> {
                    CrashReport.getInstance()
                            .log(throwable);
                })
                .onErrorReturn(throwable -> null)
                .toBlocking()
                .first();

        if (update != null && update.getPackageName() != null && update.getTrustedBadge() != null) {
            installAnalytics.sendReplacedEvent(packageName);
        }

        PackageInfo packageInfo = AptoideUtils.SystemU.getPackageInfo(packageName, getPackageManager());

        if (checkAndLogNullPackageInfo(packageInfo, packageName)) {
            return packageInfo;
        }

        installManager.onUpdateConfirmed(new Installed(packageInfo, packageManager))
                .andThen(updatesRepository.remove(update))
                .subscribe(() -> Logger.d(TAG, "databaseOnPackageReplaced: " + packageName),
                        throwable -> CrashReport.getInstance()
                                .log(throwable));
        return packageInfo;
    }

    private void databaseOnPackageRemoved(String packageName) {
        installManager.onAppRemoved(packageName)
                .andThen(Completable.fromAction(() -> updatesRepository.remove(packageName)))
                .subscribe(() -> Logger.d(TAG, "databaseOnPackageRemoved: " + packageName),
                        throwable -> CrashReport.getInstance()
                                .log(throwable));
    }

    /**
     * @param packageInfo packageInfo.
     * @return true if packageInfo is null, false otherwise.
     */
    private boolean checkAndLogNullPackageInfo(PackageInfo packageInfo, String packageName) {
        if (packageInfo == null) {
            CrashReport.getInstance()
                    .log(new IllegalArgumentException("PackageName null for package " + packageName));
            return true;
        } else {
            return false;
        }
    }

    private Completable knockCpi(String packageName, StoredMinimalAdAccessor storedMinimalAdAccessor,
                                 StoredMinimalAd storeMinimalAd) {
        return Completable.fromCallable(() -> {
            ReferrerUtils.broadcastReferrer(packageName, storeMinimalAd.getReferrer(),
                    getApplicationContext());
            AdNetworkUtils.knockCpi(adMapper.map(storeMinimalAd));
            storedMinimalAdAccessor.remove(storeMinimalAd);
            return null;
        });
    }

    @NonNull
    private Completable extractReferrer(String packageName) {
        return adsRepository.getAdsFromSecondInstall(packageName)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(minimalAd -> ReferrerUtils.extractReferrer(new SearchAdResult(minimalAd),
                        ReferrerUtils.RETRIES, true, adsRepository, httpClient, converterFactory, qManager,
                        getApplicationContext(), sharedPreferences, new MinimalAdMapper()))
                .toCompletable();
    }
}

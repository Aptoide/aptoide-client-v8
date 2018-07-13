/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;


import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.model.v3.PaidApp;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import rx.Observable;

/**
 * Created by marcelobenites on 7/27/16.
 */
public class AppRepository {

    private final NetworkOperatorManager operatorManager;
    private final AptoideClientUUID aptoideClientUUID;

    public AppRepository(NetworkOperatorManager operatorManager) {
        this.operatorManager = operatorManager;

        aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                DataProvider.getContext());
    }



    public Observable<GetApp> getApp(long appId, boolean refresh, boolean sponsored, String storeName,
                                     String packageName) {
        //Only pass the storeName if this is partners
        //If vanilla, don't pass the store name.
        //store name is already in appId
        //[AN-1160] - [AppView] latest version bug
        return GetAppRequest.of(appId,
                V8Engine.getConfiguration().getPartnerId() == null ? null : storeName,
                StoreUtils.getStoreCredentials(storeName), AptoideAccountManager.getAccessToken(),
                new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                        DataProvider.getContext()).getUniqueIdentifier(), packageName)
                .observe(refresh)
                .flatMap(response -> {
                    if (response != null && response.isOk()) {
                        if (response.getNodes().getMeta().getData().isPaid()) {
                            return addPayment(sponsored, response, refresh);
                        } else {
                            return Observable.just(response);
                        }
                    } else {
                        return Observable.error(
                                new RepositoryItemNotFoundException("No app found for app id " + appId));
                    }
                });
    }


    private Observable<GetApp> addPayment(boolean sponsored, GetApp getApp, boolean refresh) {
        return getPaidApp(getApp.getNodes().getMeta().getData().getId(), sponsored,
                getApp.getNodes().getMeta().getData().getStore().getName(), refresh).map(paidApp -> {

            if (paidApp.getPayment().isPaid()) {
                getApp.getNodes().getMeta().getData().getFile().setPath(paidApp.getPath().getStringPath());
            } else {
                getApp.getNodes()
                        .getMeta()
                        .getData()
                        .getPay()
                        .setProductId(paidApp.getPayment().getMetadata().getId());
            }
            getApp.getNodes().getMeta().getData().getPay().setPrice(paidApp.getPayment().getAmount());
            getApp.getNodes().getMeta().getData().getPay().setSymbol(paidApp.getPayment().getSymbol());
            getApp.getNodes().getMeta().getData().getPay().setStatus(paidApp.getPayment().getStatus());
            return getApp;
        }).onErrorResumeNext(throwable -> {
            if (throwable instanceof RepositoryItemNotFoundException) {
                return Observable.just(getApp);
            }
            return Observable.error(throwable);
        });
    }

    public Observable<PaidApp> getPaidApp(long appId, boolean sponsored, String storeName,
                                          boolean refresh) {
        return GetApkInfoRequest.of(appId, operatorManager, sponsored, storeName,
                AptoideAccountManager.getAccessToken()).observe(refresh).flatMap(response -> {
            if (response != null && response.isOk() && response.isPaid()) {
                return Observable.just(response);
            } else {
                return Observable.error(new RepositoryItemNotFoundException(
                        "No paid app found for app id " + appId + " in store " +
                                storeName));
            }
        });
    }

    public Observable<GetApp> getApp(String packageName, boolean refresh, boolean sponsored,
                                     String storeName) {
        return GetAppRequest.of(packageName, storeName, AptoideAccountManager.getAccessToken(),
                aptoideClientUUID.getUniqueIdentifier()).observe(refresh).flatMap(response -> {
            if (response != null && response.isOk()) {
                if (response.getNodes().getMeta().getData().isPaid()) {
                    return addPayment(sponsored, response, refresh);
                } else {
                    return Observable.just(response);
                }
            } else {
                return Observable.error(
                        new RepositoryItemNotFoundException("No app found for app package" + packageName));
            }
        });
    }

    public Observable<GetApp> getAppFromMd5(String md5, boolean refresh, boolean sponsored) {
        return GetAppRequest.ofMd5(md5, AptoideAccountManager.getAccessToken(),
                aptoideClientUUID.getUniqueIdentifier()).observe(refresh).flatMap(response -> {
            if (response != null && response.isOk()) {
                if (response.getNodes().getMeta().getData().isPaid()) {
                    return addPayment(sponsored, response, refresh);
                } else {
                    return Observable.just(response);
                }
            } else {
                return Observable.error(
                        new RepositoryItemNotFoundException("No app found for app md5" + md5));
            }
        });
    }
}

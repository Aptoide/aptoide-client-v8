/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.v8engine.gms;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by marcelobenites on 06/02/17.
 */

public class GooglePlayServicesConnection {

  private final Context context;
  private final GoogleApiAvailability apiAvailability;
  private final GoogleApiClient client;

  public GooglePlayServicesConnection(Context context, GoogleApiAvailability apiAvailability,
      GoogleApiClient client) {
    this.context = context;
    this.apiAvailability = apiAvailability;
    this.client = client;
  }

  public Single<Boolean> isAvailable() {
    return Single.fromCallable(() -> {
      return apiAvailability.isGooglePlayServicesAvailable(context.getApplicationContext())
          == ConnectionResult.SUCCESS;
    });
  }

  public Completable connect() {
    return Observable.create(new Observable.OnSubscribe<Void>() {
      @Override public void call(Subscriber<? super Void> subscriber) {
        final GoogleApiClient.ConnectionCallbacks connectionCallbacks =
            new GoogleApiClient.ConnectionCallbacks() {
              @Override public void onConnected(@Nullable Bundle bundle) {
                if (!subscriber.isUnsubscribed()) {
                  subscriber.onCompleted();
                }
              }

              @Override public void onConnectionSuspended(int i) {

              }
            };

        final GoogleApiClient.OnConnectionFailedListener connectionFailedListener =
            new GoogleApiClient.OnConnectionFailedListener() {
              @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                if (!subscriber.isUnsubscribed()) {
                  subscriber.onError(new GooglePlayServicesException(connectionResult.getErrorCode(),
                      connectionResult.hasResolution()));
                }
              }
            };

        subscriber.add(Subscriptions.create(() -> {
          client.unregisterConnectionCallbacks(connectionCallbacks);
          client.unregisterConnectionFailedListener(connectionFailedListener);
          client.disconnect();
        }));

        client.registerConnectionCallbacks(connectionCallbacks);
        client.registerConnectionFailedListener(connectionFailedListener);
        client.connect();
      }
    }).toCompletable();
  }
}

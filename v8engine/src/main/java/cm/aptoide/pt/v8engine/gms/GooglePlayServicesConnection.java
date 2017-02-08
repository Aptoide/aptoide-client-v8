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
import com.jakewharton.rxrelay.BehaviorRelay;
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
  private final BehaviorRelay<Status> statusSubject;
  private final GoogleApiClient.OnConnectionFailedListener connectionFailedListener;
  private final GoogleApiClient.ConnectionCallbacks connectionCallbacks;

  public GooglePlayServicesConnection(Context context, GoogleApiAvailability apiAvailability,
      GoogleApiClient client) {
    this.context = context.getApplicationContext();
    this.apiAvailability = apiAvailability;
    this.client = client;
    this.statusSubject = BehaviorRelay.create();
    this.connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
      @Override public void onConnected(@Nullable Bundle bundle) {
        statusSubject.call(new Status(Status.CONNECTED, 0, false));
      }

      @Override public void onConnectionSuspended(int i) {
        statusSubject.call(new Status(Status.SUSPENDED, 0, false));
      }
    };

    this.connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
      @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        statusSubject.call(new Status(Status.ERROR, connectionResult.getErrorCode(),
            connectionResult.hasResolution()));
      }
    };
  }

  public boolean isAvailable() {
    return apiAvailability.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
  }

  public void connect() {
    if (isAvailable() && !client.isConnected() && !client.isConnecting()) {
      client.registerConnectionCallbacks(connectionCallbacks);
      client.registerConnectionFailedListener(connectionFailedListener);
      client.connect();
    }
  }

  public void disconnect() {
    if (isAvailable()) {
      client.unregisterConnectionCallbacks(connectionCallbacks);
      client.unregisterConnectionFailedListener(connectionFailedListener);
      client.disconnect();
    }
  }

  public Observable<Status> getStatus() {
    return statusSubject;
  }

  public static class Status {
    public static final int CONNECTED = 0;
    public static final int SUSPENDED = 1;
    public static final int ERROR = 99;

    private final int code;
    private final int errorCode;
    private final boolean resolvable;

    public Status(int code, int errorCode, boolean resolvable) {
      this.code = code;
      this.errorCode = errorCode;
      this.resolvable = resolvable;
    }

    public int getCode() {
      return code;
    }

    public int getErrorCode() {
      return errorCode;
    }

    public boolean isResolvable() {
      return resolvable;
    }
  }
}

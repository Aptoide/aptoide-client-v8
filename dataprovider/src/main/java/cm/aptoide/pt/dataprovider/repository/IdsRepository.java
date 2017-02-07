/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.dataprovider.repository;

import cm.aptoide.pt.interfaces.AptoideClientUUID;

/**
 * Created by neuro on 11-07-2016.
 */
public interface IdsRepository extends AptoideClientUUID {

  String getGoogleAdvertisingId();

  String getAdvertisingId();

  String getAndroidId();
}

/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 01/05/2016.
 */

package cm.aptoide.pt.v8engine.activity;

/**
 * Created by neuro on 01-05-2016.
 */
public abstract class AptoideBaseScreenActivity extends AptoideBaseActivity {

	/*
	 * @return o nome so monitor associado a esta activity, para efeitos de Analytics.
     */
	protected abstract String getAnalyticsScreenName();
}

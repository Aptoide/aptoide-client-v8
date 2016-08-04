/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.ProofOfPayment;
import com.trello.rxlifecycle.FragmentEvent;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.PaymentPayload;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.model.MinimalAd;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.dataprovider.ws.v3.CheckProductPaymentRequest;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.install.download.DownloadInstallationProvider;
import cm.aptoide.pt.v8engine.interfaces.AppMenuOptions;
import cm.aptoide.pt.v8engine.interfaces.Payments;
import cm.aptoide.pt.v8engine.interfaces.Scrollable;
import cm.aptoide.pt.v8engine.repository.AdRepository;
import cm.aptoide.pt.v8engine.repository.AppRepository;
import cm.aptoide.pt.v8engine.services.ValidatePaymentsService;
import cm.aptoide.pt.v8engine.util.AppUtils;
import cm.aptoide.pt.v8engine.util.SearchUtils;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.ThemeUtils;
import cm.aptoide.pt.v8engine.util.referrer.ReferrerUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewDescriptionDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewDeveloperDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewFlagThisDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewInstallDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewRateAndCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewScreenshotsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewSuggestedAppsDisplayable;
import io.realm.Realm;
import lombok.Cleanup;
import lombok.Getter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewFragment extends GridRecyclerFragment implements Scrollable, AppMenuOptions, Payments {

	public static final int VIEW_ID = R.layout.fragment_app_view;
	//
	// constants
	//
	private static final String TAG = AppViewFragment.class.getSimpleName();
	private static final String BAR_EXPANDED = "BAR_EXPANDED";
	private static final int PAY_APP_REQUEST_CODE = 12;

	private static final String CONFIG_ENVIRONMENT = //BuildConfig.DEBUG ? PayPalConfiguration.ENVIRONMENT_SANDBOX :
			PayPalConfiguration.ENVIRONMENT_PRODUCTION;

	private static final String CONFIG_CLIENT_ID = //BuildConfig.DEBUG ? "ARhHzhAH_B_6_9ggd97pIHNduraLFU9jf7Wzw06QMyEj2pRotOf8vw3PM0Ls" :
			"AW47wxAycZoTcXd5KxcJPujXWwImTLi-GNe3XvUUwFavOw8Nq4ZnlDT1SZIY";

	private static PayPalConfiguration config = new PayPalConfiguration()
			// Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
			// or live (ENVIRONMENT_PRODUCTION)
			.environment(CONFIG_ENVIRONMENT).clientId(CONFIG_CLIENT_ID);

	// FIXME restoreInstanteState doesn't work in this case
	private final Bundle memoryArgs = new Bundle();
	//private static final String TAG = AppViewFragment.class.getName();
	//
	// vars
	//
	private AppViewHeader header;
	//	private GetAppMeta.App app;
	private long appId;
	private String packageName;
	private boolean shouldInstall;
	private Scheduled scheduled;
	private String storeTheme;
	//
	// static fragment default new instance method
	//
	private MinimalAd minimalAd;
	// Stored to postpone ads logic
	private InstallManager installManager;

	private Action0 unInstallAction;
	private MenuItem uninstallMenuItem;
	private DownloadServiceHelper downloadManager;
	private AppRepository appRepository;
	private Subscription subscription;
	private AdRepository adRepository;
	private boolean sponsored;
	private List<GetAdsResponse.Ad> suggestedAds;

	public static AppViewFragment newInstance(String packageName, boolean shouldInstall) {
		Bundle bundle = new Bundle();
		bundle.putString(BundleKeys.PACKAGE_NAME.name(), packageName);
		bundle.putBoolean(BundleKeys.SHOULD_INSTALL.name(), shouldInstall);

		AppViewFragment fragment = new AppViewFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	public static AppViewFragment newInstance(long appId) {
		Bundle bundle = new Bundle();
		bundle.putLong(BundleKeys.APP_ID.name(), appId);

		AppViewFragment fragment = new AppViewFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	public static AppViewFragment newInstance(long appId, String storeTheme) {
		Bundle bundle = new Bundle();
		bundle.putLong(BundleKeys.APP_ID.name(), appId);
		bundle.putString(StoreFragment.BundleCons.STORE_THEME, storeTheme);
		AppViewFragment fragment = new AppViewFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	public static AppViewFragment newInstance(MinimalAd minimalAd) {
		Bundle bundle = new Bundle();
		bundle.putLong(BundleKeys.APP_ID.name(), minimalAd.getAppId());
		bundle.putParcelable(BundleKeys.MINIMAL_AD.name(), minimalAd);

		AppViewFragment fragment = new AppViewFragment();
		fragment.setArguments(bundle);

		return fragment;
	}

	public static AppViewFragment newInstance(GetAdsResponse.Ad ad) {
		return newInstance(MinimalAd.from(ad));
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final PermissionManager permissionManager = new PermissionManager();
		downloadManager = new DownloadServiceHelper(AptoideDownloadManager.getInstance(), permissionManager);
		installManager = new InstallManager(permissionManager, getContext().getPackageManager(),
				new DownloadInstallationProvider(downloadManager));
		appRepository = new AppRepository(new NetworkOperatorManager((TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE)));
		adRepository = new AdRepository();

		Context context = getContext();
		Intent intent = new Intent(context, PayPalService.class);
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
		context.startService(intent);
	}

	@Override
	public void loadExtras(Bundle args) {
		super.loadExtras(args);
		appId = args.getLong(BundleKeys.APP_ID.name(), -1);
		packageName = args.getString(BundleKeys.PACKAGE_NAME.name(), null);
		shouldInstall = args.getBoolean(BundleKeys.SHOULD_INSTALL.name(), false);
		minimalAd = args.getParcelable(BundleKeys.MINIMAL_AD.name());
		sponsored = minimalAd != null;
		storeTheme = args.getString(StoreFragment.BundleCons.STORE_THEME);
	}

	private void setupObservables(GetApp getApp) {
		// For stores subscription
		Database.StoreQ.getAll(realm).asObservable().compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(stores -> {
			if (Database.StoreQ.get(getApp.getNodes().getMeta().getData().getStore().getId(), realm) != null) {
				adapter.notifyDataSetChanged();
			}
		});

		// For install actions
		Database.RollbackQ.getAll(realm).asObservable().compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(rollbacks -> {
			adapter.notifyDataSetChanged();
		});

		// TODO: 27-05-2016 neuro install actions, not present in v7
	}

	private void setupDisplayables(GetApp getApp) {
		LinkedList<Displayable> displayables = new LinkedList<>();

		GetAppMeta.App app = getApp.getNodes().getMeta().getData();

		displayables.add(new AppViewInstallDisplayable(installManager, downloadManager, getApp, minimalAd, shouldInstall));
		displayables.add(new AppViewStoreDisplayable(getApp));
		displayables.add(new AppViewRateAndCommentsDisplayable(getApp));
		displayables.add(new AppViewScreenshotsDisplayable(app));
		displayables.add(new AppViewDescriptionDisplayable(getApp));
		displayables.add(new AppViewFlagThisDisplayable(getApp));
		if (suggestedAds != null) {
			displayables.add(new AppViewSuggestedAppsDisplayable(suggestedAds));
		}
		displayables.add(new AppViewDeveloperDisplayable(getApp));

		setDisplayables(displayables);
	}

	public void buyApp(GetAppMeta.App app) {
		GetAppMeta.Pay payment = app.getPay();

		PayPalPayment payPalPayment = new PayPalPayment(BigDecimal.valueOf(payment.getPrice()), payment.getCurrency(), Long.toString(app.getId()),
				PayPalPayment.PAYMENT_INTENT_SALE);

		Intent intent = new Intent(getContext(), PaymentActivity.class);
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
		intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
		intent.putExtra(CheckProductPaymentRequest.Constants.STORE, app.getStore().getName());
		intent.putExtra(CheckProductPaymentRequest.Constants.PRICE, payment.getPrice());
		intent.putExtra(CheckProductPaymentRequest.Constants.CURRENCY, payment.getCurrency());
		intent.putExtra(CheckProductPaymentRequest.Constants.TAX_RATE, app.getPayment().payment_services.get(0).getTaxRate());
		startActivityForResult(intent, PAY_APP_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PAY_APP_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
				if (confirm != null) {
					try {
						Logger.i(TAG, confirm.toJSONObject().toString(4));

						// TODO: 29/07/16 sithengineer
						// send 'confirm' to the server
						ProofOfPayment proof = confirm.getProofOfPayment();

						PaymentPayload paymentPayload = new PaymentPayload();
						paymentPayload.setPayType(1); // magic value: paypal payment type id
						paymentPayload.setApiVersion("3"); // magic value: webservice version
						paymentPayload.setProductId(appId);
						paymentPayload.setStore(data.getStringExtra(CheckProductPaymentRequest.Constants.STORE));
						paymentPayload.setPrice(data.getDoubleExtra(CheckProductPaymentRequest.Constants.PRICE, 0.0));
						paymentPayload.setCurrency(data.getStringExtra(CheckProductPaymentRequest.Constants.CURRENCY));
						paymentPayload.setPayKey(proof.getPaymentId());
						paymentPayload.setTaxRate(data.getDoubleExtra(CheckProductPaymentRequest.Constants.TAX_RATE, 0.0));

						final TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
						paymentPayload.setSimCountryCode(telephonyManager.getSimCountryIso());

						@Cleanup
						Realm realm = Database.get();
						realm.beginTransaction();
						realm.copyToRealmOrUpdate(paymentPayload);
						realm.commitTransaction();

						getActivity().startService(ValidatePaymentsService.getIntent(getActivity()));

					} catch (JSONException e) {
						Logger.e(TAG, "an extremely unlikely failure occurred: ", e);
					}
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Logger.i(TAG, "The user canceled.");
				ShowMessage.asSnack(header.badge, R.string.user_canceled);

			} else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
				Logger.i(TAG, "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
				ShowMessage.asSnack(header.badge, R.string.unknown_error);
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_appview_fragment, menu);
		SearchUtils.setupGlobalSearchView(menu, getActivity());
		uninstallMenuItem = menu.findItem(R.id.menu_uninstall);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int i = item.getItemId();

		if (i == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		} else if (i == R.id.menu_share) {

			ShowMessage.asSnack(this.getView(), "TO DO");
			// TODO: 19/07/16 sithengineer

			return true;
		} else if (i == R.id.menu_schedule) {
			@Cleanup
			Realm realm = Database.get();
			realm.beginTransaction();
			realm.copyToRealmOrUpdate(scheduled);
			realm.commitTransaction();

			String str = this.getString(R.string.added_to_scheduled);
			ShowMessage.asSnack(this.getView(), str);
			return true;
		} else if (i == R.id.menu_uninstall && unInstallAction != null) {
			unInstallAction.call();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void load(boolean refresh, Bundle savedInstanceState) {

		if (subscription != null) {
			subscription.unsubscribe();
		}

		if (appId >= 0) {
			Logger.d(TAG, "loading app info using app ID");
			subscription = appRepository.getApp(appId, refresh, sponsored)
					.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
					.flatMap(getApp -> manageOrganicAds(getApp))
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(getApp -> {
						if (storeTheme == null) {
							storeTheme = getApp.getNodes().getMeta().getData().getStore().getAppearance().getTheme();
						}

						// useful data for the schedule updates menu option
						GetAppMeta.App app = getApp.getNodes().getMeta().getData();
						scheduled = Scheduled.from(app);

						header.setup(getApp);
						setupDisplayables(getApp);
						setupObservables(getApp);
						finishLoading();
					}, throwable -> finishLoading(throwable));
		} else {
			Logger.d(TAG, "loading app info using app package name");
			subscription = appRepository.getApp(packageName, refresh, sponsored)
					.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
					.flatMap(getApp -> manageOrganicAds(getApp))
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(getApp -> {
						if (storeTheme == null) {
							storeTheme = getApp.getNodes().getMeta().getData().getStore().getAppearance().getTheme();
						}

						// useful data for the schedule updates menu option
						GetAppMeta.App app = getApp.getNodes().getMeta().getData();
						scheduled = Scheduled.from(app);

						header.setup(getApp);
						setupDisplayables(getApp);
						setupObservables(getApp);
						finishLoading();
					}, throwable -> finishLoading(throwable));
		}
	}

	@Override
	public int getContentViewId() {
		return VIEW_ID;
	}

	@Override
	public void setupViews() {
		super.setupViews();
		//		this.showAppInfo();

		final AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
		ActionBar supportActionBar = parentActivity.getSupportActionBar();
		if (supportActionBar != null) {
			supportActionBar.setDisplayHomeAsUpEnabled(true);
			supportActionBar.setTitle("");
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (storeTheme != null) {
			ThemeUtils.setStatusBarThemeColor(getActivity(), StoreThemeEnum.get("default"));
		}
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
		header = new AppViewHeader(view);
		setHasOptionsMenu(true);
	}

	private Observable<GetApp> manageOrganicAds(GetApp getApp) {
		String packageName = getApp.getNodes().getMeta().getData().getPackageName();
		String storeName = getApp.getNodes().getMeta().getData().getStore().getName();

		if (minimalAd == null) {
			return adRepository.getAdFromAppView(packageName, storeName).doOnNext(ad -> {
				minimalAd = ad;
				AptoideUtils.ThreadU.runOnUiThread(() -> ReferrerUtils.extractReferrer(ad, ReferrerUtils.RETRIES, false));
				DataproviderUtils.AdNetworksUtils.knockCpc(minimalAd);
			}).map(ad -> getApp).onErrorReturn(throwable -> getApp);
		} else {
			AptoideUtils.ThreadU.runOnUiThread(() -> ReferrerUtils.extractReferrer(minimalAd, ReferrerUtils.RETRIES, false));
			return Observable.just(getApp);
		}
	}

	@NonNull
	private Observable<GetApp> manageSuggestedAds(GetApp getApp1) {
		List<String> keywords = getApp1.getNodes().getMeta().getData().getMedia().getKeywords();

		return GetAdsRequest.ofAppviewSuggested(keywords).observe().map(getAdsResponse -> {
			if (AdRepository.validAds(getAdsResponse)) {
				suggestedAds = getAdsResponse.getAds();
			}

			return getApp1;
		});
	}

	@Override
	public void scroll(Position position) {
		RecyclerView rView = getRecyclerView();
		if (rView == null || getAdapter().getItemCount() == 0) {
			Logger.e(TAG, "Recycler view is null or there are no elements in the adapter");
			return;
		}

		if (position == Position.FIRST) {
			rView.scrollToPosition(0);
		} else if (position == Position.LAST) {
			rView.scrollToPosition(getAdapter().getItemCount());
		}
	}

	//
	// Scrollable interface
	//

	@Override
	public void itemAdded(int pos) {
		getLayoutManager().onItemsAdded(getRecyclerView(), pos, 1);
	}

	@Override
	public void itemRemoved(int pos) {
		getLayoutManager().onItemsRemoved(getRecyclerView(), pos, 1);
	}

	@Override
	public void itemChanged(int pos) {
		getLayoutManager().onItemsUpdated(getRecyclerView(), pos, 1);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (memoryArgs.containsKey(BAR_EXPANDED) && header != null && header.getAppBarLayout() != null) {
			boolean isExpanded = memoryArgs.getBoolean(BAR_EXPANDED);
			header.getAppBarLayout().setExpanded(isExpanded);
		}

		// restore download bar status
		// TODO: 04/08/16 sithengineer restore download bar status
	}

	//
	// micro widget for header
	//

	@Override
	public void onPause() {
		super.onPause();

		// save header status
		if (header != null && header.getAppBarLayout() != null) {
			boolean animationsEnabled = ManagerPreferences.getAnimationsEnabledStatus();
			memoryArgs.putBoolean(BAR_EXPANDED, animationsEnabled ? header.getAppIcon().getAlpha() > 0.9f : header.getAppIcon()
					.getVisibility() == View.VISIBLE);
		}

		// save download bar status
		// TODO: 04/08/16 sithengineer save download bar status
	}

	@Override
	public void onDestroy() {
		Context context = getContext();
		context.stopService(new Intent(context, PayPalService.class));
		super.onDestroy();
	}

	@Override
	public void setUnInstallMenuOptionVisible(@Nullable Action0 unInstallAction) {
		this.unInstallAction = unInstallAction;
		uninstallMenuItem.setVisible(unInstallAction != null);
	}

	private enum BundleKeys {
		APP_ID,
		MINIMAL_AD,
		PACKAGE_NAME,
		SHOULD_INSTALL
	}

	private final class AppViewHeader {

		private final boolean animationsEnabled;

		// views
		@Getter private final AppBarLayout appBarLayout;

		@Getter private final CollapsingToolbarLayout collapsingToolbar;

		@Getter private final ImageView featuredGraphic;

		@Getter private final ImageView badge;

		@Getter private final TextView badgeText;

		@Getter private final ImageView appIcon;

		@Getter private final TextView fileSize;

		@Getter private final TextView downloadsCount;

		// ctor
		public AppViewHeader(@NonNull View view) {
			animationsEnabled = ManagerPreferences.getAnimationsEnabledStatus();

			appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);
			collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
			appIcon = (ImageView) view.findViewById(R.id.app_icon);
			featuredGraphic = (ImageView) view.findViewById(R.id.featured_graphic);
			badge = (ImageView) view.findViewById(R.id.badge_img);
			badgeText = (TextView) view.findViewById(R.id.badge_text);
			fileSize = (TextView) view.findViewById(R.id.file_size);
			downloadsCount = (TextView) view.findViewById(R.id.downloads_count);
		}

		// setup methods
		public void setup(@NonNull GetApp getApp) {

			GetAppMeta.App app = getApp.getNodes().getMeta().getData();

			String headerImageUrl = app.getGraphic();
			List<GetAppMeta.Media.Screenshot> screenshots = app.getMedia().getScreenshots();

			if (!TextUtils.isEmpty(headerImageUrl)) {
				ImageLoader.load(app.getGraphic(), featuredGraphic);
			}
			else if (screenshots != null && screenshots.size() > 0 && !TextUtils.isEmpty(screenshots.get(0).getUrl())) {
				ImageLoader.load(screenshots.get(0).getUrl(), featuredGraphic);
			}

			if (app.getIcon() != null) {
				ImageLoader.load(getApp.getNodes().getMeta().getData().getIcon(), appIcon);
			}

			collapsingToolbar.setTitle(app.getName());
			StoreThemeEnum storeThemeEnum = StoreThemeEnum.get(storeTheme);
			collapsingToolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), storeThemeEnum.getStoreHeader()));
			collapsingToolbar.setContentScrimColor(ContextCompat.getColor(getActivity(), storeThemeEnum.getStoreHeader()));
			ThemeUtils.setStatusBarThemeColor(getActivity(), StoreThemeEnum.get(storeTheme));
			// un-comment the following lines to give app icon a fading effect when user expands / collapses the action bar
			/*
			appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {

				@Override
				public void onStateChanged(AppBarLayout appBarLayout, State state) {
					switch (state) {
						case EXPANDED:
							if (animationsEnabled) {
								appIcon.animate().alpha(1F).start();
							} else {
								appIcon.setVisibility(View.VISIBLE);
							}
							break;

						default:
						case IDLE:
						case COLLAPSED:
							if (animationsEnabled) {
								appIcon.animate().alpha(0F).start();
							} else {
								appIcon.setVisibility(View.INVISIBLE);
							}
							break;
					}
				}
			});
			*/

			fileSize.setText(AptoideUtils.StringU.formatBits(AppUtils.sumFileSizes(app.getSize(), app.getObb())));

			downloadsCount.setText(AptoideUtils.StringU.withSuffix(app.getStats().getDownloads()));

			@DrawableRes
			int badgeResId = 0;
			@StringRes
			int badgeMessageId = 0;
			switch (app.getFile().getMalware().getRank()) {
				case TRUSTED:
					badgeResId = R.drawable.ic_badge_trusted;
					badgeMessageId = R.string.appview_header_trusted_text;
					break;

				case WARNING:
					badgeResId = R.drawable.ic_badge_warning;
					badgeMessageId = R.string.warning;
					break;

				default:
				case UNKNOWN:
					badgeResId = R.drawable.ic_badge_unknown;
					badgeMessageId = R.string.unknown;
					break;
			}

			ImageLoader.load(badgeResId, badge);
			badgeText.setText(badgeMessageId);
		}
	}
}


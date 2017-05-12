/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.view.app.widget;

import android.view.View;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v2.ErrorResponse;
import cm.aptoide.pt.dataprovider.ws.v3.AddApkFlagRequest;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewFlagThisDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Map;
import okhttp3.OkHttpClient;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by sithengineer on 30/06/16.
 */
@Displayables({ AppViewFlagThisDisplayable.class }) public class AppViewFlagThisWidget
    extends Widget<AppViewFlagThisDisplayable> {

  private static final String TAG = AppViewFlagThisWidget.class.getSimpleName();

  private final Map<Integer, GetAppMeta.GetAppMetaFile.Flags.Vote.Type> viewIdTypeMap;

  private View goodAppLayoutWrapper;
  private View flagsLayoutWrapper;

  private View workingWellLayout;
  private View needsLicenseLayout;
  private View fakeAppLayout;
  private View virusLayout;

  private TextView workingWellText;
  private TextView needsLicenceText;
  private TextView fakeAppText;
  private TextView virusText;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;
  private BodyInterceptor<BaseBody> baseBodyInterceptorV3;
  private OkHttpClient httpClient;

  public AppViewFlagThisWidget(View itemView) {
    super(itemView);
    viewIdTypeMap = new HashMapNotNull<>();
    viewIdTypeMap.put(R.id.working_well_layout, GetAppMeta.GetAppMetaFile.Flags.Vote.Type.GOOD);
    viewIdTypeMap.put(R.id.needs_licence_layout, GetAppMeta.GetAppMetaFile.Flags.Vote.Type.LICENSE);
    viewIdTypeMap.put(R.id.fake_app_layout, GetAppMeta.GetAppMetaFile.Flags.Vote.Type.FAKE);
    viewIdTypeMap.put(R.id.virus_layout, GetAppMeta.GetAppMetaFile.Flags.Vote.Type.VIRUS);
  }

  @Override protected void assignViews(View itemView) {
    goodAppLayoutWrapper = itemView.findViewById(R.id.good_app_layout);
    flagsLayoutWrapper = itemView.findViewById(R.id.rating_flags_layout);

    workingWellLayout = itemView.findViewById(R.id.working_well_layout);
    needsLicenseLayout = itemView.findViewById(R.id.needs_licence_layout);
    fakeAppLayout = itemView.findViewById(R.id.fake_app_layout);
    virusLayout = itemView.findViewById(R.id.virus_layout);

    workingWellText = (TextView) itemView.findViewById(R.id.working_well_count);
    needsLicenceText = (TextView) itemView.findViewById(R.id.needs_licence_count);
    fakeAppText = (TextView) itemView.findViewById(R.id.fake_app_count);
    virusText = (TextView) itemView.findViewById(R.id.virus_count);
  }

  @Override public void bindView(AppViewFlagThisDisplayable displayable) {
    httpClient = ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    baseBodyInterceptorV3 =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV3();
    accountNavigator =
        new AccountNavigator(getFragmentNavigator(), accountManager, getActivityNavigator());
    GetApp pojo = displayable.getPojo();
    GetAppMeta.App app = pojo.getNodes()
        .getMeta()
        .getData();

    if (app.getFile()
        .isGoodApp()) {
      goodAppLayoutWrapper.setVisibility(View.VISIBLE);
      flagsLayoutWrapper.setVisibility(View.GONE);
    } else {
      goodAppLayoutWrapper.setVisibility(View.GONE);
      flagsLayoutWrapper.setVisibility(View.VISIBLE);
      bindFlagViews(app);
    }
  }

  private void bindFlagViews(GetAppMeta.App app) {
    try {
      GetAppMeta.GetAppMetaFile.Flags flags = app.getFile()
          .getFlags();
      if (flags != null && flags.getVotes() != null && !flags.getVotes()
          .isEmpty()) {
        for (final GetAppMeta.GetAppMetaFile.Flags.Vote vote : flags.getVotes()) {
          applyCount(vote.getType(), vote.getCount());
        }
      }
    } catch (NullPointerException ex) {
      CrashReport.getInstance()
          .log(ex);
    }

    View.OnClickListener buttonListener = handleButtonClick(app.getStore()
        .getName(), app.getFile()
        .getMd5sum());
    workingWellLayout.setOnClickListener(buttonListener);
    needsLicenseLayout.setOnClickListener(buttonListener);
    fakeAppLayout.setOnClickListener(buttonListener);
    virusLayout.setOnClickListener(buttonListener);
  }

  private void applyCount(GetAppMeta.GetAppMetaFile.Flags.Vote.Type type, int count) {
    String countAsString = Integer.toString(count);
    switch (type) {
      case GOOD:
        workingWellText.setText(NumberFormat.getIntegerInstance()
            .format(Double.parseDouble(countAsString)));
        break;

      case VIRUS:
        virusText.setText(NumberFormat.getIntegerInstance()
            .format(Double.parseDouble(countAsString)));
        break;

      case FAKE:
        fakeAppText.setText(NumberFormat.getIntegerInstance()
            .format(Double.parseDouble(countAsString)));
        break;

      case LICENSE:
        needsLicenceText.setText(NumberFormat.getIntegerInstance()
            .format(Double.parseDouble(countAsString)));
        break;

      case FREEZE:
        // un-used type
        break;

      default:
        throw new IllegalArgumentException("Unable to find Type " + type.name());
    }
  }

  private View.OnClickListener handleButtonClick(final String storeName, final String md5) {
    return v -> {
      if (!accountManager.isLoggedIn()) {
        ShowMessage.asSnack(v, R.string.you_need_to_be_logged_in, R.string.login, snackView -> {
          accountNavigator.navigateToAccountView(Analytics.Account.AccountOrigins.APP_VIEW_FLAG);
        });
        return;
      }

      setButtonPressed(v);

      final GetAppMeta.GetAppMetaFile.Flags.Vote.Type type = viewIdTypeMap.get(v.getId());

      compositeSubscription.add(AddApkFlagRequest.of(storeName, md5, type.name()
          .toLowerCase(), accountManager.getAccessToken(), baseBodyInterceptorV3, httpClient)
          .observe(true)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(response -> {
            if (response.isOk() && !response.hasErrors()) {
              boolean voteSubmitted = false;
              switch (type) {
                case GOOD:
                  voteSubmitted = true;
                  workingWellText.setText(NumberFormat.getIntegerInstance()
                      .format(Double.parseDouble(String.valueOf(new BigDecimal(
                          workingWellText.getText()
                              .toString()))) + 1));
                  break;

                case LICENSE:
                  voteSubmitted = true;
                  needsLicenceText.setText(NumberFormat.getIntegerInstance()
                      .format(Double.parseDouble(String.valueOf(new BigDecimal(
                          needsLicenceText.getText()
                              .toString()))) + 1));
                  break;

                case FAKE:
                  voteSubmitted = true;
                  fakeAppText.setText(NumberFormat.getIntegerInstance()
                      .format(Double.parseDouble(String.valueOf(new BigDecimal(fakeAppText.getText()
                          .toString()))) + 1));
                  break;

                case VIRUS:
                  voteSubmitted = true;
                  virusText.setText(NumberFormat.getIntegerInstance()
                      .format(Double.parseDouble(String.valueOf(new BigDecimal(virusText.getText()
                          .toString()))) + 1));
                  break;

                case FREEZE:
                  // un-used type
                  break;

                default:
                  throw new IllegalArgumentException("Unable to find Type " + type.name());
              }

              if (voteSubmitted) {
                ShowMessage.asSnack(getRootView(), R.string.vote_submitted);
                return;
              }
            }

            if (response.hasErrors()) {
              for (final ErrorResponse errorResponse : response.getErrors()) {
                Logger.e(TAG, errorResponse.getErrorDescription());
              }
            }

            setAllButtonsUnPressed(v);
            ShowMessage.asSnack(getRootView(), R.string.unknown_error);
          }, error -> {
            CrashReport.getInstance()
                .log(error);
            setAllButtonsUnPressed(v);
            ShowMessage.asSnack(getRootView(), R.string.unknown_error);
          }));
    };
  }

  private void setButtonPressed(View v) {
    v.setSelected(true);
    v.setPressed(false);
    workingWellLayout.setClickable(false);
    needsLicenseLayout.setClickable(false);
    fakeAppLayout.setClickable(false);
    virusLayout.setClickable(false);
  }

  private void setAllButtonsUnPressed(View v) {
    workingWellLayout.setClickable(true);
    needsLicenseLayout.setClickable(true);
    fakeAppLayout.setClickable(true);
    virusLayout.setClickable(true);
    v.setSelected(false);
    v.setPressed(false);
  }
}

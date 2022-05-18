package cm.aptoide.pt.home.bundles;

import cm.aptoide.pt.ads.WalletAdsOfferCardManager;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.app.DownloadStateParser;
import cm.aptoide.pt.blacklist.BlacklistManager;
import cm.aptoide.pt.bonus.BonusAppcModel;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.dataprovider.model.v7.AppCoinsCampaign;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.model.v7.Layout;
import cm.aptoide.pt.dataprovider.model.v7.ListAppCoinsCampaigns;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.model.v7.Type;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.listapp.AppCoinsInfo;
import cm.aptoide.pt.dataprovider.ws.v7.AppPromoItem;
import cm.aptoide.pt.dataprovider.ws.v7.NewAppCoinsAppPromoItem;
import cm.aptoide.pt.dataprovider.ws.v7.home.ActionItemData;
import cm.aptoide.pt.dataprovider.ws.v7.home.ActionItemResponse;
import cm.aptoide.pt.dataprovider.ws.v7.home.BonusAppcBundle;
import cm.aptoide.pt.dataprovider.ws.v7.home.EditorialActionItem;
import cm.aptoide.pt.home.AppComingSoonRegistrationManager;
import cm.aptoide.pt.home.bundles.ads.AdBundle;
import cm.aptoide.pt.home.bundles.ads.AdsTagWrapper;
import cm.aptoide.pt.home.bundles.apps.EskillsApp;
import cm.aptoide.pt.home.bundles.apps.RewardApp;
import cm.aptoide.pt.home.bundles.base.ActionBundle;
import cm.aptoide.pt.home.bundles.base.ActionItem;
import cm.aptoide.pt.home.bundles.base.AppBundle;
import cm.aptoide.pt.home.bundles.base.AppComingSoonPromotionalBundle;
import cm.aptoide.pt.home.bundles.base.BonusPromotionalBundle;
import cm.aptoide.pt.home.bundles.base.EditorialActionBundle;
import cm.aptoide.pt.home.bundles.base.FeaturedAppcBundle;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.VersionPromotionalBundle;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.app.ApplicationGraphic;
import cm.aptoide.pt.view.app.FeatureGraphicApplication;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class BundlesResponseMapper {

  private final InstallManager installManager;
  private final WalletAdsOfferCardManager walletAdsOfferCardManager;
  private final BlacklistManager blacklistManager;
  private final DownloadStateParser downloadStateParser;
  private final AppComingSoonRegistrationManager appComingSoonRegistrationManager;

  public BundlesResponseMapper(InstallManager installManager,
      WalletAdsOfferCardManager walletAdsOfferCardManager, BlacklistManager blacklistManager,
      DownloadStateParser downloadStateParser,
      AppComingSoonRegistrationManager appComingSoonRegistrationManager) {
    this.installManager = installManager;
    this.walletAdsOfferCardManager = walletAdsOfferCardManager;
    this.blacklistManager = blacklistManager;
    this.downloadStateParser = downloadStateParser;
    this.appComingSoonRegistrationManager = appComingSoonRegistrationManager;
  }

  public List<HomeBundle> fromWidgetsToBundles(List<GetStoreWidgets.WSWidget> widgetBundles) {
    List<HomeBundle> appBundles = new ArrayList<>();

    for (GetStoreWidgets.WSWidget widget : widgetBundles) {
      AppBundle.BundleType type;
      try {
        if (widget.getType()
            .equals(Type.ACTION_ITEM) || widget.getType()
            .equals(Type.NEWS_ITEM) || widget.getType()
            .equals(Type.IN_GAME_EVENT) || widget.getType()
            .equals(Type.APP_COMING_SOON)) {
          type = actionItemTypeMapper(widget);
        } else {
          type = bundleTypeMapper(widget.getType(), widget.getData());
        }

        if (type.equals(HomeBundle.BundleType.UNKNOWN)) continue;

        Event event = getEvent(widget);

        String widgetTag = widget.getTag();
        String widgetActionTag = getWidgetActionTag(widget);

        Object viewObject = widget.getViewObject();
        String title = widget.getTitle();
        if (event != null && event.getName()
            .equals(Event.Name.getStoreWidgets)) {
          event.setName(Event.Name.getMoreBundle);
        }
        if (type.equals(HomeBundle.BundleType.APPS)
            || type.equals(HomeBundle.BundleType.EDITORS)
            || type.equals(HomeBundle.BundleType.TOP)) {
          List<Application> apps = null;
          if (viewObject != null) {
            apps = map(((ListApps) viewObject).getDataList()
                .getList(), type, widgetTag);
          }
          appBundles.add(new AppBundle(title, apps, type, event, widgetTag, widgetActionTag));
        } else if (type.equals(HomeBundle.BundleType.FEATURED_BONUS_APPC)) {
          List<Application> apps = null;
          int percentage = -1;
          boolean hasBonus = true;
          if (viewObject instanceof BonusAppcBundle) {
            BonusAppcBundle bundle = (BonusAppcBundle) viewObject;
            hasBonus = bundle.getBonusAppcModel()
                .getHasBonusAppc();
            apps = map(bundle.getListApps()
                .getDataList()
                .getList(), type, widgetTag);
            percentage = bundle.getBonusAppcModel()
                .getBonusPercentage();
          }
          if (hasBonus) {
            appBundles.add(
                new FeaturedAppcBundle(title, apps, type, event, widgetTag, widgetActionTag,
                    percentage));
          } else {
            appBundles.add(new AppBundle(title, apps, HomeBundle.BundleType.APPS, event, widgetTag,
                widgetActionTag));
          }
        } else if (type.equals(HomeBundle.BundleType.APPCOINS_ADS)) {
          List<Application> applicationList = null;
          if (viewObject != null) {
            applicationList = map(((ListAppCoinsCampaigns) viewObject).getDataList()
                .getList(), widgetTag);
          }
          if (applicationList == null || !applicationList.isEmpty()) {
            appBundles.add(new AppBundle(title, applicationList, HomeBundle.BundleType.APPCOINS_ADS,
                new Event().setName(Event.Name.getAppCoinsAds), widgetTag, widgetActionTag));
          }
        } else if (type.equals(HomeBundle.BundleType.ESKILLS)) {
          List<Application> applicationList = null;
          if (viewObject != null) {
            applicationList = map(((ListApps) viewObject).getDataList()
                .getList(), type, widgetTag);
          }
          if (applicationList == null || !applicationList.isEmpty()) {
            appBundles.add(new AppBundle(title, applicationList, HomeBundle.BundleType.ESKILLS,
                event.setName(Event.Name.eSkills), widgetTag, widgetActionTag));
          }
        } else if (type.equals(HomeBundle.BundleType.ADS)) {
          List<GetAdsResponse.Ad> adsList = null;
          if (viewObject != null) {
            adsList = ((GetAdsResponse) viewObject).getAds();
          }
          appBundles.add(new AdBundle(title, new AdsTagWrapper(adsList, widgetTag),
              new Event().setName(Event.Name.getAds), widgetTag));
        } else if (type.equals(HomeBundle.BundleType.EDITORIAL) || type.equals(
            HomeBundle.BundleType.NEWS_ITEM) || type.equals(HomeBundle.BundleType.IN_GAME_EVENT)) {
          if (viewObject instanceof EditorialActionItem) {
            EditorialActionItem editorialActionItem = ((EditorialActionItem) viewObject);
            BonusAppcModel bonusAppcModel = editorialActionItem.getBonusAppcModel();
            appBundles.add(new EditorialActionBundle(title, type, event, widgetTag,
                map(editorialActionItem.getActionItemResponse()), bonusAppcModel));
          } else {
            appBundles.add(new ActionBundle(title, type, event, widgetTag,
                map((ActionItemResponse) viewObject)));
          }
        } else if (type.equals(HomeBundle.BundleType.INFO_BUNDLE)) {
          ActionItem actionItem = map((ActionItemResponse) viewObject);
          if (actionItem == null || !blacklistManager.isBlacklisted(type.toString(),
              actionItem.getCardId())) {
            appBundles.add(new ActionBundle(title, type, event, widgetTag, actionItem));
          }
        } else if (type.equals(HomeBundle.BundleType.WALLET_ADS_OFFER)) {
          ActionItem actionItem = map((ActionItemResponse) viewObject);
          if (actionItem == null || walletAdsOfferCardManager.shouldShowWalletOfferCard(
              type.toString(), actionItem.getCardId())) {
            appBundles.add(new ActionBundle(title, type, event, widgetTag, actionItem));
          }
        } else if (type.equals(HomeBundle.BundleType.NEW_APP)) {
          NewAppCoinsAppPromoItem promoItem = (NewAppCoinsAppPromoItem) viewObject;
          if (promoItem != null) {
            ApplicationGraphic app = map(promoItem.getGetApp()
                .getNodes()
                .getMeta()
                .getData(), widgetTag);
            Install install = getInstall(promoItem, app);
            appBundles.add(new BonusPromotionalBundle(title, type, event, widgetTag, app,
                new DownloadModel(downloadStateParser.parseDownloadType(install.getType(), false),
                    install.getProgress(),
                    downloadStateParser.parseDownloadState(install.getState(),
                        install.isIndeterminate()), install.getAppSize()),
                promoItem.getBonusAppcModel()
                    .getBonusPercentage()));
          } else {
            appBundles.add(
                new BonusPromotionalBundle(title, type, event, widgetTag, null, null, 0));
          }
        } else if (type.equals(HomeBundle.BundleType.NEW_APP_VERSION)) {
          AppPromoItem promoItem = (AppPromoItem) viewObject;
          if (promoItem != null) {

            ApplicationGraphic app = map(promoItem.getGetApp()
                .getNodes()
                .getMeta()
                .getData(), widgetTag);
            Install install = getInstall(promoItem, app);
            appBundles.add(new VersionPromotionalBundle(title, type, event, widgetTag, app,
                promoItem.getGetApp()
                    .getNodes()
                    .getMeta()
                    .getData()
                    .getFile()
                    .getVername(),
                new DownloadModel(downloadStateParser.parseDownloadType(install.getType(), false),
                    install.getProgress(),
                    downloadStateParser.parseDownloadState(install.getState(),
                        install.isIndeterminate()), install.getAppSize())));
          } else {
            appBundles.add(
                new VersionPromotionalBundle(title, type, event, widgetTag, null, null, null));
          }
        } else if (type.equals(HomeBundle.BundleType.APP_COMING_SOON)) {
          ActionItem actionItem = map((ActionItemResponse) viewObject);
          boolean isNotificationScheduled =
              appComingSoonRegistrationManager.isNotificationScheduled(actionItem.getPackageName())
                  .toBlocking()
                  .first();
          appBundles.add(
              new AppComingSoonPromotionalBundle(title, type, event, widgetTag, actionItem,
                  isNotificationScheduled));
        }
      } catch (Exception e) {
        e.printStackTrace();
        Logger.getInstance()
            .d(this.getClass()
                    .getName(),
                "Something went wrong with widget to bundle mapping : " + e.getMessage());
      }
    }

    return appBundles;
  }

  private Install getInstall(AppPromoItem promoItem, ApplicationGraphic app) {
    return installManager.getInstall(promoItem.getGetApp()
        .getNodes()
        .getMeta()
        .getData()
        .getMd5(), app.getPackageName(), promoItem.getGetApp()
        .getNodes()
        .getMeta()
        .getData()
        .getFile()
        .getVercode())
        .toBlocking()
        .first();
  }

  private ApplicationGraphic map(GetAppMeta.App app, String widgetTag) {
    return new ApplicationGraphic(app.getName(), app.getIcon(), app.getStats()
        .getGlobalRating()
        .getAvg(), app.getStats()
        .getPdownloads(), app.getPackageName(), app.getId(), widgetTag, app.hasBilling(),
        app.getGraphic());
  }

  private String getWidgetActionTag(GetStoreWidgets.WSWidget widget) {
    String widgetActionTag = "";
    if (widget.hasActions()) {
      widgetActionTag = widget.getActions()
          .get(0)
          .getTag();
    }
    return widgetActionTag;
  }

  private ActionItem map(ActionItemResponse viewObject) {
    if (viewObject == null) return null;

    ActionItemData item = viewObject.getDataList()
        .getList()
        .get(0);
    return new ActionItem(item.getId(), item.getType() != null ? item.getType() : "",
        item.getTitle(), item.getCaption(), item.getIcon(), item.getUrl(), item.getViews(),
        item.getDate(), item.getAppearance() != null ? item.getAppearance()
        .getCaption()
        .getTheme() : "", item.getFlair() != null ? item.getFlair() : "", item.getSummary(),
        item.getPackageName(), item.getGraphic());
  }

  private HomeBundle.BundleType actionItemTypeMapper(GetStoreWidgets.WSWidget widget) {
    if (widget.getData() != null) {
      switch (widget.getData()
          .getLayout()) {
        case APPC_INFO:
          return HomeBundle.BundleType.INFO_BUNDLE;
        case CURATION_1:
          return HomeBundle.BundleType.EDITORIAL;
        case WALLET_ADS_OFFER:
          return HomeBundle.BundleType.WALLET_ADS_OFFER;
        case PROMO_GRAPHIC:
          if (widget.getType()
              .equals(Type.NEWS_ITEM)) {
            return HomeBundle.BundleType.NEWS_ITEM;
          } else if (widget.getType()
              .equals(Type.IN_GAME_EVENT)) {
            return HomeBundle.BundleType.IN_GAME_EVENT;
          } else if (widget.getType()
              .equals(Type.APP_COMING_SOON)) {
            return HomeBundle.BundleType.APP_COMING_SOON;
          }
      }
    }
    return HomeBundle.BundleType.UNKNOWN;
  }

  private Event getEvent(GetStoreWidgets.WSWidget widget) {
    return widget.getActions() != null
        && widget.getActions()
        .size() > 0 ? widget.getActions()
        .get(0)
        .getEvent() : null;
  }

  private HomeBundle.BundleType bundleTypeMapper(Type type, GetStoreWidgets.WSWidget.Data data) {
    if (type == null) {
      return HomeBundle.BundleType.UNKNOWN;
    }
    if (data != null && data.isEskills()) {
      type = Type.ESKILLS;
    }
    switch (type) {
      case APPS_GROUP:
        if (data == null) {
          return HomeBundle.BundleType.UNKNOWN;
        }
        if (data.getLayout()
            .equals(Layout.BRICK)) {
          return HomeBundle.BundleType.EDITORS;
        } else {
          return HomeBundle.BundleType.APPS;
        }
      case APPCOINS_ADS:
        return HomeBundle.BundleType.APPCOINS_ADS;
      case ESKILLS:
        return HomeBundle.BundleType.ESKILLS;
      case APPCOINS_FEATURED:
        return HomeBundle.BundleType.FEATURED_BONUS_APPC;
      case ADS:
        return HomeBundle.BundleType.ADS;
      case APPS_TOP_GROUP:
        return HomeBundle.BundleType.TOP;
      case NEW_APP:
        return HomeBundle.BundleType.NEW_APP;
      case NEW_APP_VERSION:
        return HomeBundle.BundleType.NEW_APP_VERSION;
      default:
        return HomeBundle.BundleType.APPS;
    }
  }

  private List<Application> map(List<App> apps, AppBundle.BundleType type, String tag) {
    if (apps == null || apps.isEmpty()) {
      return Collections.emptyList();
    }
    List<Application> applications = new ArrayList<>();
    for (App app : apps) {
      try {
        if (type.equals(HomeBundle.BundleType.EDITORS)) {
          AppCoinsInfo appc = app.getAppcoins();
          applications.add(new FeatureGraphicApplication(app.getName(), app.getIcon(),
              app.getStats()
                  .getRating()
                  .getAvg(), app.getStats()
              .getPdownloads(), app.getPackageName(), app.getId(), app.getGraphic(), tag,
              appc != null && appc.hasBilling(), appc != null && appc.hasAdvertising()));
        } else if (type.equals(HomeBundle.BundleType.ESKILLS)) {
          AppCoinsInfo appc = app.getAppcoins();
          applications.add(new EskillsApp(app.getName(), app.getIcon(), app.getStats()
              .getRating()
              .getAvg(), app.getStats()
              .getPdownloads(), app.getPackageName(), app.getId(), tag,
              appc != null && appc.hasBilling(), app.getGraphic()));
        } else {
          AppCoinsInfo appc = app.getAppcoins();
          applications.add(new Application(app.getName(), app.getIcon(), app.getStats()
              .getRating()
              .getAvg(), app.getStats()
              .getPdownloads(), app.getPackageName(), app.getId(), tag,
              appc != null && appc.hasBilling()));
        }
      } catch (Exception e) {
        Logger.getInstance()
            .d(this.getClass()
                    .getName(),
                "Something went wrong while parsing apps to applications: " + e.getMessage());
      }
    }

    return applications;
  }

  private List<Application> map(List<AppCoinsCampaign> appsList, String tag) {
    List<Application> rewardAppsList = new ArrayList<>();
    for (AppCoinsCampaign campaign : appsList) {
      AppCoinsCampaign.CampaignApp app = campaign.getApp();
      if (!installManager.wasAppEverInstalled(app.getPackageName())) {
        rewardAppsList.add(new RewardApp(app.getName(), app.getIcon(), app.getStats()
            .getRating()
            .getAvg(), app.getStats()
            .getPdownloads(), app.getPackageName(), app.getId(), tag, app.getAppcoins() != null,
            app.getAppcoins()
                .getClicks()
                .getClick(), app.getAppcoins()
            .getClicks()
            .getInstall(), mapReward(campaign.getReward()), app.getGraphic()));
      }
    }
    return rewardAppsList;
  }

  private RewardApp.Reward mapReward(AppCoinsCampaign.Reward reward) {
    AppCoinsCampaign.Fiat fiat = reward.getFiat();
    return new RewardApp.Reward(reward.getAppc(),
        new RewardApp.Fiat(fiat.getAmount(), fiat.getCurrency(), fiat.getSymbol()));
  }
}

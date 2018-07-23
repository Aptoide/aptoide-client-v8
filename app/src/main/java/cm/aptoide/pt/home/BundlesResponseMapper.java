package cm.aptoide.pt.home;

import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.dataprovider.model.v7.AppCoinsCampaign;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.model.v7.Layout;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.model.v7.ListRewardApps;
import cm.aptoide.pt.dataprovider.model.v7.Type;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.listapp.AppCoinsInfo;
import cm.aptoide.pt.dataprovider.ws.v7.home.Card;
import cm.aptoide.pt.dataprovider.ws.v7.home.SocialResponse;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.PackageRepository;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.app.FeatureGraphicApplication;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cm.aptoide.pt.home.SocialBundle.CardType.APTOIDE_RECOMMENDS;
import static cm.aptoide.pt.home.SocialBundle.CardType.SOCIAL_RECOMMENDATIONS;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class BundlesResponseMapper {

  private final String marketName;
  private final InstallManager installManager;
  private final PackageRepository packageRepository;

  public BundlesResponseMapper(String marketName, InstallManager installManager,
      PackageRepository packageRepository) {
    this.marketName = marketName;
    this.installManager = installManager;
    this.packageRepository = packageRepository;
  }

  public List<HomeBundle> fromWidgetsToBundles(List<GetStoreWidgets.WSWidget> widgetBundles) {
    List<HomeBundle> appBundles = new ArrayList<>();

    for (GetStoreWidgets.WSWidget widget : widgetBundles) {
      AppBundle.BundleType type = bundleTypeMapper(widget.getType(), widget.getData());

      if (type.equals(HomeBundle.BundleType.ERROR)) continue;

      Event event = getEvent(widget);

      try {
        String widgetTag = widget.getTag();
        Object viewObject = widget.getViewObject();
        String title = widget.getTitle();
        if (event != null && event.getName()
            .equals(Event.Name.getStoreWidgets)) {
          event.setName(Event.Name.getMoreBundle);
        }
        if (type.equals(HomeBundle.BundleType.APPS) || type.equals(HomeBundle.BundleType.EDITORS)) {
          appBundles.add(new AppBundle(title, map(((ListApps) viewObject).getDataList()
              .getList(), type, widgetTag), type, event, widgetTag));
        } else if (type.equals(HomeBundle.BundleType.APPCOINS_ADS)) {
          List<Application> applicationList = map(((ListRewardApps) viewObject).getDataList()
              .getList(), widgetTag);
          if (!applicationList.isEmpty()) {
            appBundles.add(new AppBundle(title, applicationList, HomeBundle.BundleType.APPCOINS_ADS,
                new Event().setName(Event.Name.getAppCoinsAds), widgetTag));
          }
        } else if (type.equals(HomeBundle.BundleType.ADS)) {
          appBundles.add(new AdBundle(title,
              new AdsTagWrapper(((GetAdsResponse) viewObject).getAds(), widgetTag),
              new Event().setName(Event.Name.getAds), widgetTag));
        } else if (type.equals(HomeBundle.BundleType.SOCIAL)) {
          List<Card> list = ((SocialResponse) viewObject).getDataList()
              .getList();
          List<App> apps = new ArrayList<>();
          for (Card card : list) {
            App app = card.getApp();
            if (!packageRepository.isAppInstalled(app.getPackageName())) {
              apps.add(app);
              if (card.hasUser()) {
                appBundles.add(new SocialBundle(map(apps, type, widgetTag), type, event, widgetTag,
                    card.getUser()
                        .getAvatar(), card.getUser()
                    .getName(), SOCIAL_RECOMMENDATIONS));
              } else {
                appBundles.add(new SocialBundle(map(apps, type, widgetTag), type, event, widgetTag,
                    R.mipmap.ic_launcher, marketName, APTOIDE_RECOMMENDS));
              }
            }
          }
        }
      } catch (Exception e) {
        Logger.getInstance()
            .d(this.getClass()
                    .getName(),
                "Something went wrong with widget to bundle mapping : " + e.getMessage());
      }
    }

    return appBundles;
  }

  private Event getEvent(GetStoreWidgets.WSWidget widget) {
    return widget.getActions()
        .size() > 0 ? widget.getActions()
        .get(0)
        .getEvent() : null;
  }

  private HomeBundle.BundleType bundleTypeMapper(Type type, GetStoreWidgets.WSWidget.Data data) {
    if (type == null) {
      return HomeBundle.BundleType.ERROR;
    }
    switch (type) {
      case APPS_GROUP:
        if (data == null) {
          return HomeBundle.BundleType.ERROR;
        }
        if (data.getLayout()
            .equals(Layout.BRICK)) {
          return HomeBundle.BundleType.EDITORS;
        } else {
          return HomeBundle.BundleType.APPS;
        }
      case APPCOINS_ADS:
        return HomeBundle.BundleType.APPCOINS_ADS;
      case ADS:
        return HomeBundle.BundleType.ADS;
      case TIMELINE_CARD:
        return HomeBundle.BundleType.SOCIAL;
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
              appc != null && appc.hasBilling(), appc != null && appc.hasAdvertising(), ""));
        } else {
          AppCoinsInfo appc = app.getAppcoins();
          applications.add(new Application(app.getName(), app.getIcon(), app.getStats()
              .getRating()
              .getAvg(), app.getStats()
              .getPdownloads(), app.getPackageName(), app.getId(), tag,
              appc != null && appc.hasBilling(), appc != null && appc.hasAdvertising(), ""));
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
      App app = campaign.getApp();
      if (!installManager.wasAppEverInstalled(app.getPackageName())) {
        rewardAppsList.add(new RewardApp(app.getName(), app.getIcon(), app.getStats()
            .getRating()
            .getAvg(), app.getStats()
            .getPdownloads(), app.getPackageName(), app.getId(), tag,
            Double.valueOf(campaign.getReward())));
      }
    }
    return rewardAppsList;
  }
}

package cm.aptoide.pt.home;

import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.model.v7.Layout;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.model.v7.Type;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.ws.v7.home.Card;
import cm.aptoide.pt.dataprovider.ws.v7.home.SocialResponse;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.app.FeatureGraphicApplication;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class BundlesResponseMapper {

  private final String marketName;

  public BundlesResponseMapper(String marketName) {
    this.marketName = marketName;
  }

  public List<HomeBundle> fromWidgetsToBundles(List<GetStoreWidgets.WSWidget> widgetBundles) {

    List<HomeBundle> appBundles = new ArrayList<>();

    for (GetStoreWidgets.WSWidget widget : widgetBundles) {
      AppBundle.BundleType type = bundleTypeMapper(widget.getType(), widget.getData());

      if (type.equals(HomeBundle.BundleType.ERROR)) continue;

      Event event = getEvent(widget);

      try {
        if (type.equals(HomeBundle.BundleType.APPS) || type.equals(HomeBundle.BundleType.EDITORS)) {

          appBundles.add(new AppBundle(widget.getTitle(), applicationsToApps(
              ((ListApps) widget.getViewObject()).getDataList()
                  .getList(), type, widget.getTag()), type, event, widget.getTag()));
        } else if (type.equals(HomeBundle.BundleType.ADS)) {
          appBundles.add(new AdBundle(widget.getTitle(),
              new AdsTagWrapper(((GetAdsResponse) widget.getViewObject()).getAds(),
                  widget.getTag()), new Event().setName(Event.Name.getAds), widget.getTag()));
        } else if (type.equals(HomeBundle.BundleType.SOCIAL)) {
          List<Card> list = ((SocialResponse) widget.getViewObject()).getDataList()
              .getList();
          if (!list.isEmpty()) {
            List<App> apps = new ArrayList<>();
            Card card = list.get(0);
            App app = card.getApp();
            apps.add(app);
            if (card.hasUser()) {
              appBundles.add(
                  new SocialBundle(applicationsToApps(apps, type, widget.getTag()), type, event,
                      widget.getTag(), card.getUser()
                      .getAvatar(), card.getUser()
                      .getName()));
            } else {
              appBundles.add(
                  new SocialBundle(applicationsToApps(apps, type, widget.getTag()), type, event,
                      widget.getTag(), R.mipmap.ic_launcher, marketName));
            }
          }
        }
      } catch (Exception ignore) {
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
      case ADS:
        return HomeBundle.BundleType.ADS;
      case TIMELINE_CARD:
        return HomeBundle.BundleType.SOCIAL;
      default:
        return HomeBundle.BundleType.APPS;
    }
  }

  private List<Application> applicationsToApps(List<App> apps, AppBundle.BundleType type,
      String tag) {
    if (apps == null || apps.isEmpty()) {
      return Collections.emptyList();
    }
    List<Application> applications = new ArrayList<>();
    for (App app : apps) {
      if (type.equals(HomeBundle.BundleType.EDITORS)) {
        applications.add(new FeatureGraphicApplication(app.getName(), app.getIcon(), app.getStats()
            .getRating()
            .getAvg(), app.getStats()
            .getPdownloads(), app.getPackageName(), app.getId(), app.getGraphic(), tag,
            app.getStore()
                .getName()));
      } else {
        applications.add(new Application(app.getName(), app.getIcon(), app.getStats()
            .getRating()
            .getAvg(), app.getStats()
            .getPdownloads(), app.getPackageName(), app.getId(), tag, app.getStore()
            .getName()));
      }
    }

    return applications;
  }
}

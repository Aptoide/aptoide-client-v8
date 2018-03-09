package cm.aptoide.pt.home;

import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.model.v7.BundlesDataList;
import cm.aptoide.pt.dataprovider.model.v7.BundlesEndlessDataListResponse;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.app.FeatureGraphicApplication;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.functions.Func1;

/**
 * Created by jdandrade on 08/03/2018.
 */

public class BundlesResponseMapper {
  @NonNull Func1<BundlesEndlessDataListResponse, List<AppBundle>> map() {
    return bundlesResponse -> fromWidgetsToBundles(bundlesResponse.getDataList()
        .getList());
  }

  private List<AppBundle> fromWidgetsToBundles(List<BundlesDataList.Bundle> widgetBundles) {

    List<AppBundle> appBundles = new ArrayList<>();

    for (BundlesDataList.Bundle widget : widgetBundles) {
      AppBundle.BundleType type = bundleTypeMapper(widget.getType());
      appBundles.add(
          new AppBundle(widget.getTitle(), applicationsToApps(widget.getApps(), type), type));
    }

    return appBundles;
  }

  private AppBundle.BundleType bundleTypeMapper(String type) {
    switch (type) {
      case "Editors":
        return AppBundle.BundleType.EDITORS;
      case "APPS":
        return AppBundle.BundleType.APPS;
      default:
        return AppBundle.BundleType.APPS;
    }
  }

  private List<Application> applicationsToApps(List<App> apps, AppBundle.BundleType type) {
    if (apps == null || apps.isEmpty()) {
      return Collections.EMPTY_LIST;
    }
    List<Application> applications = new ArrayList<>();
    for (App app : apps) {
      if (type.equals(AppBundle.BundleType.EDITORS)) {
        applications.add(new FeatureGraphicApplication(app.getName(), app.getIcon(), app.getStats()
            .getRating()
            .getAvg(), app.getStats()
            .getPdownloads(), app.getPackageName(), app.getId(), app.getGraphic()));
      } else {
        applications.add(new Application(app.getName(), app.getIcon(), app.getStats()
            .getRating()
            .getAvg(), app.getStats()
            .getPdownloads(), app.getPackageName(), app.getId()));
      }
    }

    return applications;
  }
}

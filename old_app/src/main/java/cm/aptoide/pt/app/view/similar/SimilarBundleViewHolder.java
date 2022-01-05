package cm.aptoide.pt.app.view.similar;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.pt.app.AppViewSimilarApp;
import cm.aptoide.pt.app.SimilarAppsViewModel;
import cm.aptoide.pt.view.app.Application;
import java.util.ArrayList;
import java.util.List;

public abstract class SimilarBundleViewHolder extends RecyclerView.ViewHolder {

  public SimilarBundleViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void setBundle(SimilarAppsBundle homeBundle, int position);

  public List<AppViewSimilarApp> mapToSimilar(SimilarAppsViewModel similarApps, boolean hasAd) {
    List<AppViewSimilarApp> resultList = new ArrayList<>();

    if (hasAd) resultList.add(new AppViewSimilarApp(null, similarApps.getAd()));

    for (Application app : similarApps.getRecommendedApps())
      resultList.add(new AppViewSimilarApp(app, null));

    return resultList;
  }
}


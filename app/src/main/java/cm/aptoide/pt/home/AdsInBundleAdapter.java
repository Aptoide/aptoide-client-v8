package cm.aptoide.pt.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import java.text.DecimalFormat;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 13/03/2018.
 */

class AdsInBundleAdapter extends RecyclerView.Adapter<AdInBundleViewHolder> {
  private final DecimalFormat oneDecimalFormatter;
  private final PublishSubject<AdHomeEvent> adClickedEvents;
  private List<AdClick> ads;
  private HomeBundle homeBundle;
  private int bundlePosition;

  public AdsInBundleAdapter(List<AdClick> ads, DecimalFormat oneDecimalFormatter,
      PublishSubject<AdHomeEvent> adClickedEvents) {
    this.ads = ads;
    this.oneDecimalFormatter = oneDecimalFormatter;
    this.adClickedEvents = adClickedEvents;
    this.homeBundle = null;
    this.bundlePosition = -1;
  }

  public void update(List<AdClick> ads) {
    this.ads = ads;
    notifyDataSetChanged();
  }

  public void updateBundle(HomeBundle homeBundle, int bundlePosition) {
    this.homeBundle = homeBundle;
    this.bundlePosition = bundlePosition;
  }

  @Override public AdInBundleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new AdInBundleViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.displayable_grid_ad, parent, false), adClickedEvents,
        oneDecimalFormatter);
  }

  @Override public void onBindViewHolder(AdInBundleViewHolder viewHolder, int position) {
    viewHolder.setApp(ads.get(position), homeBundle, bundlePosition, position);
  }

  @Override public int getItemViewType(int position) {
    return ads.get(position)
        .getAd()
        .getNetwork()
        .ordinal();
  }

  @Override public int getItemCount() {
    return ads.size();
  }

}

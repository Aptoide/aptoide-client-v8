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
  private final PublishSubject<WrappedAdTag> adClickedEvents;
  private List<WrappedAdTag> ads;

  public AdsInBundleAdapter(List<WrappedAdTag> ads, DecimalFormat oneDecimalFormatter,
      PublishSubject<WrappedAdTag> adClickedEvents) {
    this.ads = ads;
    this.oneDecimalFormatter = oneDecimalFormatter;
    this.adClickedEvents = adClickedEvents;
  }

  public void update(List<WrappedAdTag> ads) {
    this.ads = ads;
    notifyDataSetChanged();
  }

  @Override public AdInBundleViewHolder onCreateViewHolder(ViewGroup parent, int position) {
    return new AdInBundleViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.app_home_item, parent, false), adClickedEvents, oneDecimalFormatter);
  }

  @Override public void onBindViewHolder(AdInBundleViewHolder viewHolder, int position) {
    viewHolder.setApp(ads.get(position));
  }

  @Override public int getItemCount() {
    return ads.size();
  }
}

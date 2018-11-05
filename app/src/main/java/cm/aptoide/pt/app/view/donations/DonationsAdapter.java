package cm.aptoide.pt.app.view.donations;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by franciscocalado on 11/10/2018.
 */

public class DonationsAdapter extends RecyclerView.Adapter<DonationListEntryViewHolder> {

  private List<Donation> donations;

  public DonationsAdapter(List<Donation> donations) {
    this.donations = donations;
  }

  @Override public DonationListEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new DonationListEntryViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.appview_donations_list_entry, parent, false), new DecimalFormat("0.##"));
  }

  @Override public void onBindViewHolder(DonationListEntryViewHolder holder, int position) {
    holder.setUp(position + 1, donations.get(position)
        .getOwner(), donations.get(position)
        .getAppc());
  }

  @Override public int getItemCount() {
    return donations.size();
  }

  public void setDonations(List<Donation> donations) {
    this.donations = donations;
    notifyDataSetChanged();
  }
}

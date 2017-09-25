package cm.aptoide.pt.spotandshareapp.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.spotandshareandroid.transfermanager.Transfer;
import cm.aptoide.pt.spotandshareapp.SpotAndShareTransfer;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by filipe on 05-07-2017.
 */

public class SpotAndShareTransferRecordAdapter
    extends RecyclerView.Adapter<SpotAndShareTransferRecordAdapter.TransferViewHolder> {

  private static final int RECEIVED_APP = 0;
  private static final int SENT_APP = 1;

  private List<SpotAndShareTransfer> appsTransfered;
  private SpotAndShareTransferRecordCardProvider spotAndShareTransferRecordCardProvider;

  public SpotAndShareTransferRecordAdapter(
      SpotAndShareTransferRecordCardProvider spotAndShareTransferRecordCardProvider) {
    this.spotAndShareTransferRecordCardProvider = spotAndShareTransferRecordCardProvider;
    this.appsTransfered = new LinkedList<>();
  }

  @Override public TransferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == SENT_APP) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.fragment_spotandshare_transfer_record_item_sent, parent, false);
      return new TransferViewHolder(view, spotAndShareTransferRecordCardProvider);
    } else if (viewType == RECEIVED_APP) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.fragment_spotandshare_transfer_record_item_received, parent, false);
      return new TransferViewHolder(view, spotAndShareTransferRecordCardProvider);
    }
    throw new IllegalArgumentException(
        "ViewType argument must be either " + RECEIVED_APP + " or " + SENT_APP);
  }

  @Override public void onBindViewHolder(TransferViewHolder holder, int position) {
    holder.setTransferItem(appsTransfered.get(position));
  }

  @Override public int getItemCount() {
    return appsTransfered.size();
  }

  @Override public int getItemViewType(int position) {
    if (appsTransfered.get(position)
        .getAppsList()
        .get(0)
        .getTransferState() == Transfer.State.SERVING) {
      return SENT_APP;
    } else {
      return RECEIVED_APP;
    }
  }

  public void updateTransferList(List<SpotAndShareTransfer> transferAppModelList) {
    this.appsTransfered = transferAppModelList;
    notifyDataSetChanged();
  }

  public void removeAll() {
    appsTransfered.clear();
    notifyDataSetChanged();
    appsTransfered = null;
  }

  class TransferViewHolder extends RecyclerView.ViewHolder {

    private SpotAndShareTransferRecordCardProvider cardProvider;
    private ImageView senderAvatar;
    private TextView senderName;
    private LinearLayout cardContentLayout;

    public TransferViewHolder(View itemView, SpotAndShareTransferRecordCardProvider cardProvider) {
      super(itemView);
      this.cardProvider = cardProvider;
      senderAvatar = (ImageView) itemView.findViewById(R.id.transfer_record_header_sender_avatar);
      senderName = (TextView) itemView.findViewById(R.id.transfer_record_header_sender_info);
      cardContentLayout =
          (LinearLayout) itemView.findViewById(R.id.spotandshare_transfer_card_content);
    }

    public void setTransferItem(SpotAndShareTransfer transfer) {
      senderAvatar.setImageDrawable(transfer.getSenderUser()
          .getAvatar());
      senderName.setText(itemView.getContext()
          .getResources()
          .getString(R.string.spotandshare_message_app_sender_info_sending, transfer.getSenderUser()
              .getUsername()));

      cardContentLayout.addView(
          cardProvider.getView(LayoutInflater.from(itemView.getContext()), transfer.getAppsList(),
              itemView.getContext()));
    }
  }
}

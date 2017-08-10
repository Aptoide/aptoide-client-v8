package cm.aptoide.pt.spotandshareapp.view;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.spotandshareandroid.transfermanager.Transfer;
import cm.aptoide.pt.spotandshareapp.R;
import cm.aptoide.pt.spotandshareapp.TransferAppModel;
import java.util.LinkedList;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by filipe on 05-07-2017.
 */

public class SpotAndShareTransferRecordAdapter
    extends RecyclerView.Adapter<SpotAndShareTransferRecordAdapter.TransferViewHolder> {

  private List<TransferAppModel> appsTransfered;
  private PublishSubject<TransferAppModel> acceptSubject;
  private PublishSubject<TransferAppModel> installSubject;

  public SpotAndShareTransferRecordAdapter(PublishSubject<TransferAppModel> acceptSubject,
      PublishSubject<TransferAppModel> installApp) {
    this.installSubject = installApp;
    this.appsTransfered = new LinkedList<>();
    this.acceptSubject = acceptSubject;
  }

  @Override public TransferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_spotandshare_transfer_record_item, parent, false);
    return new TransferViewHolder(view);
  }

  @Override public void onBindViewHolder(TransferViewHolder holder, int position) {
    holder.setTransferItem(appsTransfered.get(position));
  }

  @Override public int getItemCount() {
    return appsTransfered.size();
  }

  public void updateTransferList(List<TransferAppModel> transferAppModelList) {
    this.appsTransfered = transferAppModelList;
    notifyDataSetChanged();
  }

  public void removeAll() {
    appsTransfered.clear();
    notifyDataSetChanged();
    acceptSubject = null;
    installSubject = null;
    appsTransfered = null;
  }

  class TransferViewHolder extends ViewHolder {

    private TextView senderName;
    private ImageView appIcon;
    private Button acceptButton;
    private Button installButton;
    private ProgressBar transferProgressBar;

    public TransferViewHolder(View itemView) {
      super(itemView);
      senderName = (TextView) itemView.findViewById(R.id.sender_info);
      appIcon = (ImageView) itemView.findViewById(R.id.transfer_app_icon);
      acceptButton = (Button) itemView.findViewById(R.id.transfer_record_accept_app_button);
      installButton = (Button) itemView.findViewById(R.id.transfer_record_install_app_button);
      transferProgressBar = (ProgressBar) itemView.findViewById(R.id.transfer_record_progress_bar);
    }

    public void setTransferItem(TransferAppModel transferItem) {
      System.out.println("item:"
          + transferItem.getAppName()
          + " transfer state: "
          + transferItem.getTransferState());
      senderName.setText(itemView.getContext()
          .getResources()
          .getString(R.string.spotandshare_message_app_sender_info_sending, transferItem.getFriend()
              .getUsername()));

      appIcon.setImageDrawable(transferItem.getAppIcon());
      acceptButton.setOnClickListener(accept -> acceptSubject.onNext(transferItem));
      installButton.setOnClickListener(accept -> installSubject.onNext(transferItem));

      if (transferItem.getTransferState() == Transfer.State.PENDING_ACCEPTION) {
        acceptButton.setVisibility(View.VISIBLE);
      } else if (transferItem.getTransferState() == Transfer.State.RECEIVING) {
        acceptButton.setVisibility(View.VISIBLE);
        transferProgressBar.setVisibility(View.VISIBLE);
        transferProgressBar.setIndeterminate(true);
      } else if (transferItem.getTransferState() == Transfer.State.RECEIVED) {
        senderName.setText(itemView.getContext()
            .getResources()
            .getString(R.string.spotandshare_message_app_sender_info_sent, transferItem.getFriend()
                .getUsername()));
        acceptButton.setVisibility(View.GONE);
        if (!transferItem.isInstalled()) {
          installButton.setVisibility(View.VISIBLE);
        } else {
          installButton.setVisibility(View.GONE);
        }
        transferProgressBar.setVisibility(View.INVISIBLE);
      } else if (transferItem.getTransferState() == Transfer.State.SERVING) {
        acceptButton.setVisibility(View.GONE);
        transferProgressBar.setIndeterminate(true);
      } else {
        acceptButton.setVisibility(View.GONE);
        transferProgressBar.setVisibility(View.GONE);
        installButton.setVisibility(View.GONE);
      }
    }
  }
}

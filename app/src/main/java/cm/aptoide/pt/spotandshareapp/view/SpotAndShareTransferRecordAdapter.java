package cm.aptoide.pt.spotandshareapp.view;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.spotandshareandroid.transfermanager.Transfer;
import cm.aptoide.pt.spotandshareapp.TransferAppModel;
import java.util.LinkedList;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by filipe on 05-07-2017.
 */

public class SpotAndShareTransferRecordAdapter
    extends RecyclerView.Adapter<SpotAndShareTransferRecordAdapter.TransferViewHolder> {

  private static final int RECEIVED_APP = 0;
  private static final int SENT_APP = 1;

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
    if (viewType == SENT_APP) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.fragment_spotandshare_transfer_record_item_sent, parent, false);
      return new TransferViewHolder(view);
    } else if (viewType == RECEIVED_APP) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.fragment_spotandshare_transfer_record_item_received, parent, false);
      return new TransferViewHolder(view);
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
        .getTransferState() == Transfer.State.SERVING) {
      return SENT_APP;
    } else {
      return RECEIVED_APP;
    }
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

    private ImageView senderAvatar;
    private TextView senderName;
    private TextView appName;
    private TextView appSize;
    private ImageView appIcon;
    private ImageButton acceptButton;
    private Button installButton;
    private ProgressBar transferProgressBar;

    public TransferViewHolder(View itemView) {
      super(itemView);
      appName = (TextView) itemView.findViewById(R.id.transfer_record_app_name);
      appSize = (TextView) itemView.findViewById(R.id.transfer_record_app_size);
      senderAvatar = (ImageView) itemView.findViewById(R.id.transfer_record_header_sender_avatar);
      senderName = (TextView) itemView.findViewById(R.id.transfer_record_header_sender_info);
      appIcon = (ImageView) itemView.findViewById(R.id.transfer_app_icon);
      acceptButton = (ImageButton) itemView.findViewById(R.id.transfer_record_accept_app_button);
      installButton = (Button) itemView.findViewById(R.id.transfer_record_install_app_button);
      transferProgressBar = (ProgressBar) itemView.findViewById(R.id.transfer_record_progress_bar);
    }

    public void setTransferItem(TransferAppModel transferItem) {
      System.out.println("item:"
          + transferItem.getAppName()
          + " transfer state: "
          + transferItem.getTransferState());
      senderAvatar.setImageDrawable(transferItem.getFriend()
          .getAvatar());
      senderName.setText(itemView.getContext()
          .getResources()
          .getString(R.string.spotandshare_message_app_sender_info_sending, transferItem.getFriend()
              .getUsername()));
      appName.setText(transferItem.getAppName());
      appSize.setText(itemView.getContext()
          .getResources()
          .getString(R.string.spotandshare_short_megabytes,
              String.valueOf(transferItem.getApkSize())));
      appIcon.setImageDrawable(transferItem.getAppIcon());
      acceptButton.setOnClickListener(accept -> acceptSubject.onNext(transferItem));
      installButton.setOnClickListener(accept -> installSubject.onNext(transferItem));

      resetState();

      if (transferItem.getTransferState() == Transfer.State.PENDING_ACCEPTION) {
        acceptButton.setVisibility(View.VISIBLE);
      } else if (transferItem.getTransferState() == Transfer.State.RECEIVING) {
        acceptButton.setVisibility(View.GONE);
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
          appSize.setText(R.string.spotandshare_short_download_completed);
        } else {
          installButton.setVisibility(View.GONE);
          appSize.setText(R.string.spotandshare_short_installed);
        }
        transferProgressBar.setVisibility(View.INVISIBLE);
      } else if (transferItem.getTransferState() == Transfer.State.SERVING) {
        acceptButton.setVisibility(View.GONE);
        transferProgressBar.setIndeterminate(true);
      } else {
        acceptButton.setVisibility(View.GONE);
        transferProgressBar.setVisibility(View.INVISIBLE);
        installButton.setVisibility(View.GONE);
      }
    }

    private void resetState() {
      acceptButton.setVisibility(View.GONE);
      transferProgressBar.setVisibility(View.INVISIBLE);
      installButton.setVisibility(View.GONE);
    }
  }
}

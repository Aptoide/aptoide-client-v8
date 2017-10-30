package cm.aptoide.pt.spotandshareapp.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.spotandshareandroid.transfermanager.Transfer;
import cm.aptoide.pt.spotandshareapp.TransferAppModel;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by filipe on 22-09-2017.
 */

public class SpotAndShareTransferRecordCardProvider {

  private PublishSubject<TransferAppModel> acceptSubject;
  private PublishSubject<TransferAppModel> installSubject;
  private TextView appName;
  private TextView appSize;
  private ImageView appIcon;
  private ImageButton acceptButton;
  private Button installButton;
  private ProgressBar transferProgressBar;

  public SpotAndShareTransferRecordCardProvider(PublishSubject<TransferAppModel> acceptSubject,
      PublishSubject<TransferAppModel> installSubject) {
    this.acceptSubject = acceptSubject;
    this.installSubject = installSubject;
  }

  public View getView(LayoutInflater inflater, List<TransferAppModel> appModelList,
      Context context) {

    LinearLayout cardContainer = new LinearLayout(context);
    cardContainer.setOrientation(LinearLayout.VERTICAL);
    for (int i = 0; i < appModelList.size(); i++) {
      cardContainer.addView(getView(inflater, cardContainer, appModelList.get(i)));
    }
    return cardContainer;
  }

  private View getView(LayoutInflater inflater, ViewGroup container,
      TransferAppModel transferAppModel) {

    View card = inflater.inflate(R.layout.spotandshare_transfer_record_item, container, false);
    appName = (TextView) card.findViewById(R.id.transfer_record_app_name);
    appSize = (TextView) card.findViewById(R.id.transfer_record_app_size);
    appIcon = (ImageView) card.findViewById(R.id.transfer_app_icon);
    acceptButton = (ImageButton) card.findViewById(R.id.transfer_record_accept_app_button);
    installButton = (Button) card.findViewById(R.id.transfer_record_install_app_button);
    transferProgressBar = (ProgressBar) card.findViewById(R.id.transfer_record_progress_bar);

    appName.setText(transferAppModel.getAppName());
    appSize.setText(card.getContext()
        .getResources()
        .getString(R.string.spotandshare_short_megabytes,
            String.valueOf(transferAppModel.getApkSize())));
    appIcon.setImageDrawable(transferAppModel.getAppIcon());
    acceptButton.setOnClickListener(accept -> acceptSubject.onNext(transferAppModel));
    installButton.setOnClickListener(accept -> installSubject.onNext(transferAppModel));

    resetState();

    if (transferAppModel.getTransferState() == Transfer.State.PENDING_ACCEPTION) {
      acceptButton.setVisibility(View.VISIBLE);
    } else if (transferAppModel.getTransferState() == Transfer.State.RECEIVING) {
      acceptButton.setVisibility(View.GONE);
      transferProgressBar.setVisibility(View.VISIBLE);
      transferProgressBar.setIndeterminate(true);
    } else if (transferAppModel.getTransferState() == Transfer.State.RECEIVED) {
      acceptButton.setVisibility(View.GONE);
      if (!transferAppModel.isInstalled()) {
        installButton.setVisibility(View.VISIBLE);
        appSize.setText(R.string.spotandshare_short_download_completed);
      } else {
        installButton.setVisibility(View.GONE);
        appSize.setText(R.string.spotandshare_short_installed);
      }
      appSize.setAllCaps(false);
      transferProgressBar.setVisibility(View.INVISIBLE);
    } else if (transferAppModel.getTransferState() == Transfer.State.SERVING) {
      acceptButton.setVisibility(View.GONE);
      transferProgressBar.setIndeterminate(true);
    } else {
      acceptButton.setVisibility(View.GONE);
      transferProgressBar.setVisibility(View.INVISIBLE);
      installButton.setVisibility(View.GONE);
    }

    return card;
  }

  private void resetState() {
    acceptButton.setVisibility(View.GONE);
    transferProgressBar.setVisibility(View.INVISIBLE);
    installButton.setVisibility(View.GONE);
  }
}

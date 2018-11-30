package cm.aptoide.pt.promotions;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import cm.aptoide.pt.R;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;

public class ClaimPromotionDialogFragment extends DialogFragment
    implements ClaimPromotionDialogView {

  private EditText walletAddressEdit;
  private Button getWalletAddressButton;
  private Button continueWalletAddressButton;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    walletAddressEdit = null;
    getWalletAddressButton = null;
    continueWalletAddressButton = null;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.promotions_claim_dialog, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    walletAddressEdit = view.findViewById(R.id.wallet_edit);
    getWalletAddressButton = view.findViewById(R.id.get_wallet_button);
    continueWalletAddressButton = view.findViewById(R.id.continue_button);
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public Observable<Void> getWalletClick() {
    return RxView.clicks(getWalletAddressButton);
  }

  @Override public Observable<Void> continueClick() {
    return RxView.clicks(continueWalletAddressButton);
  }
}

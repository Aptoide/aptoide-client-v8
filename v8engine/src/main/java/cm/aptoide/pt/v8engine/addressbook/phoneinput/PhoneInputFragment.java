package cm.aptoide.pt.v8engine.addressbook.phoneinput;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.SupportV4BaseFragment;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by jdandrade on 14/02/2017.
 */
public class PhoneInputFragment extends SupportV4BaseFragment implements PhoneInputContract.View {

  private PhoneInputContract.UserActionsListener mActionsListener;
  private TextView mNotNowV;
  private TextView mSharePhoneV;

  public static PhoneInputFragment newInstance() {
    PhoneInputFragment phoneInputFragment = new PhoneInputFragment();
    Bundle extras = new Bundle();
    phoneInputFragment.setArguments(extras);
    return phoneInputFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.mActionsListener = new PhoneInputPresenter(this);
  }

  @Override public void setupViews() {
    mNotNowV.setPaintFlags(mNotNowV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    mSharePhoneV.setText(getString(R.string.addressbook_share_phone,
        Application.getConfiguration().getMarketName()));
    RxView.clicks(mNotNowV).subscribe(click -> this.mActionsListener.notNowClicked());
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_phone_input;
  }

  @Override public void bindViews(@Nullable View view) {
    mNotNowV = (TextView) view.findViewById(R.id.addressbook_not_now);
    mSharePhoneV = (TextView) view.findViewById(R.id.addressbook_share_phone_message);
  }

  @Override public void finishView() {
    getActivity().onBackPressed();
  }

  @Override public void showProgressIndicator(boolean active) {
    if (getView() == null) {
      return;
    }

    // TODO: 14/02/2017 manipulate progress
  }

  @Override public void showSubmissionSuccess() {
    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
  }

  @Override public void showSubmissionError() {
    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
  }
}

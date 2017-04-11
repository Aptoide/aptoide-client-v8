package cm.aptoide.pt.v8engine.view.addressbook;

import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.addressbook.AddressBookAnalytics;
import cm.aptoide.pt.v8engine.addressbook.data.ContactsRepositoryImpl;
import cm.aptoide.pt.v8engine.addressbook.utils.ContactUtils;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.presenter.PhoneInputContract;
import cm.aptoide.pt.v8engine.presenter.PhoneInputPresenter;
import cm.aptoide.pt.v8engine.view.fragment.UIComponentFragment;
import com.facebook.appevents.AppEventsLogger;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by jdandrade on 14/02/2017.
 */
public class PhoneInputFragment extends UIComponentFragment implements PhoneInputContract.View {

  public static final String TAG = "TAG";
  private PhoneInputContract.UserActionsListener mActionsListener;
  private TextView mNotNowV;
  private TextView mSharePhoneV;
  private Button mSaveNumber;
  private EditText mCountryNumber;
  private EditText mPhoneNumber;
  private ProgressDialog mGenericPleaseWaitDialog;
  private ContactUtils contactUtils;
  private String entranceTag;

  public static PhoneInputFragment newInstance(String tag) {
    PhoneInputFragment phoneInputFragment = new PhoneInputFragment();
    Bundle extras = new Bundle();
    extras.putString(TAG, tag);
    phoneInputFragment.setArguments(extras);
    return phoneInputFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final BodyInterceptor<BaseBody> baseBodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    this.mActionsListener =
        new PhoneInputPresenter(this, new ContactsRepositoryImpl(baseBodyInterceptor),
            new AddressBookAnalytics(Analytics.getInstance(),
                AppEventsLogger.newLogger(getContext().getApplicationContext())),
            new AddressBookNavigationManager(getFragmentNavigator(), entranceTag,
                getString(R.string.addressbook_about), getString(R.string.addressbook_data_about,
                Application.getConfiguration().getMarketName())));
    mGenericPleaseWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(getContext());
    contactUtils = new ContactUtils();
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    entranceTag = (String) args.get(TAG);
  }

  @Override public void setupViews() {
    mNotNowV.setPaintFlags(mNotNowV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    mSharePhoneV.setText(getString(R.string.addressbook_share_phone,
        Application.getConfiguration().getMarketName()));

    String countryCodeE164 = contactUtils.getCountryCodeForRegion(getContext());
    if (!countryCodeE164.isEmpty()) {
      mCountryNumber.setHint(countryCodeE164);
    }

    mCountryNumber.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable editable) {
        if (editable.length() == 3) {
          mPhoneNumber.requestFocus();
        }
      }
    });

    mPhoneNumber.setOnEditorActionListener((textView, actionId, keyEvent) -> {
      if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId
          == EditorInfo.IME_ACTION_DONE)) {
        String countryCode = mCountryNumber.getText().toString();
        mActionsListener.submitClicked(countryCode.concat(mPhoneNumber.getText().toString()));
      }
      return false;
    });

    RxView.clicks(mNotNowV).subscribe(click -> this.mActionsListener.notNowClicked());
    RxView.clicks(mSaveNumber).subscribe(click -> {

      String countryCode = mCountryNumber.getText().toString();

      if (mCountryNumber.getText().toString().isEmpty()) {
        countryCode = String.valueOf(mCountryNumber.getHint());
      }

      this.mActionsListener.submitClicked(countryCode.concat(mPhoneNumber.getText().toString()));
    });
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_phone_input;
  }

  @Override public void bindViews(@Nullable View view) {
    mNotNowV = (TextView) view.findViewById(R.id.addressbook_not_now);
    mSharePhoneV = (TextView) view.findViewById(R.id.addressbook_share_phone_message);
    mSaveNumber = (Button) view.findViewById(R.id.addressbook_phone_input_save);
    mCountryNumber = (EditText) view.findViewById(R.id.addressbook_country_number);
    mPhoneNumber = (EditText) view.findViewById(R.id.addressbook_phone_number);
  }

  @Override public void finishView() {
    getActivity().onBackPressed();
  }

  @Override public void setGenericPleaseWaitDialog(boolean showProgress) {
    if (showProgress) {
      mGenericPleaseWaitDialog.show();
    } else {
      mGenericPleaseWaitDialog.dismiss();
    }
  }

  @Override public void showSubmissionError() {
    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
  }

  @Override public void hideVirtualKeyboard() {
    AptoideUtils.SystemU.hideKeyboard(getActivity());
  }
}

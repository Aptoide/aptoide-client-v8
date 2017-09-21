package cm.aptoide.pt.spotandshareapp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.ParcelableSpan;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.spotandshareapp.ShareApkSandbox;
import cm.aptoide.pt.spotandshareapp.presenter.ShareAptoidePresenter;
import cm.aptoide.pt.view.BackButtonFragment;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

/**
 * Created by filipe on 12-09-2017.
 */

public class ShareAptoideFragment extends BackButtonFragment implements ShareAptoideView {

  private static final String SHARE_APTOIDE_LINK = "http://192.168.43.1:38080";
  private Toolbar toolbar;
  private LinearLayout shareAptoideLinearLayout;
  private TextView shareAptoideFirstInstruction;
  private TextView shareAptoideLink;

  private PublishRelay<Void> backRelay;
  private RxAlertDialog backDialog;
  private ClickHandler clickHandler;

  public static Fragment newInstance() {
    Fragment fragment = new ShareAptoideFragment();
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    backRelay = PublishRelay.create();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_spotandshare_share_aptoide, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    toolbar = (Toolbar) view.findViewById(R.id.spotandshare_toolbar);
    setupToolbar();
    shareAptoideLinearLayout = (LinearLayout) view.findViewById(R.id.share_aptoide_layout);
    shareAptoideFirstInstruction =
        (TextView) view.findViewById(R.id.share_aptoide_first_instruction);
    shareAptoideLink = (TextView) view.findViewById(R.id.share_aptoide_link);
    setupShareTextViews();
    setupBackClick();

    attachPresenter(new ShareAptoidePresenter(this,
        ((AptoideApplication) getActivity().getApplicationContext()).getSpotAndShare(),
        new ShareApkSandbox(getActivity().getApplicationContext(), getActivity().getAssets()),
        new PermissionManager(), (PermissionService) getContext()), savedInstanceState);
  }

  private void setupBackClick() {
    clickHandler = () -> {
      backRelay.call(null);
      return true;
    };
    registerClickHandler(clickHandler);
    backDialog = new RxAlertDialog.Builder(getContext()).setMessage(
        R.string.spotandshare_message_leave_group_warning)
        .setPositiveButton(R.string.spotandshare_button_leave_group)
        .setNegativeButton(R.string.spotandshare_button_cancel_leave_group)
        .build();
  }

  private void setupShareTextViews() {//// TODO: 12-09-2017 filipe create the ssid
    Spannable spannable = createColorSpan(
        getResources().getString(R.string.spotandshare_message_first_share_instruction,
            "DummyHotspot"), getResources().getColor(R.color.orange_700), "DummyHotspot");
    shareAptoideFirstInstruction.setText(spannable);

    spannable = createColorSpan(
        getResources().getString(R.string.spotandshare_message_second_share_instruction_alternative,
            SHARE_APTOIDE_LINK), getResources().getColor(R.color.orange_700), SHARE_APTOIDE_LINK);
    shareAptoideLink.setText(spannable);
  }

  private Spannable createColorSpan(String text, int color, String... spanTexts) {
    return createSpan(text, new ForegroundColorSpan(color), spanTexts);
  }

  //// FIXME: 23-05-2017 : Use SpannableFactory class after spotandshare module removal
  private Spannable createSpan(String text, ParcelableSpan span, String[] spanTexts) {
    final Spannable result = new SpannableString(text);
    for (String spanText : spanTexts) {
      int spanTextStart = text.indexOf(spanText);
      if (spanTextStart >= 0
          && spanTextStart < text.length()
          && spanText.length() <= text.length()) {
        result.setSpan(span, spanTextStart, (spanTextStart + spanText.length()),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      }
    }
    return result;
  }

  private void setupToolbar() {
    setHasOptionsMenu(true);
    toolbar.setTitle(R.string.spotandshare_title_toolbar);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
  }

  @Override public void onDestroyView() {
    toolbar = null;
    unregisterClickHandler(clickHandler);
    clickHandler = null;
    backDialog = null;

    shareAptoideLinearLayout = null;
    shareAptoideFirstInstruction = null;
    shareAptoideLink = null;

    super.onDestroyView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      backRelay.call(null);
    }
    return false;
  }

  @Override public Observable<Void> backButtonEvent() {
    return backRelay;
  }

  @Override public void showExitWarning() {
    backDialog.show();
  }

  @Override public Observable<Void> exitEvent() {
    return backDialog.positiveClicks()
        .map(dialogInterface -> null);
  }

  @Override public void navigateBack() {
    getFragmentNavigator().cleanBackStack();
    getFragmentNavigator().navigateToWithoutBackSave(SpotAndShareMainFragment.newInstance(), true);
  }

  @Override public void onLeaveGroupError() {
    Toast.makeText(getContext(), "There was an error while trying to leave the group",
        Toast.LENGTH_SHORT)
        .show();
  }

  @Override public void onCreateGroupError(Throwable throwable) {
    Toast.makeText(getContext(), R.string.spotandshare_message_error_create_group,
        Toast.LENGTH_SHORT)
        .show();
  }
}

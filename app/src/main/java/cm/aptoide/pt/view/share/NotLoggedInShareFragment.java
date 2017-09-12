package cm.aptoide.pt.view.share;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.view.account.SocialLoginFragment;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

/**
 * Created by pedroribeiro on 29/08/17.
 */

public class NotLoggedInShareFragment extends SocialLoginFragment implements NotLoggedInShareView {

  private static final String APP_NAME = "app_name";
  private static final String APP_ICON = "app_title";
  private static final String APP_RATING = "app_rating";
  private Presenter presenter;
  private Button facebookButton;
  private Button googleButton;
  private RatingBar appRating;
  private TextView appTitle;
  private ImageView appIcon;
  private TextView closeText;
  private TextView dontShowAgain;

  public static NotLoggedInShareFragment newInstance(GetAppMeta.App app) {
    NotLoggedInShareFragment fragment = new NotLoggedInShareFragment();
    Bundle bundle = new Bundle();
    bundle.putString(APP_NAME, app.getName());
    bundle.putString(APP_ICON, app.getIcon());
    bundle.putFloat(APP_RATING, app.getStats()
        .getRating()
        .getAvg());
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    presenter = new NotLoggedInSharePresenter(this,
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
        CrashReport.getInstance());
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    bindViews(view);
    attachPresenter(presenter, null);
  }

  @Override public Analytics.Account.StartupClickOrigin getStartupClickOrigin() {
    return Analytics.Account.StartupClickOrigin.NOT_LOGGED_IN_DIALOG;
  }

  @Override protected Button getGoogleButton() {
    return googleButton;
  }

  @Override protected Button getFacebookButton() {
    return facebookButton;
  }

  @Override protected void bindViews(View view) {
    facebookButton = (Button) view.findViewById(R.id.not_logged_in_share_facebook_button);
    googleButton = (Button) view.findViewById(R.id.not_logged_in_share_google_button);
    appIcon = (ImageView) view.findViewById(R.id.not_logged_in_app_icon);
    appTitle = (TextView) view.findViewById(R.id.not_logged_int_app_title);
    closeText = (TextView) view.findViewById(R.id.not_logged_in_close);
    dontShowAgain = (TextView) view.findViewById(R.id.not_logged_in_dont_show_again);
    appRating = (RatingBar) view.findViewById(R.id.not_logged_in_app_rating);
    facebookLoginSubject = PublishRelay.create();

    appTitle.setText(getArguments().getString(APP_NAME));
    appRating.setRating(getArguments().getFloat(APP_RATING));
    ImageLoader.with(getContext())
        .load(getArguments().getString(APP_ICON), appIcon);
    super.bindViews(view);
  }

  @Override protected void showGoogleLoginError() {
    Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.google_login_cancelled,
        Snackbar.LENGTH_LONG)
        .show();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.not_logged_in_share, null, false);
  }

  @Override public void initializeFacebookCallback() {
    registerFacebookCallback();
  }

  @Override public Observable<Void> closeClick() {
    return RxView.clicks(closeText);
  }

  @Override public void closeFragment() {
    finishWithResult(RESULT_CANCELED);
  }

  @Override public Observable<Void> dontShowAgainClick() {
    return RxView.clicks(dontShowAgain);
  }

  @Override public void navigateToMainView() {
    finishWithResult(RESULT_OK);
  }
}
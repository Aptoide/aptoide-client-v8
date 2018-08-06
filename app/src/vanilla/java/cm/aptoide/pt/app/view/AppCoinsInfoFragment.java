package cm.aptoide.pt.app.view;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.AppCoinsInfoPresenter;
import cm.aptoide.pt.view.BackButtonFragment;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by D01 on 30/07/2018.
 */

public class AppCoinsInfoFragment extends BackButtonFragment implements AppCoinsInfoView {

  public static final String APPCWALLETPACKAGENAME = "com.appcoins.wallet";
  @Inject AppCoinsInfoPresenter appCoinsInfoPresenter;
  private Toolbar toolbar;
  private PublishSubject<Void> coinbaseClickSubject;
  private View bdsCardView;
  private TextView appcMessageAppcoinsSection2a;
  private Button installButton;
  private TextView appcMessageAppcoinsSection2b;
  private ClickableSpan coinbaseClickListener;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    coinbaseClickSubject = PublishSubject.create();
    coinbaseClickListener = new ClickableSpan() {
      @Override public void onClick(View view) {
        if (coinbaseClickSubject != null) {
          coinbaseClickSubject.onNext(null);
        }
      }
    };
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    bdsCardView = view.findViewById(R.id.product_bdsWallet_cardview);
    installButton = (Button) view.findViewById(R.id.appview_install_button);
    appcMessageAppcoinsSection2a =
        (TextView) view.findViewById(R.id.appc_message_appcoins_section_2a);
    appcMessageAppcoinsSection2b =
        (TextView) view.findViewById(R.id.appc_message_appcoins_section_2b);

    TextView appcMessageAppcoinsSection3 =
        (TextView) view.findViewById(R.id.appc_message_appcoins_section_3);
    TextView appcMessageAppcoinsSection4 =
        (TextView) view.findViewById(R.id.appc_message_appcoins_section_4);

    setupTextView(getString(R.string.appc_short_get_appc),
        getString(R.string.appc_message_appcoins_section_3), appcMessageAppcoinsSection3);
    setupTextView(getString(R.string.appc_short_spend_appc),
        getString(R.string.appc_message_appcoins_section_3), appcMessageAppcoinsSection4);

    ((TextView) bdsCardView.findViewById(R.id.product_title_textview)).setText(
        getString(R.string.appc_title_settings_appcoins_wallet));
    ((ImageView) bdsCardView.findViewById(R.id.product_icon_imageview)).setImageDrawable(
        ContextCompat.getDrawable(getContext(), R.drawable.appcoins_wallet_icon));

    setupCoinbaseLink();
    setupWalletLink();
    setHasOptionsMenu(true);
    setupToolbar();
    attachPresenter(appCoinsInfoPresenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onDestroy() {
    coinbaseClickSubject = null;
    coinbaseClickListener = null;
    super.onDestroy();
  }

  @Override public void onDestroyView() {
    toolbar = null;
    bdsCardView = null;
    installButton = null;
    appcMessageAppcoinsSection2a = null;
    appcMessageAppcoinsSection2b = null;
    super.onDestroyView();
  }

  private void setupWalletLink() {
    final String formattedString =
        String.format(getString(R.string.appc_message_appcoins_section_2a),
            getString(R.string.appc_title_settings_appcoins_wallet));
    SpannableString spannableString = new SpannableString(formattedString);
    spannableString.setSpan(new UnderlineSpan(), 0, formattedString.length(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    appcMessageAppcoinsSection2a.setText(spannableString);
    appcMessageAppcoinsSection2a.setMovementMethod(LinkMovementMethod.getInstance());
  }

  private void setupCoinbaseLink() {
    final String coinbase = getString(R.string.coinbase);
    final String section2b =
        String.format(getString(R.string.appc_message_appcoins_section_2b), coinbase);

    SpannableString spannableString = new SpannableString(section2b);
    spannableString.setSpan(coinbaseClickListener, section2b.indexOf(coinbase),
        section2b.indexOf(coinbase) + coinbase.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange)),
        section2b.indexOf(coinbase), section2b.indexOf(coinbase) + coinbase.length(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    appcMessageAppcoinsSection2b.setText(spannableString);
    appcMessageAppcoinsSection2b.setMovementMethod(LinkMovementMethod.getInstance());
  }

  private void setupTextView(String appcString, String text, TextView appcMessageAppcoinsSection) {
    final String spendGetAppcoinsLogo =
        String.format("<img src=\"%1$s\"/> <font color=\"%2$s\"><small>%3$s</small></font>",
            R.drawable.spend_get_appc_icon, getResources().getColor(R.color.orange), appcString);
    final String formatedText = String.format(text, spendGetAppcoinsLogo);
    appcMessageAppcoinsSection.setText(Html.fromHtml(formatedText, getImageGetter(), null));
  }

  private void setupToolbar() {
    toolbar.setTitle(R.string.appc_title_about_appcoins);

    final AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(toolbar.getTitle());
    }
  }

  @NonNull private Html.ImageGetter getImageGetter() {
    return source -> {
      Drawable drawable = null;
      try {
        drawable = getResources().getDrawable(Integer.parseInt(source));
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
      } catch (Resources.NotFoundException e) {
        Log.e("log_tag", "Image not found. Check the ID.", e);
      } catch (NumberFormatException e) {
        Log.e("log_tag", "Source string not a valid resource ID.", e);
      }
      return drawable;
    };
  }

  @Override public Observable<Void> coinbaseLinkClick() {
    return coinbaseClickSubject;
  }

  @Override public Observable<Void> cardViewClick() {
    return RxView.clicks(bdsCardView);
  }

  @Override public Observable<Void> installButtonClick() {
    return RxView.clicks(installButton);
  }

  @Override public Observable<Void> appCoinsWalletClick() {
    return RxView.clicks(appcMessageAppcoinsSection2a);
  }

  @Override public void openApp(String packageName) {
    AptoideUtils.SystemU.openApp(packageName, getContext().getPackageManager(), getContext());
  }

  @Override public void setButtonText(boolean isInstalled) {
    String installState = getResources().getString(R.string.appview_button_install);
    if (isInstalled) {
      installButton.setText(getResources().getString(R.string.appview_button_open));
    } else {
      installButton.setText(installState);
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_appcoints_info, container, false);
  }
}

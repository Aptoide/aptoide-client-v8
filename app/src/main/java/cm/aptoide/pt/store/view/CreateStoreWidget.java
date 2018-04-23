package cm.aptoide.pt.store.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.text.ParcelableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.store.ManageStoreFragment;
import cm.aptoide.pt.account.view.store.ManageStoreViewModel;
import cm.aptoide.pt.account.view.user.CreateStoreDisplayable;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.view.recycler.widget.Widget;
import cm.aptoide.pt.view.spannable.SpannableFactory;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by trinkes on 02/12/2016.
 */

public class CreateStoreWidget extends Widget<CreateStoreDisplayable> {

  private final CrashReport crashReport;
  private Button button;
  private TextView followers;
  private TextView following;

  public CreateStoreWidget(View itemView) {
    super(itemView);
    crashReport = CrashReport.getInstance();
  }

  @Override protected void assignViews(View itemView) {
    button = (Button) itemView.findViewById(R.id.create_store_action);
    followers = (TextView) itemView.findViewById(R.id.followers);
    following = (TextView) itemView.findViewById(R.id.following);
  }

  @Override public void bindView(CreateStoreDisplayable displayable) {
    SpannableFactory spannableFactory = new SpannableFactory();
    String followersText = String.format(getContext().getString(R.string.storetab_short_followers),
        String.valueOf(displayable.getFollowers()));

    ParcelableSpan[] textStyle = {
        new StyleSpan(android.graphics.Typeface.BOLD), new ForegroundColorSpan(getTextColor())
    };
    followers.setText(spannableFactory.createMultiSpan(followersText, textStyle,
        String.valueOf(displayable.getFollowers())));

    String followingText = String.format(getContext().getString(R.string.storetab_short_followings),
        String.valueOf(displayable.getFollowings()));
    following.setText(spannableFactory.createMultiSpan(followingText, textStyle,
        String.valueOf(displayable.getFollowings())));

    compositeSubscription.add(RxView.clicks(button)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(click -> getFragmentNavigator().navigateTo(
            ManageStoreFragment.newInstance(new ManageStoreViewModel(), false), true))
        .doOnNext(__ -> displayable.getStoreAnalytics()
            .sendStoreTabInteractEvent("Login", false))
        .subscribe(__ -> {
        }, err -> crashReport.log(err)));
  }

  private @ColorInt int getTextColor() {
    Context context = getContext();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return context.getResources()
          .getColor(R.color.default_color, context.getTheme());
    } else {
      return context.getResources()
          .getColor(R.color.default_color);
    }
  }
}

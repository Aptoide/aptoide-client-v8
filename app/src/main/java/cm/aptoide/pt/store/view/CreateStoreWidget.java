package cm.aptoide.pt.store.view;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.store.ManageStoreFragment;
import cm.aptoide.pt.account.view.store.ManageStoreViewModel;
import cm.aptoide.pt.account.view.user.CreateStoreDisplayable;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by trinkes on 02/12/2016.
 */

public class CreateStoreWidget extends Widget<CreateStoreDisplayable> {

  private final CrashReport crashReport;
  private Button button;
  private TextView followers;
  private TextView followings;

  public CreateStoreWidget(View itemView) {
    super(itemView);
    crashReport = CrashReport.getInstance();
  }

  @Override protected void assignViews(View itemView) {
    button = (Button) itemView.findViewById(R.id.create_store_action);
    followers = (TextView) itemView.findViewById(R.id.followers);
    followings = (TextView) itemView.findViewById(R.id.following);
  }

  @Override public void bindView(CreateStoreDisplayable displayable) {
    followers.setText(String.format(itemView.getContext()
        .getString(R.string.storetab_short_followers), String.valueOf(displayable.getFollowers())));

    followings.setText(String.format(itemView.getContext()
            .getString(R.string.storetab_short_followings),
        String.valueOf(displayable.getFollowings())));

    compositeSubscription.add(RxView.clicks(button)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(click -> getFragmentNavigator().navigateTo(
            ManageStoreFragment.newInstance(new ManageStoreViewModel(), false), true))
        .doOnNext(__ -> displayable.getStoreAnalytics()
            .sendStoreTabInteractEvent("Login"))
        .subscribe(__ -> {
        }, err -> crashReport.log(err)));
  }
}

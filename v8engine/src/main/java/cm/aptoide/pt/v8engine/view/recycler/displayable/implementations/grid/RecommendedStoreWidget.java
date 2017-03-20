package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 06/12/2016.
 */

public class RecommendedStoreWidget extends Widget<RecommendedStoreDisplayable> {

  private TextView storeName;
  private TextView followingUsers;
  private TextView numberStoreApps;
  private ImageView storeIcon;
  private AppCompatButton followButton;

  public RecommendedStoreWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    storeName = (TextView) itemView.findViewById(R.id.recommended_store_name);
    followingUsers = (TextView) itemView.findViewById(R.id.recommended_store_users);
    numberStoreApps = (TextView) itemView.findViewById(R.id.recommended_store_apps);
    storeIcon = (ImageView) itemView.findViewById(R.id.store_avatar_row);
    followButton = (AppCompatButton) itemView.findViewById(R.id.recommended_store_action);
  }

  @Override public void bindView(RecommendedStoreDisplayable displayable) {
    Store store = displayable.getPojo();
    storeName.setText(store.getName());
    followingUsers.setText(String.valueOf(store.getStats().getSubscribers()));
    numberStoreApps.setText(String.valueOf(store.getStats().getApps()));
    final FragmentActivity context = getContext();
    ImageLoader.with(context)
        .loadWithShadowCircleTransform(store.getAvatar(), storeIcon,
            StoreThemeEnum.get(store).getStoreHeaderInt());
    setFollowButtonListener(displayable);
    setButtonText(displayable);
    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(click -> displayable.openStoreFragment(getNavigationManager()),
            throwable -> CrashReport.getInstance().log(throwable)));
  }

  private void setFollowButtonListener(RecommendedStoreDisplayable displayable) {
    compositeSubscription.add(RxView.clicks(followButton).flatMap(click -> {
      followButton.setEnabled(false);
      return displayable.isFollowing()
          .first()
          .observeOn(Schedulers.computation())
          .map(isSubscribed -> {
            if (isSubscribed) {
              displayable.unsubscribeStore();
            } else {
              displayable.subscribeStore(getContext());
            }
            return !isSubscribed;
          });
    }).observeOn(AndroidSchedulers.mainThread()).subscribe(isSubscribing -> {
      followButton.setEnabled(true);
      int message;
      if (isSubscribing) {
        message = R.string.store_followed;
      } else {
        message = R.string.unfollowing_store_message;
      }
      ShowMessage.asSnack(itemView,
          AptoideUtils.StringU.getFormattedString(message, displayable.getPojo().getName()));
    }, throwable -> {
      CrashReport.getInstance().log(throwable);
      ShowMessage.asSnack(itemView, R.string.error_occured);
    }));
  }

  private void setButtonText(RecommendedStoreDisplayable displayable) {
    followButton.setVisibility(View.GONE);
    compositeSubscription.add(displayable.isFollowing()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(isSubscribed -> {
          int message;
          if (isSubscribed) {
            message = R.string.followed;
          } else {
            message = R.string.follow;
          }
          followButton.setText(
              AptoideUtils.StringU.getFormattedString(message, displayable.getPojo().getName()));
          followButton.setVisibility(View.VISIBLE);
        }));
  }
}

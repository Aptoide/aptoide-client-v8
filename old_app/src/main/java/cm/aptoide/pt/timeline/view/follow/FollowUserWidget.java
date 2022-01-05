package cm.aptoide.pt.timeline.view.follow;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.timeline.view.displayable.FollowUserDisplayable;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by trinkes on 16/12/2016.
 */

public class FollowUserWidget extends Widget<FollowUserDisplayable> {

  private TextView userNameTv;
  private TextView storeNameTv;
  private TextView followingNumber;
  private TextView followersNumber;
  private ImageView mainIcon;
  private ImageView secondaryIcon;
  private LinearLayout followNumbers;
  private View separatorView;

  public FollowUserWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    userNameTv = itemView.findViewById(R.id.user_name);
    storeNameTv = itemView.findViewById(R.id.store_name);
    followingNumber = itemView.findViewById(R.id.following_number);
    followersNumber = itemView.findViewById(R.id.followers_number);
    mainIcon = itemView.findViewById(R.id.main_icon);
    secondaryIcon = itemView.findViewById(R.id.secondary_icon);
    followNumbers = itemView.findViewById(R.id.followers_following_numbers);
    separatorView = itemView.findViewById(R.id.separator_vertical);
  }

  @Override public void bindView(FollowUserDisplayable displayable, int position) {
    followNumbers.setVisibility(View.VISIBLE);
    separatorView.setVisibility(View.VISIBLE);
    followingNumber.setText(displayable.getFollowing());
    followersNumber.setText(displayable.getFollowers());

    final FragmentActivity context = getContext();
    if (displayable.hasStoreAndUser()) {
      ImageLoader.with(context)
          .loadUsingCircleTransform(displayable.getStoreAvatar(), mainIcon);
      ImageLoader.with(context)
          .loadUsingCircleTransform(displayable.getUserAvatar(), secondaryIcon);
      mainIcon.setVisibility(View.VISIBLE);
      secondaryIcon.setVisibility(View.VISIBLE);
    } else if (displayable.hasUser()) {
      ImageLoader.with(context)
          .loadUsingCircleTransform(displayable.getUserAvatar(), mainIcon);
      secondaryIcon.setVisibility(View.GONE);
    } else if (displayable.hasStore()) {
      ImageLoader.with(context)
          .loadUsingCircleTransform(displayable.getStoreAvatar(), mainIcon);
      secondaryIcon.setVisibility(View.GONE);
    } else {
      mainIcon.setVisibility(View.GONE);
      secondaryIcon.setVisibility(View.GONE);
    }

    if (displayable.hasUser()) {
      this.userNameTv.setText(displayable.getUserName());
      userNameTv.setVisibility(View.VISIBLE);
    } else {
      userNameTv.setVisibility(View.GONE);
    }

    if (displayable.hasStore()) {
      setupStoreNameTv(displayable.storeName());
    } else {
      storeNameTv.setVisibility(View.GONE);
    }

    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(click -> displayable.viewClicked(getFragmentNavigator()),
            err -> CrashReport.getInstance()
                .log(err)));
  }

  private void setupStoreNameTv(String storeName) {
    storeNameTv.setText(storeName);
    storeNameTv.setVisibility(View.VISIBLE);
    Drawable drawable;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      drawable = getContext().getResources()
          .getDrawable(R.drawable.ic_store, null);
    } else {
      drawable = getContext().getResources()
          .getDrawable(R.drawable.ic_store);
    }
    drawable.setBounds(0, 0, 30, 30);
    drawable.mutate();

    storeNameTv.setCompoundDrawablePadding(5);
    storeNameTv.setCompoundDrawables(drawable, null, null, null);
  }
}

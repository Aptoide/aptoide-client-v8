package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FollowUserDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
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
  private TextView followingTv;
  private TextView followedTv;

  public FollowUserWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    userNameTv = (TextView) itemView.findViewById(R.id.user_name);
    storeNameTv = (TextView) itemView.findViewById(R.id.store_name);
    followingNumber = (TextView) itemView.findViewById(R.id.following_number);
    followersNumber = (TextView) itemView.findViewById(R.id.followers_number);
    followingTv = (TextView) itemView.findViewById(R.id.following_tv);
    followedTv = (TextView) itemView.findViewById(R.id.followers_tv);
    mainIcon = (ImageView) itemView.findViewById(R.id.main_icon);
    secondaryIcon = (ImageView) itemView.findViewById(R.id.secondary_icon);
  }

  @Override public void bindView(FollowUserDisplayable displayable) {
    followingNumber.setText(displayable.getFollowing());
    followersNumber.setText(displayable.getFollowers());

    if (displayable.hasStoreAndUser()) {
      ImageLoader.loadWithCircleTransform(displayable.getStoreAvatar(), mainIcon);
      ImageLoader.loadWithCircleTransform(displayable.getUserAvatar(), secondaryIcon);
      mainIcon.setVisibility(View.VISIBLE);
      secondaryIcon.setVisibility(View.VISIBLE);
    } else if (displayable.hasUser()) {
      ImageLoader.loadWithCircleTransform(displayable.getUserAvatar(), mainIcon);
      secondaryIcon.setVisibility(View.GONE);
    } else if (displayable.hasStore()) {
      ImageLoader.loadWithCircleTransform(displayable.getStoreAvatar(), mainIcon);
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
      setupStoreNameTv(displayable.getStoreColor(), displayable.storeName());
    } else {
      storeNameTv.setVisibility(View.GONE);
    }
    followedTv.setTextColor(displayable.getStoreColor());
    followingTv.setTextColor(displayable.getStoreColor());

    if (displayable.hasStore()) {
      compositeSubscription.add(RxView.clicks(itemView)
          .subscribe(click -> displayable.viewClicked(((FragmentShower) getContext()))));
    }
  }

  private void setupStoreNameTv(int storeColor, String storeName) {
    storeNameTv.setText(storeName);
    storeNameTv.setTextColor(storeColor);
    storeNameTv.setVisibility(View.VISIBLE);
    Drawable drawable;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      drawable = getContext().getResources().getDrawable(R.drawable.ic_store, null);
    } else {
      drawable = getContext().getResources().getDrawable(R.drawable.ic_store);
    }
    drawable.setBounds(0, 0, 30, 30);
    drawable.mutate();

    drawable.setColorFilter(storeColor, PorterDuff.Mode.SRC_IN);
    storeNameTv.setCompoundDrawablePadding(5);
    storeNameTv.setCompoundDrawables(drawable, null, null, null);
  }
}

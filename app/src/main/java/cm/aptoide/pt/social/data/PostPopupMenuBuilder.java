package cm.aptoide.pt.social.data;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import cm.aptoide.pt.R;

/**
 * Created by jdandrade on 11/10/2017.
 */

public class PostPopupMenuBuilder {

  private PopupMenu popup;

  public PostPopupMenuBuilder prepMenu(Context context, View view) {
    popup = new PopupMenu(context, view);
    MenuInflater inflater = popup.getMenuInflater();
    inflater.inflate(R.menu.menu_post_overflow, popup.getMenu());
    return this;
  }

  public PostPopupMenuBuilder addItemDelete(
      MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
    MenuItem menuItemDelete = popup.getMenu()
        .findItem(R.id.delete);
    menuItemDelete.setOnMenuItemClickListener(onMenuItemClickListener);
    menuItemDelete.setVisible(true);
    return this;
  }

  public PostPopupMenuBuilder addReportAbuse(
      MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
    MenuItem menuReportAbuse = popup.getMenu()
        .findItem(R.id.report);
    menuReportAbuse.setOnMenuItemClickListener(onMenuItemClickListener);
    menuReportAbuse.setVisible(true);
    return this;
  }

  public PostPopupMenuBuilder addUnfollowStore(
      MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
    MenuItem menuItemUnfollow = popup.getMenu()
        .findItem(R.id.unfollow_store);
    menuItemUnfollow.setOnMenuItemClickListener(onMenuItemClickListener);
    menuItemUnfollow.setVisible(true);
    return this;
  }

  public PostPopupMenuBuilder addUnfollowUser(
      MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
    MenuItem menuItemUnfollow = popup.getMenu()
        .findItem(R.id.unfollow_user);
    menuItemUnfollow.setOnMenuItemClickListener(onMenuItemClickListener);
    menuItemUnfollow.setVisible(true);
    return this;
  }

  public PostPopupMenuBuilder addIgnoreUpdate(
      MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
    MenuItem menuIgnoreUpdate = popup.getMenu()
        .findItem(R.id.ignore_update);
    menuIgnoreUpdate.setOnMenuItemClickListener(onMenuItemClickListener);
    menuIgnoreUpdate.setVisible(true);
    return this;
  }

  public PopupMenu getPopupMenu() {
    return popup;
  }
}

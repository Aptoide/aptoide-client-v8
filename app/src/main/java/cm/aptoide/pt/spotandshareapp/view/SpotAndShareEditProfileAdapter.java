package cm.aptoide.pt.spotandshareapp.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUserAvatar;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by filipe on 07-08-2017.
 */

public class SpotAndShareEditProfileAdapter extends RecyclerView.Adapter {

  private List<SpotAndShareAvatar> avatarList;
  private Context context;
  private PublishSubject<SpotAndShareAvatar> pickAvatarSubject;
  private SpotAndShareAvatar selectedAvatar;

  public SpotAndShareEditProfileAdapter(Context context,
      PublishSubject<SpotAndShareAvatar> pickAvatarSubject) {
    this.context = context;
    this.pickAvatarSubject = pickAvatarSubject;
  }

  public void setAvatarList(List<SpotAndShareAvatar> avatarList) {
    this.avatarList = avatarList;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_spotandshare_edit_profile_item, parent, false);
    return new AvatarViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    AvatarViewHolder avatarViewHolder = (AvatarViewHolder) holder;
    avatarViewHolder.setAvatar(avatarList.get(position));
  }

  @Override public int getItemCount() {
    if (avatarList != null && !avatarList.isEmpty()) {
      return avatarList.size();
    }
    return 0;
  }

  public void removeAll() {
    if (avatarList != null) {
      avatarList.clear();
      avatarList = null;
    }
    pickAvatarSubject = null;
    context = null;
  }

  public Observable<SpotAndShareAvatar> onSelectedAvatar() {
    return pickAvatarSubject;
  }

  public SpotAndShareUserAvatar getSelectedAvatar() {
    if (selectedAvatar == null) {
      return (SpotAndShareUserAvatar) avatarList.get(0);
    }
    return (SpotAndShareUserAvatar) selectedAvatar;
  }

  /**
   * Rebuild the avatar with select flag. Using the avatar ID as index in the avatar list.
   *
   * @param selectAvatar - avatar that was selected
   */
  public void selectAvatar(SpotAndShareAvatar selectAvatar) {
    if (selectedAvatar != null) {
      unselectAvatar(selectedAvatar);
    }
    selectAvatar =
        new SpotAndShareUserAvatar(selectAvatar.getAvatarId(), selectAvatar.getString(), true);
    avatarList.set(selectAvatar.getAvatarId(), selectAvatar);
    selectedAvatar = selectAvatar;

    notifyDataSetChanged();
  }

  private void unselectAvatar(SpotAndShareAvatar avatar) {
    avatarList.set(avatar.getAvatarId(),
        new SpotAndShareUserAvatar(avatar.getAvatarId(), avatar.getString(), false));
  }

  class AvatarViewHolder extends ViewHolder {

    private ImageView avatarImageView;

    public AvatarViewHolder(View itemView) {
      super(itemView);
      avatarImageView = (ImageView) itemView.findViewById(R.id.avatar_image_view);
    }

    public void setAvatar(SpotAndShareAvatar avatar) {
      avatarImageView.setOnClickListener(v -> pickAvatarSubject.onNext(avatar));
      ImageLoader.with(context)
          .load(avatar.getString(), avatarImageView);
      System.out.println(
          "Drawing avatar : " + avatar.getAvatarId() + " it is selected " + avatar.isSelected());
      avatarImageView.setSelected(avatar.isSelected());
    }
  }
}

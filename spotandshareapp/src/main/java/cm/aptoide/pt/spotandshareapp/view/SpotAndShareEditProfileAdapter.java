package cm.aptoide.pt.spotandshareapp.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cm.aptoide.pt.spotandshareapp.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
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

  public SpotAndShareEditProfileAdapter(Context context,
      PublishSubject<SpotAndShareAvatar> pickAvatarSubject) {
    this.context = context;
    this.pickAvatarSubject = pickAvatarSubject;
  }

  public void setAvatarList(List<SpotAndShareAvatar> avatarList) {
    this.avatarList = avatarList;
    notifyDataSetChanged();
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_spotandshare_pick_apps_item, parent, false);
    return new AvatarViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    AvatarViewHolder avatarViewHolder = (AvatarViewHolder) holder;
    avatarViewHolder.setAppModelItem(avatarList.get(position - 1));
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

  class AvatarViewHolder extends ViewHolder {

    private ImageView avatarImageView;

    public AvatarViewHolder(View itemView) {
      super(itemView);
      avatarImageView = (ImageView) itemView.findViewById(R.id.avatar_image_view);
    }

    public void setAppModelItem(SpotAndShareAvatar avatar) {
      ImageLoader.with(context)
          .load(avatar.getString(), avatarImageView);
      avatarImageView.setOnClickListener(v -> pickAvatarSubject.onNext(avatar));
    }
  }
}

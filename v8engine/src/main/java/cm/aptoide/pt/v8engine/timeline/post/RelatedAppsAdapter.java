package cm.aptoide.pt.v8engine.timeline.post;

import android.animation.ObjectAnimator;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Observable;

class RelatedAppsAdapter extends RecyclerView.Adapter {
  private final ArrayList<PostRemoteAccessor.RelatedApp> relatedAppList;
  private final PublishRelay<PostRemoteAccessor.RelatedApp> relatedAppPublisher;

  public RelatedAppsAdapter() {
    relatedAppList = new ArrayList<>();
    relatedAppPublisher = PublishRelay.create();
  }

  public Observable<PostRemoteAccessor.RelatedApp> getClickedView() {
    return relatedAppPublisher;
  }

  public Completable setRelatedApps(List<PostRemoteAccessor.RelatedApp> relatedApps) {
    return Completable.fromAction(() -> {
      PostRemoteAccessor.RelatedApp selected = null;
      for (PostRemoteAccessor.RelatedApp relatedApp : relatedAppList) {
        if (relatedApp.isSelected()) {
          selected = relatedApp;
          break;
        }
      }

      if (selected != null) {
        for (PostRemoteAccessor.RelatedApp newRelatedApp : relatedApps) {
          if (newRelatedApp.equals(selected)) {
            newRelatedApp.setSelected(true);
            break;
          }
        }
      }

      relatedAppList.clear();
      relatedAppList.addAll(relatedApps);
      notifyDataSetChanged();
    });
  }

  public Completable setRelatedAppSelected(PostRemoteAccessor.RelatedApp relatedApp) {
    return Completable.fromAction(() -> {
      for (PostRemoteAccessor.RelatedApp app : relatedAppList) {
        app.setSelected(app.equals(relatedApp));
      }
      notifyDataSetChanged();
    });
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_view_post_related_app, parent, false);
    return new RelatedAppViewHolder(itemView, relatedAppPublisher);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    ((RelatedAppViewHolder) holder).bind(relatedAppList.get(position));
  }

  @Override public int getItemCount() {
    return relatedAppList.size();
  }

  public PostRemoteAccessor.RelatedApp getCurrentSelected() {
    for (PostRemoteAccessor.RelatedApp relatedApp : relatedAppList) {
      if (relatedApp.isSelected()) return relatedApp;
    }
    return null;
  }

  private static class RelatedAppViewHolder extends RecyclerView.ViewHolder {
    public static final int SELECTED_ELEVATION = 20;
    public static final int UNSELECTED_ELEVATION = 0;
    private final ImageView image;
    private final TextView name;
    private final ImageView checkIndicator;
    private PostRemoteAccessor.RelatedApp app;

    RelatedAppViewHolder(View itemView,
        PublishRelay<PostRemoteAccessor.RelatedApp> relatedAppPublisher) {
      super(itemView);
      image = (ImageView) itemView.findViewById(R.id.app_image);
      name = (TextView) itemView.findViewById(R.id.app_name);
      checkIndicator = ((ImageView) itemView.findViewById(R.id.check_indicator));
      itemView.setOnClickListener(view -> relatedAppPublisher.call(app));
    }

    void bind(PostRemoteAccessor.RelatedApp app) {
      this.app = app;
      ImageLoader.with(image.getContext())
          .load(app.getImage(), image);
      this.name.setText(app.getName());

      // TODO: 13/07/2017 trinkes support pre lollipop devices
      ObjectAnimator animator;
      if (app.isSelected()) {
        animator = ObjectAnimator.ofFloat(itemView, "elevation", ViewCompat.getElevation(itemView),
            SELECTED_ELEVATION);
      } else {
        animator = ObjectAnimator.ofFloat(itemView, "elevation", ViewCompat.getElevation(itemView),
            UNSELECTED_ELEVATION);
      }
      checkIndicator.setVisibility(app.isSelected() ? View.VISIBLE : View.GONE);

      animator.setDuration(200);
      animator.start();
    }
  }
}

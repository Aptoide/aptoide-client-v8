package cm.aptoide.pt.v8engine.timeline.post;

import android.animation.ObjectAnimator;
import android.support.annotation.Nullable;
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
  public static final int SPINNER = 0;
  public static final int RELATED_APP = 1;
  private final ArrayList<PostRemoteAccessor.RelatedApp> relatedAppList;
  private final PublishRelay<PostRemoteAccessor.RelatedApp> relatedAppPublisher;
  private boolean loading;

  public RelatedAppsAdapter() {
    relatedAppList = new ArrayList<>();
    relatedAppPublisher = PublishRelay.create();
    loading = false;
  }

  public Observable<PostRemoteAccessor.RelatedApp> getClickedView() {
    return relatedAppPublisher;
  }

  public Completable setRelatedApps(List<PostRemoteAccessor.RelatedApp> relatedApps) {

    if (relatedAppList.equals(relatedApps)) {
      return Completable.complete();
    }
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
    View itemView;
    if (viewType == RELATED_APP) {
      itemView = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_view_post_related_app, parent, false);
      return new RelatedAppViewHolder(itemView, relatedAppPublisher);
    } else if (viewType == SPINNER) {
      itemView = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.simple_spinner_item, parent, false);
      return new RecyclerView.ViewHolder(itemView) {
      };
    } else {
      throw new IllegalArgumentException("viewType " + viewType + " not supported");
    }
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof RelatedAppViewHolder) {
      ((RelatedAppViewHolder) holder).bind(relatedAppList.get(loading ? position - 1 : position));
    }
  }

  @Override public int getItemViewType(int position) {
    if (loading && position == 0) {
      return SPINNER;
    } else {
      return RELATED_APP;
    }
  }

  @Override public int getItemCount() {
    return loading ? relatedAppList.size() + 1 : relatedAppList.size();
  }

  public @Nullable PostRemoteAccessor.RelatedApp getCurrentSelected() {
    for (PostRemoteAccessor.RelatedApp relatedApp : relatedAppList) {
      if (relatedApp.isSelected()) return relatedApp;
    }
    return null;
  }

  public void showLoading() {
    if (!loading) {
      loading = true;
      notifyItemInserted(0);
    }
  }

  public void hideLoading() {
    if (loading) {
      loading = false;
      notifyItemRemoved(0);
    }
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

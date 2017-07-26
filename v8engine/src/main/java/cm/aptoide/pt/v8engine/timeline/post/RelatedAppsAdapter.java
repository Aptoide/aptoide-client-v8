package cm.aptoide.pt.v8engine.timeline.post;

import android.animation.ObjectAnimator;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
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

  public void addRelatedApps(List<PostRemoteAccessor.RelatedApp> relatedApps) {
    if (!relatedAppList.equals(relatedApps) && !relatedApps.isEmpty()) {
      PostRemoteAccessor.RelatedApp selected = getCurrentSelected();
      setRelatedAppSelected(selected, relatedApps);
      relatedAppList.addAll(0, relatedApps);
      notifyItemRangeInserted(0, relatedApps.size());
    }
  }

  private void setRelatedAppSelected(PostRemoteAccessor.RelatedApp relatedApp,
      List<PostRemoteAccessor.RelatedApp> list) {
    if (relatedApp != null && list != null) {
      for (PostRemoteAccessor.RelatedApp app : list) {
        app.setSelected(app.equals(relatedApp));
      }
    }
  }

  public Completable setRelatedAppSelected(PostRemoteAccessor.RelatedApp relatedApp) {
    return Completable.fromAction(() -> {
      setRelatedAppSelected(relatedApp, relatedAppList);
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

  public void clearRemoteRelated() {
    int numberOfRemoves = 0;
    for (int i = 0; i < relatedAppList.size(); i++) {
      PostRemoteAccessor.RelatedApp relatedApp = relatedAppList.get(i);
      if (relatedApp.getOrigin()
          .equals(PostManager.Origin.Remote)) {
        relatedAppList.remove(i);
        i--;
        numberOfRemoves++;
      } else {
        break;
      }
    }
    notifyItemRangeRemoved(0, numberOfRemoves);
  }

  public void clearAllRelated() {
    int numberOfRemoves = relatedAppList.size();
    relatedAppList.clear();
    notifyItemRangeRemoved(0, numberOfRemoves);
  }

  private static class RelatedAppViewHolder extends RecyclerView.ViewHolder {
    public static final int SELECTED_ELEVATION = 20;
    public static final int UNSELECTED_ELEVATION = 5;
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

      ObjectAnimator animator;
      if (app.isSelected()) {
        if (shouldAnimateSelection()) {
          scaleView(checkIndicator);
        }
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

    private boolean shouldAnimateSelection() {
      return checkIndicator.getVisibility() != View.VISIBLE;
    }

    private void scaleView(View v) {
      Animation anim =
          new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
              1f);
      anim.setFillAfter(true);
      anim.setDuration(200);
      v.startAnimation(anim);
    }
  }
}

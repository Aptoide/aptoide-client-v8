package cm.aptoide.pt.v8engine.view.recycler;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class RecyclerViewHolder<T> extends RecyclerView.ViewHolder {

  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private T viewModel;
  private Context context;

  protected RecyclerViewHolder(View itemView) {
    super(itemView);
    context = itemView.getContext();
  }

  /**
   * Updates this view with the received view model.
   *
   * @param viewModel the new view model
   */
  public final void updateViewModel(T viewModel) {
    this.viewModel = viewModel;
    update(context, viewModel);
  }

  protected abstract void update(Context context, T viewModel);

  protected T getViewModel() {
    return viewModel;
  }

  @CallSuper protected void addSubscription(Subscription s) {
    compositeSubscription.add(s);
  }

  public final void releaseSubscriptions() {
    if (compositeSubscription.hasSubscriptions() && !compositeSubscription.isUnsubscribed()) {
      compositeSubscription.unsubscribe();
    }
  }

  public abstract int getViewResource();
}

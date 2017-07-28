package cm.aptoide.pt.spotandshareapp.view;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.spotandshareapp.AppModel;
import cm.aptoide.pt.spotandshareapp.Header;
import cm.aptoide.pt.spotandshareapp.R;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by filipe on 19-06-2017.
 */

public class SpotAndSharePickAppsAdapter extends RecyclerView.Adapter<ViewHolder> {

  private static final int TYPE_HEADER = 0;
  private static final int TYPE_ITEM = 1;

  private Header header;
  private List<AppModel> installedApps;
  private PublishSubject<AppModel> appSubject;

  public SpotAndSharePickAppsAdapter(PublishSubject<AppModel> appSubject, Header header) {
    this.appSubject = appSubject;
    this.header = header;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    if (viewType == TYPE_HEADER) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.fragment_spotandshare_app_selection_header, parent, false);
      return new ViewHolderHeader(view);
    } else {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.fragment_spotandshare_app_selection_item, parent, false);
      return new ViewHolderItem(view);
    }
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    if (holder instanceof ViewHolderHeader) {
      ViewHolderHeader viewHolderHeader = (ViewHolderHeader) holder;
      viewHolderHeader.setAppModelHeader(header);
    } else if (holder instanceof ViewHolderItem) {

      ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
      viewHolderItem.setAppModelItem(installedApps.get(position - 1));
    }
  }

  @Override public int getItemViewType(int position) {
    if (isPositionHeader(position)) {
      return TYPE_HEADER;
    }
    return TYPE_ITEM;
  }

  @Override public int getItemCount() {
    if (installedApps != null) {
      return installedApps.size() + 1;
    }
    return 0;
  }

  public void setInstalledAppsList(List<AppModel> installedApps) {
    this.installedApps = installedApps;
  }

  public boolean isPositionHeader(int position) {
    return position == 0;
  }

  public Observable<AppModel> onSelectedApp() {
    return appSubject;
  }

  class ViewHolderHeader extends ViewHolder {

    private TextView headerTextView;

    public ViewHolderHeader(View itemView) {
      super(itemView);
      headerTextView = (TextView) itemView.findViewById(R.id.app_item_text_view);
    }

    public void setAppModelHeader(Header header) {
      headerTextView.setText(header.getTitle());
    }
  }

  class ViewHolderItem extends ViewHolder {

    private ImageView appIcon;
    private TextView appName;
    private FrameLayout frameLayout;

    public ViewHolderItem(View itemView) {
      super(itemView);
      appIcon = (ImageView) itemView.findViewById(R.id.app_item_image_view);
      appName = (TextView) itemView.findViewById(R.id.app_item_text_view);
      frameLayout = (FrameLayout) itemView.findViewById(R.id.app_item_frame_layout);
    }

    public void setAppModelItem(AppModel appModel) {
      appIcon.setImageDrawable(appModel.getAppIconAsDrawable());
      appName.setText(appModel.getAppName());
      frameLayout.setOnClickListener(v -> appSubject.onNext(appModel));
    }
  }
}

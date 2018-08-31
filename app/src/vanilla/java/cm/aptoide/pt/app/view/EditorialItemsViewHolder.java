package cm.aptoide.pt.app.view;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.ArrayList;

/**
 * Created by D01 on 28/08/2018.
 */

class EditorialItemsViewHolder extends RecyclerView.ViewHolder {
  private View itemText;
  private TextView title;
  private TextView firstTitle;
  private TextView secondaryTitle;
  private TextView message;
  private View media;
  private ImageView image;
  private VideoView video;
  private RecyclerView mediaList;
  private View appCard;
  private MediaBundleAdapter mediaBundleAdapter;

  public EditorialItemsViewHolder(View view) {
    super(view);
    itemText = view.findViewById(R.id.editorial_item_text);
    title = (TextView) view.findViewById(R.id.editorial_item_title);
    firstTitle = (TextView) view.findViewById(R.id.editorial_item_first_title);
    secondaryTitle = (TextView) view.findViewById(R.id.editorial_item_secondary_title);
    message = (TextView) view.findViewById(R.id.editorial_item_message);
    media = view.findViewById(R.id.editorial_item_media);
    image = (ImageView) view.findViewById(R.id.editorial_image);
    video = (VideoView) view.findViewById(R.id.editorial_video);
    mediaList = (RecyclerView) view.findViewById(R.id.editoral_image_list);
    appCard = view.findViewById(R.id.app_cardview);
    mediaBundleAdapter = new MediaBundleAdapter(new ArrayList<>());
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
    mediaList.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        int margin = AptoideUtils.ScreenU.getPixelsForDip(8, view.getResources());
        outRect.set(margin, margin, 0, margin);
      }
    });
    mediaList.setLayoutManager(layoutManager);
    mediaList.setAdapter(mediaBundleAdapter);
  }

  public void setVisibility(EditorialItem editorialItem, int position) {

  }
}

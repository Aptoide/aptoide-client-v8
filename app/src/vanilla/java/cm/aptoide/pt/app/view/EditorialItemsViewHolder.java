package cm.aptoide.pt.app.view;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.ArrayList;

/**
 * Created by D01 on 28/08/2018.
 */

class EditorialItemsViewHolder extends RecyclerView.ViewHolder {
  private final ImageView appCardImage;
  private final TextView appCardName;
  private TextView description;
  private View itemText;
  private View title;
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
    title = view.findViewById(R.id.editorial_item_title);
    firstTitle = (TextView) view.findViewById(R.id.editorial_item_first_title);
    secondaryTitle = (TextView) view.findViewById(R.id.editorial_item_secondary_title);
    message = (TextView) view.findViewById(R.id.editorial_item_message);
    media = view.findViewById(R.id.editorial_item_media);
    image = (ImageView) view.findViewById(R.id.editorial_image);
    video = (VideoView) view.findViewById(R.id.editorial_video);
    description = (TextView) view.findViewById(R.id.editorial_image_description);
    mediaList = (RecyclerView) view.findViewById(R.id.editoral_image_list);
    appCard = view.findViewById(R.id.app_cardview);
    appCardImage = (ImageView) appCard.findViewById(R.id.app_icon_imageview);
    appCardName = (TextView) appCard.findViewById(R.id.app_title_textview);
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

  public void setVisibility(EditorialContent editorialItem, int position) {
    if (editorialItem.hasTitle() || editorialItem.hasMessage()) {
      itemText.setVisibility(View.VISIBLE);
      manageTitleVisibility(editorialItem, position);
      manageMessageVisibility(editorialItem);
    }
    manageMediaVisibility(editorialItem);
    if (editorialItem.isPlaceHolderType()) {
      ImageLoader.with(itemView.getContext())
          .load(editorialItem.getIcon(), appCardImage);
      appCardImage.setVisibility(View.VISIBLE);
      appCardName.setText(editorialItem.getAppName());
      appCardName.setVisibility(View.VISIBLE);
      appCard.setVisibility(View.VISIBLE);
    }
  }

  private void manageTitleVisibility(EditorialContent editorialItem, int position) {
    if (editorialItem.hasTitle()) {
      title.setVisibility(View.VISIBLE);
      if (position == 0) {
        firstTitle.setText(editorialItem.getTitle());
        firstTitle.setVisibility(View.VISIBLE);
      } else {
        secondaryTitle.setText(editorialItem.getTitle());
        secondaryTitle.setVisibility(View.VISIBLE);
      }
    }
  }

  private void manageMessageVisibility(EditorialContent editorialItem) {
    if (editorialItem.hasMessage()) {
      message.setText(editorialItem.getMessage());
      message.setVisibility(View.VISIBLE);
    }
  }

  private void manageMediaVisibility(EditorialContent editorialItem) {
    if (editorialItem.hasMedia()) {
      media.setVisibility(View.VISIBLE);
      if (editorialItem.hasListOfMedia()) {
        mediaBundleAdapter.add(editorialItem.getMedia());
        mediaList.setVisibility(View.VISIBLE);
      } else {
        EditorialMedia editorialMedia = editorialItem.getMedia()
            .get(0);
        if (editorialMedia.isImage()) {
          ImageLoader.with(itemView.getContext())
              .load(editorialMedia.getUrl(), image);
          image.setVisibility(View.VISIBLE);
        }
        if (editorialMedia.isVideo()) {
          //TODO add video
          video.setVisibility(View.VISIBLE);
        }
      }
      EditorialMedia editorialMedia = editorialItem.getMedia()
          .get(0);
      if (editorialMedia.hasDescription()) {
        description.setText(editorialMedia.getDescription());
        description.setVisibility(View.VISIBLE);
        //TODO add description here in the future
      }
    }
  }
}

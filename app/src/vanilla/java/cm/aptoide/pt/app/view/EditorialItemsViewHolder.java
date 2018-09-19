package cm.aptoide.pt.app.view;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by D01 on 28/08/2018.
 */

class EditorialItemsViewHolder extends RecyclerView.ViewHolder {
  private final ImageView appCardImage;
  private final TextView appCardRating;
  private final View appCardRatingLayout;
  private final TextView appCardNameWithRating;
  private final View appCardLayout;
  private final DecimalFormat oneDecimalFormat;
  private final Button appCardButton;
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
  private MediaBundleAdapter mediaBundleAdapter;

  private LinearLayout downloadInfoLayout;
  private ProgressBar downloadProgressBar;
  private TextView downloadProgressValue;
  private ImageView cancelDownload;
  private ImageView pauseDownload;
  private ImageView resumeDownload;
  private View downloadControlsLayout;
  private RelativeLayout cardInfoLayout;

  public EditorialItemsViewHolder(View view, DecimalFormat oneDecimalFormat) {
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
    appCardLayout = view.findViewById(R.id.app_cardview);
    this.oneDecimalFormat = oneDecimalFormat;
    appCardButton = (Button) appCardLayout.findViewById(R.id.appview_install_button);
    appCardNameWithRating =
        (TextView) appCardLayout.findViewById(R.id.app_title_textview_with_rating);
    appCardImage = (ImageView) appCardLayout.findViewById(R.id.app_icon_imageview);
    appCardRating = (TextView) appCardLayout.findViewById(R.id.rating_label);
    appCardRatingLayout = appCardLayout.findViewById(R.id.rating_layout);

    cardInfoLayout = (RelativeLayout) view.findViewById(R.id.card_info_install_layout);
    downloadControlsLayout = view.findViewById(R.id.install_controls_layout);
    downloadInfoLayout = ((LinearLayout) view.findViewById(R.id.appview_transfer_info));
    downloadProgressBar = ((ProgressBar) view.findViewById(R.id.appview_download_progress_bar));
    downloadProgressValue = (TextView) view.findViewById(R.id.appview_download_progress_number);
    cancelDownload = ((ImageView) view.findViewById(R.id.appview_download_cancel_button));
    resumeDownload = ((ImageView) view.findViewById(R.id.appview_download_resume_download));
    pauseDownload = ((ImageView) view.findViewById(R.id.appview_download_pause_download));

    mediaBundleAdapter = new MediaBundleAdapter(new ArrayList<>());
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
    mediaList.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        int margin = AptoideUtils.ScreenU.getPixelsForDip(6, view.getResources());
        outRect.set(0, margin, margin, margin);
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
      appCardLayout.setVisibility(View.INVISIBLE);
      appCardLayout.setScaleX(0.1f);
      appCardLayout.setScaleY(0.1f);
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
      }
    }
  }

  public boolean isVisible(float screenHeight, float screenWidth) {
    final Rect actualPosition = new Rect();
    appCardLayout.getLocalVisibleRect(actualPosition);
    final Rect screen =
        new Rect(0, 0, (int) screenWidth, (int) screenHeight - appCardLayout.getHeight() * 2);
    return actualPosition.intersect(screen);
  }

  public View getPlaceHolder() {
    return appCardLayout;
  }

  public void setPlaceHolderInfo(String appName, String image, String buttonText, float rating) {
    ImageLoader.with(itemView.getContext())
        .load(image, appCardImage);
    appCardImage.setVisibility(View.VISIBLE);
    if (rating == 0) {
      appCardRating.setText(R.string.appcardview_title_no_stars);
    } else {
      appCardRating.setText(oneDecimalFormat.format(rating));
    }
    appCardRatingLayout.setVisibility(View.VISIBLE);
    appCardNameWithRating.setText(appName);
    appCardNameWithRating.setVisibility(View.VISIBLE);
    appCardButton.setText(buttonText);
    appCardLayout.setVisibility(View.VISIBLE);
  }

  public void setPlaceHolderDownloadInfo(DownloadModel downloadModel, String update, String install,
      String open) {
    if (downloadModel.isDownloading()) {
      downloadInfoLayout.setVisibility(View.VISIBLE);
      cardInfoLayout.setVisibility(View.GONE);
      setDownloadState(downloadModel.getProgress(), downloadModel.getDownloadState());
    } else {
      downloadInfoLayout.setVisibility(View.GONE);
      cardInfoLayout.setVisibility(View.VISIBLE);
      setButtonText(downloadModel, update, install, open);
    }
  }

  private void setButtonText(DownloadModel model, String update, String install, String open) {
    DownloadModel.Action action = model.getAction();
    switch (action) {
      case UPDATE:
        appCardButton.setText(update);
        break;
      case INSTALL:
        appCardButton.setText(install);
        break;
      case OPEN:
        appCardButton.setText(open);
        break;
    }
  }

  private void setDownloadState(int progress, DownloadModel.DownloadState downloadState) {
    LinearLayout.LayoutParams pauseShowing =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 4f);
    LinearLayout.LayoutParams pauseHidden =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 2f);
    switch (downloadState) {
      case ACTIVE:
        downloadProgressBar.setIndeterminate(false);
        downloadProgressBar.setProgress(progress);
        downloadProgressValue.setText(String.valueOf(progress) + "%");
        pauseDownload.setVisibility(View.VISIBLE);
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        downloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      case INDETERMINATE:
        downloadProgressBar.setIndeterminate(true);
        pauseDownload.setVisibility(View.VISIBLE);
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        downloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      case PAUSE:
        downloadProgressBar.setIndeterminate(false);
        downloadProgressBar.setProgress(progress);
        downloadProgressValue.setText(String.valueOf(progress) + "%");
        pauseDownload.setVisibility(View.GONE);
        cancelDownload.setVisibility(View.VISIBLE);
        resumeDownload.setVisibility(View.VISIBLE);
        downloadControlsLayout.setLayoutParams(pauseHidden);
        break;
      case COMPLETE:
        downloadProgressBar.setIndeterminate(true);
        pauseDownload.setVisibility(View.VISIBLE);
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        downloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      default:
        //eventListener
        break;
      case NOT_ENOUGH_STORAGE_ERROR:
        //eventListener
        break;
    }
  }
}

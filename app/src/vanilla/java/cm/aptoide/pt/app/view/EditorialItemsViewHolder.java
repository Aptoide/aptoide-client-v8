package cm.aptoide.pt.app.view;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.home.SnapToStartHelper;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import rx.subjects.PublishSubject;

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
  private final LinearLayoutManager layoutManager;
  private TextSwitcher descriptionSwitcher;
  private View itemText;
  private View title;
  private TextView firstTitle;
  private TextView secondaryTitle;
  private TextView message;
  private View media;
  private ImageView image;
  private ImageView videoThumbnail;
  private FrameLayout videoThumbnailContainer;
  private RecyclerView mediaList;
  private MediaBundleAdapter mediaBundleAdapter;
  private PublishSubject<EditorialEvent> uiEventListener;

  private LinearLayout downloadInfoLayout;
  private ProgressBar downloadProgressBar;
  private TextView downloadProgressValue;
  private ImageView cancelDownload;
  private ImageView pauseDownload;
  private ImageView resumeDownload;
  private View downloadControlsLayout;
  private RelativeLayout cardInfoLayout;
  private int currentMediaPosition;
  private boolean mediaDescriptionVisible;

  public EditorialItemsViewHolder(View view, DecimalFormat oneDecimalFormat,
      PublishSubject<EditorialEvent> uiEventListener) {
    super(view);
    itemText = view.findViewById(R.id.editorial_item_text);
    title = view.findViewById(R.id.editorial_item_title);
    firstTitle = (TextView) view.findViewById(R.id.editorial_item_first_title);
    secondaryTitle = (TextView) view.findViewById(R.id.editorial_item_secondary_title);
    message = (TextView) view.findViewById(R.id.editorial_item_message);
    media = view.findViewById(R.id.editorial_item_media);
    image = (ImageView) view.findViewById(R.id.editorial_image);
    videoThumbnail = view.findViewById(R.id.editorial_video_thumbnail);
    videoThumbnailContainer = view.findViewById(R.id.editorial_video_thumbnail_container);
    descriptionSwitcher =
        (TextSwitcher) view.findViewById(R.id.editorial_image_description_switcher);
    mediaList = (RecyclerView) view.findViewById(R.id.editoral_image_list);
    appCardLayout = view.findViewById(R.id.app_cardview);
    this.oneDecimalFormat = oneDecimalFormat;
    this.uiEventListener = uiEventListener;
    appCardButton = (Button) appCardLayout.findViewById(R.id.appview_install_button);
    appCardNameWithRating =
        (TextView) appCardLayout.findViewById(R.id.app_title_textview_with_rating);
    appCardImage = (ImageView) appCardLayout.findViewById(R.id.app_icon_imageview);
    appCardRating = (TextView) appCardLayout.findViewById(R.id.rating_label);
    appCardRatingLayout = appCardLayout.findViewById(R.id.rating_layout);
    mediaBundleAdapter = new MediaBundleAdapter(new ArrayList<>(), uiEventListener);

    cardInfoLayout = (RelativeLayout) view.findViewById(R.id.card_info_install_layout);
    downloadControlsLayout = view.findViewById(R.id.install_controls_layout);
    downloadInfoLayout = ((LinearLayout) view.findViewById(R.id.appview_transfer_info));
    downloadProgressBar = ((ProgressBar) view.findViewById(R.id.appview_download_progress_bar));
    downloadProgressValue = (TextView) view.findViewById(R.id.appview_download_progress_number);
    cancelDownload = ((ImageView) view.findViewById(R.id.appview_download_cancel_button));
    resumeDownload = ((ImageView) view.findViewById(R.id.appview_download_resume_download));
    pauseDownload = ((ImageView) view.findViewById(R.id.appview_download_pause_download));

    layoutManager =
        new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);

    SnapHelper mediaSnap = new SnapToStartHelper();
    mediaSnap.attachToRecyclerView(mediaList);

    Animation fadeIn = new AlphaAnimation(0, 1);
    fadeIn.setDuration(1000);
    Animation fadeOut = new AlphaAnimation(1, 0);
    fadeOut.setDuration(500);
    currentMediaPosition = -1;
    descriptionSwitcher.setInAnimation(fadeIn);
    descriptionSwitcher.setOutAnimation(fadeOut);

    mediaList.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        int margin = AptoideUtils.ScreenU.getPixelsForDip(6, view.getResources());
        outRect.set(0, 0, margin, 0);
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
    manageMediaVisibility(editorialItem, position);
    if (editorialItem.isPlaceHolderType()) {
      setPlaceHolderInfo(editorialItem.getAppName(), editorialItem.getIcon(),
          editorialItem.getRating());
      appCardLayout.setVisibility(View.INVISIBLE);
      appCardLayout.setScaleX(0.1f);
      appCardLayout.setScaleY(0.1f);
      setListeners();
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

  public void manageDescriptionAnimationVisibility(int firstVisibleItem,
      List<EditorialMedia> media) {
    if (!mediaDescriptionVisible) {
      String descriptionText = media.get(firstVisibleItem)
          .getDescription();
      if (currentMediaPosition != firstVisibleItem) {
        descriptionSwitcher.setVisibility(View.VISIBLE);
        descriptionSwitcher.setText(descriptionText);
        currentMediaPosition = firstVisibleItem;
      }
    }
  }

  public void setAllDescriptionsVisible() {
    if (!mediaDescriptionVisible) {
      for (int mediaPosition = 0; mediaPosition < mediaBundleAdapter.getItemCount();
          mediaPosition++) {
        MediaViewHolder mediaViewHolder =
            ((MediaViewHolder) mediaList.findViewHolderForAdapterPosition(mediaPosition));
        if (mediaViewHolder != null) {
          mediaViewHolder.setDescriptionVisible();
          mediaDescriptionVisible = true;
        }
      }
    }
  }

  private void manageMediaVisibility(EditorialContent editorialItem, int position) {
    if (editorialItem.hasMedia()) {
      List<EditorialMedia> editorialMediaList = editorialItem.getMedia();
      media.setVisibility(View.VISIBLE);
      if (editorialItem.hasListOfMedia()) {
        mediaBundleAdapter.add(editorialMediaList);
        mediaList.setVisibility(View.VISIBLE);
        if (editorialItem.hasAnyMediaDescription()) {
          mediaList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
              super.onScrolled(recyclerView, dx, dy);
              uiEventListener.onNext(new EditorialEvent(EditorialEvent.Type.MEDIA_LIST,
                  layoutManager.findFirstCompletelyVisibleItemPosition(),
                  layoutManager.findLastCompletelyVisibleItemPosition(), position,
                  editorialItem.getMedia()));
            }
          });
        }
      } else {
        EditorialMedia editorialMedia = editorialMediaList.get(0);
        if (editorialMedia.hasDescription()) {
          descriptionSwitcher.setCurrentText(editorialMedia.getDescription());
          descriptionSwitcher.setVisibility(View.VISIBLE);
        }
        if (editorialMedia.isImage()) {
          ImageLoader.with(itemView.getContext())
              .load(editorialMedia.getUrl(), image);
          image.setVisibility(View.VISIBLE);
        }
        if (editorialMedia.isVideo()) {
          if (editorialMedia.getThumbnail() != null) {
            ImageLoader.with(itemView.getContext())
                .load(editorialMedia.getThumbnail(), videoThumbnail);
          }
          if (editorialMedia.hasUrl()) {
            videoThumbnailContainer.setVisibility(View.VISIBLE);
            videoThumbnailContainer.setOnClickListener(v -> uiEventListener.onNext(
                new EditorialEvent(EditorialEvent.Type.MEDIA, editorialMedia.getUrl())));
          }
        }
      }
    }
  }

  private void setPlaceHolderInfo(String appName, String image, float rating) {
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
    appCardLayout.setVisibility(View.VISIBLE);
  }

  public boolean isVisible(float screenHeight, float screenWidth) {
    final Rect placeHolderPosition = new Rect();
    appCardLayout.getLocalVisibleRect(placeHolderPosition);
    final Rect screen =
        new Rect(0, 0, (int) screenWidth, (int) screenHeight - appCardLayout.getHeight() * 2);
    return placeHolderPosition.intersect(screen);
  }

  public View getPlaceHolder() {
    return appCardLayout;
  }

  public void setPlaceHolderDownloadingInfo(DownloadModel downloadModel) {
    downloadInfoLayout.setVisibility(View.VISIBLE);
    cardInfoLayout.setVisibility(View.GONE);
    setDownloadState(downloadModel.getProgress(), downloadModel.getDownloadState());
  }

  public void setPlaceHolderDefaultStateInfo(DownloadModel downloadModel, String update,
      String install, String open) {
    downloadInfoLayout.setVisibility(View.GONE);
    cardInfoLayout.setVisibility(View.VISIBLE);
    setButtonText(downloadModel, update, install, open);
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
        break;
    }
  }

  private void setListeners() {
    cancelDownload.setOnClickListener(
        click -> uiEventListener.onNext(new EditorialEvent(EditorialEvent.Type.CANCEL)));
    resumeDownload.setOnClickListener(
        click -> uiEventListener.onNext(new EditorialEvent(EditorialEvent.Type.RESUME)));
    pauseDownload.setOnClickListener(
        click -> uiEventListener.onNext(new EditorialEvent(EditorialEvent.Type.PAUSE)));
    appCardLayout.setOnClickListener(
        click -> uiEventListener.onNext(new EditorialEvent(EditorialEvent.Type.APPCARD)));
    appCardButton.setOnClickListener(
        click -> uiEventListener.onNext(new EditorialEvent(EditorialEvent.Type.BUTTON)));
  }
}

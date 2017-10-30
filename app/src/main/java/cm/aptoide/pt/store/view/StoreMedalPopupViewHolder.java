package cm.aptoide.pt.store.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;

/**
 * Created by trinkes on 30/10/2017.
 */
class StoreMedalPopupViewHolder {

  private final ImageView headerBackground;
  private final ImageView medalIcon;
  private final ImageView tinMedal;
  private final ImageView bronzeMedal;
  private final ImageView silverMedal;
  private final ImageView goldMedal;
  private final ImageView platinumMedal;
  private final TextView medalText;
  private final TextView congratulationsMessage;
  private final TextView uploadedAppsTv;
  private final TextView downloadsTv;
  private final TextView followersTv;
  private final TextView reviewsTv;
  private final View progress1;
  private final View progress2;
  private final View progress3;
  private final View progress4;

  public StoreMedalPopupViewHolder(View view) {
    headerBackground = ((ImageView) view.findViewById(R.id.header_background));
    medalIcon = ((ImageView) view.findViewById(R.id.medal_icon));
    tinMedal = ((ImageView) view.findViewById(R.id.tin_medal));
    bronzeMedal = ((ImageView) view.findViewById(R.id.bronze_medal));
    silverMedal = ((ImageView) view.findViewById(R.id.silver_medal));
    goldMedal = ((ImageView) view.findViewById(R.id.gold_medal));
    platinumMedal = ((ImageView) view.findViewById(R.id.platinum_medal));
    medalText = (TextView) view.findViewById(R.id.medal_title);
    congratulationsMessage = (TextView) view.findViewById(R.id.congratulations_message);
    uploadedAppsTv = (TextView) view.findViewById(R.id.uploaded_apps);
    downloadsTv = (TextView) view.findViewById(R.id.downloads);
    followersTv = (TextView) view.findViewById(R.id.followers);
    reviewsTv = (TextView) view.findViewById(R.id.reviews);
    progress1 = view.findViewById(R.id.progress1);
    progress2 = view.findViewById(R.id.progress2);
    progress3 = view.findViewById(R.id.progress3);
    progress4 = view.findViewById(R.id.progress4);
  }

  public ImageView getHeaderBackground() {
    return headerBackground;
  }

  public ImageView getMedalIcon() {
    return medalIcon;
  }

  public ImageView getTinMedal() {
    return tinMedal;
  }

  public ImageView getBronzeMedal() {
    return bronzeMedal;
  }

  public ImageView getSilverMedal() {
    return silverMedal;
  }

  public ImageView getGoldMedal() {
    return goldMedal;
  }

  public ImageView getPlatinumMedal() {
    return platinumMedal;
  }

  public TextView getMedalText() {
    return medalText;
  }

  public TextView getCongratulationsMessage() {
    return congratulationsMessage;
  }

  public TextView getUploadedAppsTv() {
    return uploadedAppsTv;
  }

  public TextView getDownloadsTv() {
    return downloadsTv;
  }

  public TextView getFollowersTv() {
    return followersTv;
  }

  public TextView getReviewsTv() {
    return reviewsTv;
  }

  public View getProgress1() {
    return progress1;
  }

  public View getProgress2() {
    return progress2;
  }

  public View getProgress3() {
    return progress3;
  }

  public View getProgress4() {
    return progress4;
  }
}

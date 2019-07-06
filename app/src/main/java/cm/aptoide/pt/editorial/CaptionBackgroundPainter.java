package cm.aptoide.pt.editorial;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import cm.aptoide.pt.R;

public class CaptionBackgroundPainter {

  private final Resources resources;

  public CaptionBackgroundPainter(Resources resources) {
    this.resources = resources;
  }

  public void addColorBackgroundToCaption(CardView captionView, String captionColor) {
    if (captionColor != null && !captionColor.isEmpty()) {
      try {
        captionView.setCardBackgroundColor(Color.parseColor(captionColor));
      } catch (IllegalArgumentException e) {
        setDefaultBackgroundColor(captionView);
      }
    } else {
      setDefaultBackgroundColor(captionView);
    }
  }

  private void setDefaultBackgroundColor(CardView captionView) {
    captionView.setCardBackgroundColor(this.resources.getColor(R.color.curation_default));
  }
}

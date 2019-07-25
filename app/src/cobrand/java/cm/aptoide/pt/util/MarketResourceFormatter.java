package cm.aptoide.pt.util;

import android.content.Context;
import cm.aptoide.pt.R;

public class MarketResourceFormatter {

  private String marketName;

  public MarketResourceFormatter(String marketName) {
    this.marketName = marketName;
  }

  public String formatString(Context context, int id, String... optParamaters) {
    switch (id) {
      case R.string.reviewappview_highlighted_reviews_explanation_4:
      case R.string.update_self_msg:
      case R.string.hello_follow_me_on_aptoide:
      case R.string.wizard_sub_title_viewpager_two:
      case R.string.create_profile_pub_pri:
      case R.string.setting_category_autoupdate_message:
      case R.string.setting_category_autoupdate_title:
        return context.getString(id, marketName);
    }
    return context.getString(id);
  }
}

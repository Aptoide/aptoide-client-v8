package cm.aptoide.pt.app.view.widget;

import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.view.displayable.AppViewRewardAppDisplayable;
import cm.aptoide.pt.view.recycler.widget.Widget;
import java.text.DecimalFormat;

/**
 * Created by filipegoncalves on 4/27/18.
 */

public class AppViewRewardAppWidget extends Widget<AppViewRewardAppDisplayable> {

  private TextView appcoinsRewardMessage;
  private DecimalFormat twoDecimalFormat;

  public AppViewRewardAppWidget(@NonNull View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    appcoinsRewardMessage = (TextView) itemView.findViewById(R.id.appcoins_reward_message);
  }

  @Override public void unbindView() {
    super.unbindView();
    twoDecimalFormat = null;
  }

  @Override public void bindView(AppViewRewardAppDisplayable displayable) {
    this.twoDecimalFormat = new DecimalFormat("#.##");
    appcoinsRewardMessage.setText(buildRewardMessage(displayable.getAppcoinsReward()));
  }

  private SpannableString buildRewardMessage(double appcoinsReward) {
    String reward = String.valueOf(twoDecimalFormat.format(appcoinsReward)) + " APPC";
    String tryAppMessage = itemView.getResources()
        .getString(R.string.appc_message_appview_appcoins_reward, reward);

    SpannableString spannable = new SpannableString(tryAppMessage);
    spannable.setSpan(new ForegroundColorSpan(itemView.getResources()
            .getColor(R.color.orange_700)), tryAppMessage.indexOf(reward), tryAppMessage.length(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    return spannable;
  }
}

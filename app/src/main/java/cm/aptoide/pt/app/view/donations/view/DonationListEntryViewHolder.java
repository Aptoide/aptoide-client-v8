package cm.aptoide.pt.app.view.donations.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.Translator;
import java.text.DecimalFormat;

/**
 * Created by franciscocalado on 11/10/2018.
 */

public class DonationListEntryViewHolder extends RecyclerView.ViewHolder {
  private static final String DEFAULT_NO_NAME = "Mysterious Donor";

  private TextView position;
  private TextView nickname;
  private TextView donatedValue;
  private DecimalFormat decimalFormat;

  public DonationListEntryViewHolder(View itemView, DecimalFormat decimalFormat) {
    super(itemView);
    this.decimalFormat = decimalFormat;
    position = itemView.findViewById(R.id.user_placement);
    nickname = itemView.findViewById(R.id.nickname);
    donatedValue = itemView.findViewById(R.id.donated_value);
  }

  public void setUp(int position, String nickname, float donatedValue) {
    this.position.setText(String.valueOf(position));
    if (nickname != null && nickname.equals(DEFAULT_NO_NAME)) {
      this.nickname.setText(Translator.translate(nickname, itemView.getContext(), ""));
    } else {
      this.nickname.setText(nickname);
    }
    this.nickname.setText(nickname);
    this.donatedValue.setText(decimalFormat.format(donatedValue));
  }
}

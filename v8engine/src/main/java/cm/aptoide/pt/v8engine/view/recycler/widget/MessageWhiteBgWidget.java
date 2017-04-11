package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.MessageWhiteBgDisplayable;

/**
 * Created by trinkes on 20/12/2016.
 */

public class MessageWhiteBgWidget extends Widget<MessageWhiteBgDisplayable> {

  private TextView message;

  public MessageWhiteBgWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    message = (TextView) itemView.findViewById(R.id.message);
  }

  @Override public void bindView(MessageWhiteBgDisplayable displayable) {
    String messageText = displayable.getMessage();
    if (TextUtils.isEmpty(messageText)) {
      message.setVisibility(View.GONE);
    } else {
      message.setText(messageText);
    }
  }
}

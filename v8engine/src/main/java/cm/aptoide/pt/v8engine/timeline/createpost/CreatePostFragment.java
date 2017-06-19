package cm.aptoide.pt.v8engine.timeline.createpost;

import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.fragment.UIComponentFragment;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;

public class CreatePostFragment extends UIComponentFragment implements CreatePostView {

  private TextInputEditText userInputLayout;
  private EditText userInput;
  private Button share;

  @Override public int getContentViewId() {
    return R.layout.fragment_create_timeline_post;
  }

  @Override public void bindViews(@Nullable View view) {
    userInputLayout = (TextInputEditText) view.findViewById(R.id.input_layout_text);
    userInput = (EditText) view.findViewById(R.id.input_text);
    share = (Button) view.findViewById(R.id.share);
  }

  @Override public void setupViews() {
    attachPresenter(new CreatePostPresenter(this, CrashReport.getInstance()), null);
  }

  @Override public Observable<Void> shareButtonPressed() {
    return RxView.clicks(share);
  }

  @Override public String getInputText() {
    return userInput.getText().toString();
  }
}

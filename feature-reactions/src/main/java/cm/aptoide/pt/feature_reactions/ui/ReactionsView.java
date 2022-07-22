package cm.aptoide.pt.feature_reactions.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import cm.aptoide.pt.feature_reactions.data.ReactionType;
import java.util.Arrays;
import java.util.List;

public class ReactionsView extends LinearLayout {

  public static int WIDTH_REACTIONS = 0;
  public static int HEIGHT_REACTIONS = 0;
  public static int REACTIONS_PADDING = 0;
  private List<Reaction> reactions;

  public ReactionsView(Context context) {
    super(context);
    init();
  }

  public ReactionsView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ReactionsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {

    setReactionOrientation();

    reactions = Arrays.asList(new Reaction(ReactionType.LIKE, REACTIONS_PADDING, this.getContext()),
        new Reaction(ReactionType.LAUGH, REACTIONS_PADDING, this.getContext()),
        new Reaction(ReactionType.LOVE, REACTIONS_PADDING, this.getContext()),
        new Reaction(ReactionType.THUG, REACTIONS_PADDING, this.getContext()),
        new Reaction(ReactionType.DOWN, REACTIONS_PADDING, this.getContext()));

    this.setOrientation(HORIZONTAL);

    ViewGroup.LayoutParams reactionParams =
        new ViewGroup.LayoutParams(WIDTH_REACTIONS, HEIGHT_REACTIONS);

    for (Reaction reaction : reactions) {
      reaction.setReactionParams(reactionParams);
      this.addView(reaction.getView());
      reaction.play();
    }
  }

  public void setReactionOrientation() {
    int orientation = getResources().getConfiguration().orientation;
    WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
      WIDTH_REACTIONS = Math.round((size.x) / 5);
      HEIGHT_REACTIONS = Math.round((size.x) / 5);
      REACTIONS_PADDING = Math.round(7 * (WIDTH_REACTIONS / 100));
    } else {
      WIDTH_REACTIONS = Math.round(((size.x) / 2) / 5);
      HEIGHT_REACTIONS = Math.round(((size.x) / 2) / 5);
      REACTIONS_PADDING = Math.round(7 * (WIDTH_REACTIONS / 100));
    }
  }

  public int getReactionsViewHeight() {
    return REACTIONS_PADDING + REACTIONS_PADDING + HEIGHT_REACTIONS;
  }

  public void setCallback(@Nullable Callback callback) {
    for (Reaction reaction : reactions) {
      reaction.setCallback(callback);
    }
  }

  public interface Callback {

    /**
     * Called when a reaction item is clicked.
     *
     * @param reactionType The menu item that is selected
     */
    void onReactionItemClicked(ReactionType reactionType);
  }
}
package cm.aptoide.pt.reactions.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cm.aptoide.pt.R;
import cm.aptoide.pt.reactions.data.ReactionType;
import cm.aptoide.pt.reactions.util.DisplayUtil;
import java.util.Arrays;
import java.util.List;

public class ReactionsView extends LinearLayout {

  public static final int WIDTH_REACTIONS = DisplayUtil.dpToPx(56);
  public static final int HEIGHT_REACTIONS = DisplayUtil.dpToPx(56);
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
    reactions = Arrays.asList(new Reaction(ReactionType.LIKE, this.getContext()),
        new Reaction(ReactionType.LAUGH, this.getContext()),
        new Reaction(ReactionType.LOVE, this.getContext()),
        new Reaction(ReactionType.THUG, this.getContext()),
        new Reaction(ReactionType.DOWN, this.getContext()));

    this.setOrientation(HORIZONTAL);
    this.setBackground(this.getContext()
        .getResources()
        .getDrawable(R.drawable.rounded_corners_white));

    ViewGroup.LayoutParams reactionParams =
        new ViewGroup.LayoutParams(WIDTH_REACTIONS, HEIGHT_REACTIONS);

    for (Reaction reaction : reactions) {
      reaction.setReactionParams(reactionParams);
      this.addView(reaction.getView());
    }
  }

  public int getReactionsViewHeight() {
    return 8 + 8 + HEIGHT_REACTIONS;
  }

  @Override protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
    super.onVisibilityChanged(changedView, visibility);
    if (visibility == VISIBLE) {
      for (Reaction reaction : reactions) {
        reaction.play();
      }
    } else {
      for (Reaction reaction : reactions) {
        reaction.cancel();
      }
    }
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

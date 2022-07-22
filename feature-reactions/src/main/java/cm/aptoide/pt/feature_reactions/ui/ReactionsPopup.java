package cm.aptoide.pt.feature_reactions.ui;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import cm.aptoide.pt.feature_reactions.R;
import cm.aptoide.pt.feature_reactions.data.ReactionType;

public class ReactionsPopup {

  private final View anchorView;
  private final PopupWindow popup;
  private final ReactionsView reactionsView;

  private int gravity = Gravity.TOP | Gravity.START;

  private OnReactionClickListener reactionClickListener;
  private OnDismissListener onDismissListener;

  /**
   * Constructor to create a new reactions popup with an anchor view.
   *
   * @param context Context the reactions popup is running in, through which it can access the
   *                current theme, resources, etc.
   * @param anchor  Anchor view for this popup. The popup will appear on top of
   */

  public ReactionsPopup(@NonNull Context context, @NonNull View anchor) {
    this.anchorView = anchor;

    popup = new PopupWindow();
    popup.setWindowLayoutMode(WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT);
    reactionsView = new ReactionsView(context);
    reactionsView.setVisibility(View.VISIBLE);
    popup.setContentView(reactionsView);
    popup.setFocusable(true);
    popup.setClippingEnabled(true);
    popup.setBackgroundDrawable(
        ContextCompat.getDrawable(context, R.drawable.rounded_corners_reactions));
    popup.setElevation(10);

    reactionsView.setCallback(reactionType -> {
      if (reactionClickListener != null) {
        reactionClickListener.onReactionItemClick(reactionType);
      }
    });

    popup.setOnDismissListener(() -> {
      if (onDismissListener != null) {
        onDismissListener.onDismiss(reactionsView);
      }
    });
  }

  /**
   * Show the reactions popup anchored to the view specified during construction.
   *
   * @see #dismiss()
   */
  public void show() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      popup.showAsDropDown(anchorView, (int) anchorView.getX(),
          (int) anchorView.getY() - anchorView.getHeight() - reactionsView.getReactionsViewHeight(),
          this.gravity);
    } else {
      popup.showAsDropDown(anchorView, (int) anchorView.getX(), (int) anchorView.getY()
          - anchorView.getHeight()
          - reactionsView.getReactionsViewHeight());
    }
  }

  /**
   * Dismiss the reactions popup.
   *
   * @see #show()
   */
  public void dismiss() {
    popup.dismiss();
  }

  /**
   * Sets a listener that will be notified when the user selects a reaction item from the reactions
   * list.
   *
   * @param listener the listener to notify
   */
  public void setOnReactionsItemClickListener(@Nullable OnReactionClickListener listener) {
    reactionClickListener = listener;
  }

  /**
   * @return the gravity used to align the popup window to its anchor view
   * @see #setGravity(int)
   */
  public int getGravity() {
    return this.gravity;
  }

  /**
   * Sets the gravity used to align the popup window to its anchor view.
   * <p>
   * If the popup is showing, calling this method will take effect only the next time the popup is
   * shown.
   *
   * @param gravity the gravity used to align the popup window
   * @see #getGravity()
   */
  public void setGravity(int gravity) {
    this.gravity = gravity;
  }

  /**
   * Sets a listener that will be notified when reactions popup is dismissed.
   *
   * @param listener the listener to notify
   */
  public void setOnDismissListener(@Nullable OnDismissListener listener) {
    onDismissListener = listener;
  }

  /**
   * Interface responsible for receiving reaction item click events.
   */
  public interface OnReactionClickListener {
    /**
     * This method will be invoked when a reaction is clicked.
     *
     * @param item the reaction item that was clicked
     */
    void onReactionItemClick(ReactionType item);
  }

  /**
   * Callback interface used to notify the application that the menu has closed.
   */
  public interface OnDismissListener {
    /**
     * Called when the associated reactions popup has been dismissed.
     *
     * @param reactionsView the reactions popup that was dismissed
     */
    void onDismiss(ReactionsView reactionsView);
  }
}
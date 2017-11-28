package cm.aptoide.pt.social.view;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import cm.aptoide.pt.R;
import cm.aptoide.pt.social.view.viewholder.Game1ViewHolder;
import cm.aptoide.pt.social.view.viewholder.Game2ViewHolder;
import cm.aptoide.pt.social.view.viewholder.Game3ViewHolder;
import com.jakewharton.rxrelay.PublishRelay;

/**
 * Created by franciscocalado on 11/27/17.
 */

public class GameCardTouchHandler extends ItemTouchHelper.SimpleCallback {


  public GameCardTouchHandler() {
    super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
  }

  @Override public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
      RecyclerView.ViewHolder target) {
    return false;
  }

  @Override
  public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    if (!((viewHolder instanceof Game1ViewHolder)||(viewHolder instanceof Game2ViewHolder)||(viewHolder instanceof Game3ViewHolder))) return 0;
    return super.getSwipeDirs(recyclerView, viewHolder);
  }

  @Override
  public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
    if(actionState== ItemTouchHelper.ACTION_STATE_SWIPE){
      if(dX > 0){
        viewHolder.itemView.findViewById(R.id.stamp_left).setVisibility(View.VISIBLE);
        if(dX<72) {
          viewHolder.itemView.findViewById(R.id.stamp_left).setAlpha(0.1f);
          if(viewHolder instanceof Game1ViewHolder || viewHolder instanceof Game2ViewHolder){
            viewHolder.itemView.findViewById(R.id.right_answer).setAlpha(0.5f);
            viewHolder.itemView.findViewById(R.id.left_answer).setAlpha(0.5f);
          }
          else{
            viewHolder.itemView.findViewById(R.id.game_card_question3_answer_left).setAlpha(0.5f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_icon_left).setAlpha(0.5f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_answer_right).setAlpha(0.5f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_icon_right).setAlpha(0.5f);
          }
        }
        if(dX>=72 && dX<144){
          viewHolder.itemView.findViewById(R.id.stamp_left).setAlpha(0.2f);
          if(viewHolder instanceof Game1ViewHolder || viewHolder instanceof Game2ViewHolder){
            viewHolder.itemView.findViewById(R.id.right_answer).setAlpha(0.3f);
            viewHolder.itemView.findViewById(R.id.left_answer).setAlpha(0.3f);
          }
          else{
            viewHolder.itemView.findViewById(R.id.game_card_question3_answer_left).setAlpha(0.3f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_icon_left).setAlpha(0.3f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_answer_right).setAlpha(0.3f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_icon_right).setAlpha(0.3f);
          }
        }
        if(dX>=144 && dX<216){
          viewHolder.itemView.findViewById(R.id.stamp_left).setAlpha(0.3f);
          if(viewHolder instanceof Game1ViewHolder || viewHolder instanceof Game2ViewHolder){
            viewHolder.itemView.findViewById(R.id.right_answer).setAlpha(0f);
            viewHolder.itemView.findViewById(R.id.left_answer).setAlpha(0f);
          }
          else{
            viewHolder.itemView.findViewById(R.id.game_card_question3_answer_left).setAlpha(0f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_icon_left).setAlpha(0f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_answer_right).setAlpha(0f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_icon_right).setAlpha(0f);
          }
        }
        if(dX>=216 && dX<288){
          viewHolder.itemView.findViewById(R.id.stamp_left).setAlpha(0.4f);
        }
        if(dX>=288 && dX<360){
          viewHolder.itemView.findViewById(R.id.stamp_left).setAlpha(0.5f);
        }
        if(dX>=360 && dX<432) {
          viewHolder.itemView.findViewById(R.id.stamp_left).setAlpha(0.6f);
        }
        if(dX>=432 && dX<504){
          viewHolder.itemView.findViewById(R.id.stamp_left).setAlpha(0.7f);
        }
        if(dX>=504 && dX<576) {
          viewHolder.itemView.findViewById(R.id.stamp_left).setAlpha(0.8f);
        }
        if(dX>=576 && dX<648){
          viewHolder.itemView.findViewById(R.id.stamp_left).setAlpha(0.9f);
        }
        if(dX>=648){
          viewHolder.itemView.findViewById(R.id.stamp_left).setAlpha(1f);
        }
      }
      else if(dX<0) {
        viewHolder.itemView.findViewById(R.id.stamp_right)
            .setVisibility(View.VISIBLE);
        if(dX>-72){
          viewHolder.itemView.findViewById(R.id.stamp_right).setAlpha(0.1f);
          if(viewHolder instanceof Game1ViewHolder || viewHolder instanceof Game2ViewHolder){
            viewHolder.itemView.findViewById(R.id.right_answer).setAlpha(0.5f);
            viewHolder.itemView.findViewById(R.id.left_answer).setAlpha(0.5f);
          }
          else{
            viewHolder.itemView.findViewById(R.id.game_card_question3_answer_left).setAlpha(0.5f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_icon_left).setAlpha(0.5f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_answer_right).setAlpha(0.5f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_icon_right).setAlpha(0.5f);
          }
        }
        if(dX<=-72 && dX>-144){
          viewHolder.itemView.findViewById(R.id.stamp_right).setAlpha(0.2f);
          if(viewHolder instanceof Game1ViewHolder || viewHolder instanceof Game2ViewHolder){
            viewHolder.itemView.findViewById(R.id.right_answer).setAlpha(0.3f);
            viewHolder.itemView.findViewById(R.id.left_answer).setAlpha(0.3f);
          }
          else{
            viewHolder.itemView.findViewById(R.id.game_card_question3_answer_left).setAlpha(0.3f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_icon_left).setAlpha(0.3f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_answer_right).setAlpha(0.3f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_icon_right).setAlpha(0.3f);
          }
        }
        if(dX<=-144 && dX>-216){
          viewHolder.itemView.findViewById(R.id.stamp_right).setAlpha(0.3f);
          if(viewHolder instanceof Game1ViewHolder || viewHolder instanceof Game2ViewHolder){
            viewHolder.itemView.findViewById(R.id.right_answer).setAlpha(0f);
            viewHolder.itemView.findViewById(R.id.left_answer).setAlpha(0f);
          }
          else{
            viewHolder.itemView.findViewById(R.id.game_card_question3_answer_left).setAlpha(0f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_icon_left).setAlpha(0f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_answer_right).setAlpha(0f);
            viewHolder.itemView.findViewById(R.id.game_card_question3_icon_right).setAlpha(0f);
          }
        }
        if(dX<=-216 && dX>-288){
          viewHolder.itemView.findViewById(R.id.stamp_right).setAlpha(0.4f);
        }
        if(dX<=-288 && dX>-360) {
          viewHolder.itemView.findViewById(R.id.stamp_right).setAlpha(0.5f);
        }
        if(dX<=-432 && dX>-504){
          viewHolder.itemView.findViewById(R.id.stamp_right).setAlpha(0.7f);
        }
        if(dX<=-504 && dX>-576){
          viewHolder.itemView.findViewById(R.id.stamp_right).setAlpha(0.8f);
        }
        if(dX<=-576 && dX>-648){
          viewHolder.itemView.findViewById(R.id.stamp_right).setAlpha(0.9f);
        }
        if(dX<=-648){
          viewHolder.itemView.findViewById(R.id.stamp_right).setAlpha(1f);}
      }
      else{
        viewHolder.itemView.findViewById(R.id.stamp_right).setVisibility(View.INVISIBLE);
        viewHolder.itemView.findViewById(R.id.stamp_left).setVisibility(View.INVISIBLE);

      }
    }

    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
  }

  @Override public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    viewHolder.itemView.findViewById(R.id.stamp_left).setVisibility(View.INVISIBLE);
    viewHolder.itemView.findViewById(R.id.stamp_right).setVisibility(View.INVISIBLE);

    if(viewHolder instanceof Game1ViewHolder || viewHolder instanceof Game2ViewHolder){
      viewHolder.itemView.findViewById(R.id.right_answer).setAlpha(1f);
      viewHolder.itemView.findViewById(R.id.left_answer).setAlpha(1f);
    }
    else{
      viewHolder.itemView.findViewById(R.id.game_card_question3_answer_left).setAlpha(1f);
      viewHolder.itemView.findViewById(R.id.game_card_question3_icon_left).setAlpha(1f);
      viewHolder.itemView.findViewById(R.id.game_card_question3_answer_right).setAlpha(1f);
      viewHolder.itemView.findViewById(R.id.game_card_question3_icon_right).setAlpha(1f);
    }

    super.clearView(recyclerView, viewHolder);
  }

  @Override
  public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

    int position = viewHolder.getAdapterPosition();

    if(viewHolder instanceof Game1ViewHolder){
      Game1ViewHolder view = (Game1ViewHolder) viewHolder;
      if(swipeDir == ItemTouchHelper.LEFT) {
        view.onPostDismissedLeft(view.getCard(), position);
      }
      else if(swipeDir == ItemTouchHelper.RIGHT){
        view.onPostDismissedRight(view.getCard(), position);
      }
    }
    if (viewHolder instanceof Game2ViewHolder){
      Game2ViewHolder view = (Game2ViewHolder) viewHolder;
      if(swipeDir == ItemTouchHelper.LEFT) {
        view.onPostDismissedLeft(view.getCard(), position);
      }
      else if(swipeDir == ItemTouchHelper.RIGHT){
        view.onPostDismissedRight(view.getCard(), position);
      }
    }
    if (viewHolder instanceof Game3ViewHolder){
      Game3ViewHolder view = (Game3ViewHolder) viewHolder;
      if(swipeDir == ItemTouchHelper.LEFT) {
        view.onPostDismissedLeft(view.getCard(), position);
      }
      else if(swipeDir == ItemTouchHelper.RIGHT){
        view.onPostDismissedRight(view.getCard(), position);
      }
    }
  }
}

package cm.aptoide.pt.feature_reactions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import cm.aptoide.pt.feature_reactions.data.TopReaction;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import java.util.List;
import timber.log.Timber;

import static cm.aptoide.pt.feature_reactions.ReactionMapper.mapReaction;

public class TopReactionsPreview {

  private ImageView firstReaction;
  private ImageView secondReaction;
  private ImageView thirdReaction;
  private TextView numberOfReactions;
  private ImageView[] imageView;

  public TopReactionsPreview() {

  }

  public void initialReactionsSetup(View view) {
    firstReaction = view.findViewById(R.id.reaction_1);
    secondReaction = view.findViewById(R.id.reaction_2);
    thirdReaction = view.findViewById(R.id.reaction_3);
    numberOfReactions = view.findViewById(R.id.number_of_reactions);
    imageView = new ImageView[] { firstReaction, secondReaction, thirdReaction };
  }

  public void setReactions(List<TopReaction> reactions, int numberOfReactions, Context context) {
    ImageView[] imageViews = { firstReaction, secondReaction, thirdReaction };
    int validReactions = 0;
    for (int i = 0; i < imageViews.length; i++) {
      if (i < reactions.size() && isReactionValid(reactions.get(i)
          .getType())) {
        loadWithShadowCircleTransform(mapReaction(reactions.get(i)
            .getType()), imageViews[i], context);
        imageViews[i].setVisibility(View.VISIBLE);
        validReactions++;
      } else {
        imageViews[i].setVisibility(View.GONE);
      }
    }
    if (numberOfReactions > 0 && validReactions > 0) {
      this.numberOfReactions.setText(String.valueOf(numberOfReactions));
      this.numberOfReactions.setVisibility(View.VISIBLE);
    } else {
      this.numberOfReactions.setVisibility(View.GONE);
    }
  }

  public Target<Drawable> loadWithShadowCircleTransform(@DrawableRes int drawableId,
      ImageView imageView, Context context) {
    if (context != null) {
      return Glide.with(context)
          .load(drawableId)
          .apply(getRequestOptions().transform(new ShadowCircleTransformation(context, imageView)))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Timber.d("::loadWithShadowCircleTransform() Context is null");
    }
    return null;
  }

  @SuppressLint("CheckResult") @NonNull private RequestOptions getRequestOptions() {
    RequestOptions requestOptions = new RequestOptions();
    DecodeFormat decodeFormat;
    if (Build.VERSION.SDK_INT >= 26) {
      decodeFormat = DecodeFormat.PREFER_ARGB_8888;
      requestOptions.disallowHardwareConfig();
    } else {
      decodeFormat = DecodeFormat.PREFER_RGB_565;
    }
    return requestOptions.format(decodeFormat)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
  }

  public boolean isReactionValid(String reaction) {
    return !reaction.equals("") && mapReaction(reaction) != -1;
  }

  public void clearReactions() {
    for (ImageView imageView : imageView) {
      imageView.setVisibility(View.GONE);
    }
    this.numberOfReactions.setVisibility(View.GONE);
  }

  public void onDestroy() {
    firstReaction = null;
    secondReaction = null;
    thirdReaction = null;
    numberOfReactions = null;
  }
}

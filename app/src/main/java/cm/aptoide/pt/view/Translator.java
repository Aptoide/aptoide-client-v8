package cm.aptoide.pt.view;

import android.content.Context;
import cm.aptoide.pt.R;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;

/**
 * Created by pedroribeiro on 02/05/16.
 *
 * <p>
 * This class is deprecated since it is a bad practice and we need to stop using this. Only use this
 * in last resort.
 * </p>
 */
@Deprecated public final class Translator {

  public static String translate(String string, Context context) {
    if (string == null) {
      return string;
    }
    String translated = null;
    switch (string) {
      case "Home":
        translated = context.getString(R.string.home_title);
        break;
      case "Updates":
        translated = context.getString(R.string.updates);
        break;
      case "Stores":
        translated = context.getString(R.string.stores);
        break;
      case "Downloads":
        translated = context.getString(R.string.downloads);
        break;
      case "Community":
        translated = context.getString(R.string.community);
        break;
      case "Apps Timeline":
        translated = context.getString(R.string.apps_timeline);
        break;
      case "Timeline":
        translated = context.getString(R.string.timeline);
        break;
      case "Latest Reviews":
        translated = context.getString(R.string.latest_reviews);
        break;
      case "Latest Comments":
        translated = context.getString(R.string.latest_comments);
        break;
      case "Applications":
        translated = context.getString(R.string.applications);
        break;
      case "Games":
        translated = context.getString(R.string.games);
        break;
      case "Highlighted":
        translated = context.getString(R.string.highlighted);
        break;
      case "Trending":
        translated = context.getString(R.string.trending);
        break;
      case "Local Top Apps":
        translated = context.getString(R.string.local_top_apps);
        break;
      case "Top Stores":
        translated = context.getString(R.string.top_stores_fragment_title);
        break;
      case "Top Games":
        translated = context.getString(R.string.top_games);
        break;
      case "Reviews":
        translated = context.getString(R.string.reviews);
        break;
      case "News & Weather":
        translated = context.getString(R.string.news_weather);
        break;
      case "Productivity":
        translated = context.getString(R.string.productivity);
        break;
      case "News & Magazines":
        translated = context.getString(R.string.news_magazines);
        break;
      case "Reference":
        translated = context.getString(R.string.reference);
        break;
      case "Shopping":
        translated = context.getString(R.string.shopping);
        break;
      case "Social":
        translated = context.getString(R.string.social);
        break;
      case "Business":
        translated = context.getString(R.string.business);
        break;
      case "Sports":
        translated = context.getString(R.string.sports);
        break;
      case "Themes":
        translated = context.getString(R.string.themes);
        break;
      case "Tools":
        translated = context.getString(R.string.tools);
        break;
      case "Travel":
        translated = context.getString(R.string.travel);
        break;
      case "Software Libraries":
        translated = context.getString(R.string.software_libraries);
        break;
      case "Demo":
        translated = context.getString(R.string.demo);
        break;
      case "Comics":
        translated = context.getString(R.string.comics);
        break;
      case "Music & Audio":
        translated = context.getString(R.string.music_audio);
        break;
      case "Weather":
        translated = context.getString(R.string.weather);
        break;
      case "Photography":
        translated = context.getString(R.string.photography);
        break;
      case "Communication":
        translated = context.getString(R.string.communication);
        break;
      case "Personalization":
        translated = context.getString(R.string.personalization);
        break;
      case "Travel & Local":
        translated = context.getString(R.string.travel_local);
        break;
      case "Transportation":
        translated = context.getString(R.string.transportation);
        break;
      case "Medical":
        translated = context.getString(R.string.medical);
        break;
      case "Entertainment":
        translated = context.getString(R.string.entertainment);
        break;
      case "Finance":
        translated = context.getString(R.string.finance);
        break;
      case "Health":
        translated = context.getString(R.string.health);
        break;
      case "Libraries & Demo":
        translated = context.getString(R.string.libraries_demo);
        break;
      case "Books & Reference":
        translated = context.getString(R.string.books_reference);
        break;
      case "Lifestyle":
        translated = context.getString(R.string.lifestyle);
        break;
      case "Transport":
        translated = context.getString(R.string.transport);
        break;
      case "Health & Fitness":
        translated = context.getString(R.string.health_fitness);
        break;
      case "Media & Video":
        translated = context.getString(R.string.media_video);
        break;
      case "Multimedia":
        translated = context.getString(R.string.multimedia);
        break;
      case "Education":
        translated = context.getString(R.string.education);
        break;
      case "All":
        translated = context.getString(R.string.all);
        break;
      case "Puzzle":
        translated = context.getString(R.string.puzzle);
        break;
      case "Casino":
        translated = context.getString(R.string.casino);
        break;
      case "Action":
        translated = context.getString(R.string.action);
        break;
      case "Strategy":
        translated = context.getString(R.string.strategy);
        break;
      case "Family":
        translated = context.getString(R.string.family);
        break;
      case "Simulation":
        translated = context.getString(R.string.simulation);
        break;
      case "Adventure":
        translated = context.getString(R.string.adventure);
        break;
      case "Word":
        translated = context.getString(R.string.word);
        break;
      case "Arcade":
        translated = context.getString(R.string.arcade);
        break;
      case "Arcade & Action":
        translated = context.getString(R.string.arcade_action);
        break;
      case "Trivia":
        translated = context.getString(R.string.trivia);
        break;
      case "Card":
        translated = context.getString(R.string.card);
        break;
      case "Role Playing":
        translated = context.getString(R.string.role_playing);
        break;
      case "Educational":
        translated = context.getString(R.string.educational);
        break;
      case "Music":
        translated = context.getString(R.string.music);
        break;
      case "Board":
        translated = context.getString(R.string.board);
        break;
      case "Brain & Puzzle":
        translated = context.getString(R.string.brain_puzzle);
        break;
      case "Cards & Casino":
        translated = context.getString(R.string.cards_casino);
        break;
      case "Casual":
        translated = context.getString(R.string.casual);
        break;
      case "Sports Games":
        translated = context.getString(R.string.sports_games);
        break;
      case "Racing":
        translated = context.getString(R.string.racing);
        break;
      case "Top Apps":
        translated = context.getString(R.string.top_apps);
        break;
      case "Latest Apps":
      case "Latest Applications":
        translated = context.getString(R.string.latest_applications);
        break;
      case "Top Apps in this store":
        translated = context.getString(R.string.top_apps_in_store);
        break;
      case "Apps for Kids":
        translated = context.getString(R.string.apps_for_kids);
        break;
      case "Aptoide Publishers":
        translated = AptoideUtils.StringU.getFormattedString(R.string.aptoide_publishers,
            context.getResources(), Application.getConfiguration()
                .getMarketName());
        break;
      case "Music & Video":
        translated = context.getString(R.string.music_video);
        break;
      case "Essential Apps":
        translated = context.getString(R.string.essential_apps);
        break;
      case "Summer Apps":
        translated = context.getString(R.string.summer_apps);
        break;
      case "Play-it!":
        translated = context.getString(R.string.title_play_it);
        break;
      case "More Editors Choice":
      case "More Editor's Choice":
      case "More Editors' Choice":
        translated = context.getString(R.string.more_editors_choice);
        break;
      case "Comments in this store":
      case "Comments on this store":
        translated = context.getString(R.string.comment_store_title);
        break;
      case "Spot & Share":
      case "Spot&Share":
        translated = context.getString(R.string.spot_share);
        break;
      case "Followed Stores":
        translated = context.getString(R.string.followed_stores);
        break;
      case "Recommended Stores":
        translated = context.getString(R.string.recommended_stores);
        break;
      case "Featured Stores":
        translated = context.getString(R.string.featured_stores);
        break;
      default:
        translated = string;
        break;
    }
    return translated;
  }

  public static String[] translateToMultiple(String string, Context context) {

    String[] result = null;

    if (string == null) {
      return result;
    }

    switch (string) {
      case "Your store doesn't have any applications yet. Install Aptoide Uploader and upload apps to share them with the world!":
      case "Your store does not have any applications yet. Install Aptoide Uploader and upload apps to share them with the world!":
        result = new String[4];
        result[0] = context.getString(R.string.install_app_outter_pt1);
        result[1] = context.getString(R.string.install_app_outter_pt2);
        result[2] = context.getString(R.string.install_app_inner);
        result[3] = context.getString(R.string.open_app_inner);
        break;

      default:
        break;
    }

    return result;
  }
}

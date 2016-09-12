package cm.aptoide.pt.v8engine.util;

import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by pedroribeiro on 02/05/16.
 */
public class Translator {

	public static String translate(String string) {
		if (string == null) {
			return string;
		}
		String translated = null;
		switch (string) {
			case "Home":
				translated = Application.getContext().getString(R.string.home_title);
				break;
			case "Updates":
				translated = Application.getContext().getString(R.string.updates);
				break;
			case "Stores":
				translated = Application.getContext().getString(R.string.stores);
				break;
			case "Downloads":
				translated = Application.getContext().getString(R.string.downloads);
				break;
			case "Community":
				translated = Application.getContext().getString(R.string.community);
				break;
			case "Latest Reviews":
				translated = Application.getContext().getString(R.string.latest_reviews);
				break;
			case "Latest Comments":
				translated = Application.getContext().getString(R.string.latest_comments);
				break;
			case "Applications":
				translated = Application.getContext().getString(R.string.applications);
				break;
			case "Games":
				translated = Application.getContext().getString(R.string.games);
				break;
			case "Highlighted":
				translated = Application.getContext().getString(R.string.highlighted);
				break;
			case "Trending":
				translated = Application.getContext().getString(R.string.trending);
				break;
			case "Local Top Apps":
				translated = Application.getContext().getString(R.string.local_top_apps);
				break;
			case "Top Stores":
				translated = Application.getContext().getString(R.string.top_stores_fragment_title);
				break;
			case "Top Games":
				translated = Application.getContext().getString(R.string.top_games);
				break;
			case "Reviews":
				translated = Application.getContext().getString(R.string.reviews);
				break;
			case "News & Weather":
				translated = Application.getContext().getString(R.string.news_weather);
				break;
			case "Productivity":
				translated = Application.getContext().getString(R.string.productivity);
				break;
			case "News & Magazines":
				translated = Application.getContext().getString(R.string.news_magazines);
				break;
			case "Reference":
				translated = Application.getContext().getString(R.string.reference);
				break;
			case "Shopping":
				translated = Application.getContext().getString(R.string.shopping);
				break;
			case "Social":
				translated = Application.getContext().getString(R.string.social);
				break;
			case "Business":
				translated = Application.getContext().getString(R.string.business);
				break;
			case "Sports":
				translated = Application.getContext().getString(R.string.sports);
				break;
			case "Themes":
				translated = Application.getContext().getString(R.string.themes);
				break;
			case "Tools":
				translated = Application.getContext().getString(R.string.tools);
				break;
			case "Travel":
				translated = Application.getContext().getString(R.string.travel);
				break;
			case "Software Libraries":
				translated = Application.getContext().getString(R.string.software_libraries);
				break;
			case "Demo":
				translated = Application.getContext().getString(R.string.demo);
				break;
			case "Comics":
				translated = Application.getContext().getString(R.string.comics);
				break;
			case "Music & Audio":
				translated = Application.getContext().getString(R.string.music_audio);
				break;
			case "Weather":
				translated = Application.getContext().getString(R.string.weather);
				break;
			case "Photography":
				translated = Application.getContext().getString(R.string.photography);
				break;
			case "Communication":
				translated = Application.getContext().getString(R.string.communication);
				break;
			case "Personalization":
				translated = Application.getContext().getString(R.string.personalization);
				break;
			case "Travel & Local":
				translated = Application.getContext().getString(R.string.travel_local);
				break;
			case "Transportation":
				translated = Application.getContext().getString(R.string.transportation);
				break;
			case "Medical":
				translated = Application.getContext().getString(R.string.medical);
				break;
			case "Entertainment":
				translated = Application.getContext().getString(R.string.entertainment);
				break;
			case "Finance":
				translated = Application.getContext().getString(R.string.finance);
				break;
			case "Health":
				translated = Application.getContext().getString(R.string.health);
				break;
			case "Libraries & Demo":
				translated = Application.getContext().getString(R.string.libraries_demo);
				break;
			case "Books & Reference":
				translated = Application.getContext().getString(R.string.books_reference);
				break;
			case "Lifestyle":
				translated = Application.getContext().getString(R.string.lifestyle);
				break;
			case "Transport":
				translated = Application.getContext().getString(R.string.transport);
				break;
			case "Health & Fitness":
				translated = Application.getContext().getString(R.string.health_fitness);
				break;
			case "Media & Video":
				translated = Application.getContext().getString(R.string.media_video);
				break;
			case "Multimedia":
				translated = Application.getContext().getString(R.string.multimedia);
				break;
			case "Education":
				translated = Application.getContext().getString(R.string.education);
				break;
			case "All":
				translated = Application.getContext().getString(R.string.all);
				break;
			case "Puzzle":
				translated = Application.getContext().getString(R.string.puzzle);
				break;
			case "Casino":
				translated = Application.getContext().getString(R.string.casino);
				break;
			case "Action":
				translated = Application.getContext().getString(R.string.action);
				break;
			case "Strategy":
				translated = Application.getContext().getString(R.string.strategy);
				break;
			case "Family":
				translated = Application.getContext().getString(R.string.family);
				break;
			case "Simulation":
				translated = Application.getContext().getString(R.string.simulation);
				break;
			case "Adventure":
				translated = Application.getContext().getString(R.string.adventure);
				break;
			case "Word":
				translated = Application.getContext().getString(R.string.word);
				break;
			case "Arcade":
				translated = Application.getContext().getString(R.string.arcade);
				break;
			case "Arcade & Action":
				translated = Application.getContext().getString(R.string.arcade_action);
				break;
			case "Trivia":
				translated = Application.getContext().getString(R.string.trivia);
				break;
			case "Card":
				translated = Application.getContext().getString(R.string.card);
				break;
			case "Role Playing":
				translated = Application.getContext().getString(R.string.role_playing);
				break;
			case "Educational":
				translated = Application.getContext().getString(R.string.educational);
				break;
			case "Music":
				translated = Application.getContext().getString(R.string.music);
				break;
			case "Board":
				translated = Application.getContext().getString(R.string.board);
				break;
			case "Brain & Puzzle":
				translated = Application.getContext().getString(R.string.brain_puzzle);
				break;
			case "Cards & Casino":
				translated = Application.getContext().getString(R.string.cards_casino);
				break;
			case "Casual":
				translated = Application.getContext().getString(R.string.casual);
				break;
			case "Sports Games":
				translated = Application.getContext().getString(R.string.sports_games);
				break;
			case "Racing":
				translated = Application.getContext().getString(R.string.racing);
				break;
			case "Top Apps":
				translated = Application.getContext().getString(R.string.top_apps);
				break;
			case "Latest Applications":
				translated = Application.getContext().getString(R.string.latest_applications);
				break;
			case "Top Apps in this store":
				translated = Application.getContext().getString(R.string.top_apps_in_store);
				break;
			case "Apps for Kids":
				translated = Application.getContext().getString(R.string.apps_for_kids);
				break;
			case "Aptoide Publishers":
				translated = Application.getContext().getString(R.string.aptoide_publishers);
				break;
			case "Music & Video":
				translated = Application.getContext().getString(R.string.music_video);
				break;
			case "Essential Apps":
				translated = Application.getContext().getString(R.string.essential_apps);
				break;
			case "Summer Apps":
				translated = Application.getContext().getString(R.string.summer_apps);
				break;
			case "Play-it!":
				translated = Application.getContext().getString(R.string.play_it);
				break;
			case "More Editors Choice":
				translated = Application.getContext().getString(R.string.more_editors_choice);
				break;
			case "More Editor's Choice":
				translated = Application.getContext().getString(R.string.more_editors_choice);
				break;
			default:
				translated = string;
				break;
		}
		return translated;
	}
}

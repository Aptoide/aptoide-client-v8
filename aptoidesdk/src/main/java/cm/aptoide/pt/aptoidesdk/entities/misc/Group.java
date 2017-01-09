package cm.aptoide.pt.aptoidesdk.entities.misc;

import lombok.Getter;

/**
 * Created by neuro on 09-01-2017.
 */

public enum Group {
  GAMES("games", 2),
  APPLICATIONS("applications", 1),
  BEAUTY("beauty", 8874),
  ART_DESIGN("art-design", 8871),
  DATING("dating", 8869),
  PARENTING("parenting", 8868),
  EVENTS("events", 8867),
  HOUSE_HOME("house-home", 8866),
  FOOD_DRINK("food-drink", 8861),
  AUTO_VEHICLES("auto-vehicles", 8859),
  MAPS_NAVIGATION("maps-navigation", 8792),
  VIDEO_PLAYERS_EDITORS("video-players-editors", 8784),
  BOARD("board", 1697),
  MUSIC("music", 1696),
  EDUCATIONAL("educational", 1695),
  ROLE_PLAYING("role-playing", 1693),
  CARD("card", 1692),
  TRIVIA("trivia", 1691),
  ARCADE("arcade", 1690),
  WORD("word", 1677),
  ADVENTURE("adventure", 1675),
  SIMULATION("simulation", 1674),
  FAMILY("family", 1672),
  STRATEGY("strategy", 1671),
  ACTION("action", 1670),
  CASINO("casino", 1669),
  PUZZLE("puzzle", 1668),
  TRANSPORT("transport", 850),
  LIBRARIES_DEMO("libraries-demo", 736),
  MEDICAL("medical", 459),
  TRANSPORTATION("transportation", 418),
  TRAVEL_LOCAL("travel-local", 415),
  WEATHER("weather", 310),
  SPORTS_GAMES("sports-games", 293),
  BUSINESS("business", 149),
  EDUCATION("education", 95),
  MEDIA_VIDEO("media-video", 89),
  HEALTH_FITNESS("health-fitness", 86),
  BOOKS_REFERENCE("books-reference", 78),
  RACING("racing", 47),
  PERSONALIZATION("personalization", 40),
  PHOTOGRAPHY("photography", 39),
  MUSIC_AUDIO("music-audio", 31),
  NEWS_MAGAZINES("news-magazines", 26),
  CASUAL("casual", 24),
  CARDS_CASINO("cards-casino", 23),
  BRAIN_PUZZLE("brain-puzzle", 22),
  ARCADE_ACTION("arcade-action", 21),
  SOFTWARE_LIBRARIES("software-libraries", 20),
  DEMO("demo", 19),
  TRAVEL("travel", 18),
  TOOLS("tools", 17),
  THEMES("themes", 16),
  SPORTS("sports", 15),
  SOCIAL("social", 14),
  SHOPPING("shopping", 13),
  REFERENCE("reference", 12),
  PRODUCTIVITY("productivity", 11),
  NEWS_WEATHER("news-weather", 10),
  MULTIMEDIA("multimedia", 9),
  LIFESTYLE("lifestyle", 8),
  HEALTH("health", 7),
  FINANCE("finance", 6),
  ENTERTAINMENT("entertainment", 5),
  COMMUNICATION("communication", 4),
  COMICS("comics", 3)
  ;

  @Getter private final String name;
  @Getter private final int id;

  Group(String name, int id) {
    this.name = name;
    this.id = id;
  }
}

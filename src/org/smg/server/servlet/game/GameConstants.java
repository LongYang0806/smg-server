package org.smg.server.servlet.game;

public final class GameConstants {
	  private GameConstants() { }  // Prevent instantiation/subclassing
	
	  // Game entity
	  public static final String SCREENSHOT = "screenshot";
	  public static final String ICON = "icon";
	  public static final String GAME_NAME = "gameName";
	  public static final String DESCRIPTION = "description";
	  public static final String URL = "url";
	  public static final String WIDTH = "width";
	  public static final String HEIGHT = "height";
	  public static final String GAME_ID = "gamdId" ;
	  public static final String HAS_TOKENS = "hasTokens";
	  public static final String POST_DATE = "postDate";
	  public static final String PLAYER_ID = "playerId";
	  
	  
	  
	  
	  
	  
	  
	  // Error messages
	  static final String GAME_EXISTS = "GAME_EXISTS";
	  static final String WRONG_GAME_ID = "WRONG_GAME_ID";
	  static final String URL_ERROR = "INVALID_URL_PATH_ERROR";
	  static final String WRONG_PLAYER_ID = "WRONG_PLAYER_ID";
	  static final String WRONG_RATING = "WRONG_RATING";
	  // Success messages
	  
	  static final String DELETED_GAME = "DELETED_GAME";
	  static final String UPDATED_GAME = "UPDATED_GAME";
	  static final String ALL="all";
	  static final String STATS = "stats";
	  static final String CURRENT_GAMES = "CURRENT_GAMES";
	  static final String RATING = "ratings";
	  
	  // Invalid indicator
	  static final int INVALID = -1;
	  
	  
	  public static final String GAME_META_INFO = "gameMetaInfo";
	  public static final String VERSION_ONE = "versionOne" ; 
	}

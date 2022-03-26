
/*
Game Attributes - hard coded values used in the game
 */

final class GameAttributes
{
   public static final int NUM_SKELETONS = 3;
   public static final int NUM_BATS = 3;
   public static final int NUM_BARRELS = 3;
   public static final int NUM_TORCHES = 3;

   public static final String PLAYER_KEY = "player";
   public static final int PLAYER_NUM_PROPERTIES = 5;
   public static final int PLAYER_ID = 1;
   public static final int PLAYER_COL = 2;
   public static final int PLAYER_ROW = 3;
   public static final int PLAYER_ANIMATION_PERIOD = 4;

   public static Player player; // player is initialized once images are loaded
   public static int PLAYER_HP = 3;

   public static final String SKELETON_KEY = "skeleton";
   public static final int SKELETON_NUM_PROPERTIES = 6;
   public static final int SKELETON_ID = 1;
   public static final int SKELETON_COL = 2;
   public static final int SKELETON_ROW = 3;
   public static final int SKELETON_ACTION_PERIOD = 4;
   public static final int SKELETON_ANIMATION_PERIOD = 5;

   public static final String BAT_KEY = "bat";
   public static final int BAT_NUM_PROPERTIES = 6;
   public static final int BAT_ID = 1;
   public static final int BAT_COL = 2;
   public static final int BAT_ROW = 3;
   public static final int BAT_ACTION_PERIOD = 4;
   public static final int BAT_ANIMATION_PERIOD = 5;

   public static final String SPACE_KEY = "space";
   public static final int SPACE_NUM_PROPERTIES = 4;
   public static final int SPACE_ID = 1;
   public static final int SPACE_COL = 2;
   public static final int SPACE_ROW = 3;

   public static final String TORCH_KEY = "torch";
   public static final int TORCH_NUM_PROPERTIES = 5;
   public static final int TORCH_ID = 1;
   public static final int TORCH_COL = 2;
   public static final int TORCH_ROW = 3;
   public static final int TORCH_ANIMATION_PERIOD = 4;

   public static final String BARREL_KEY = "barrel";
   public static final int BARREL_NUM_PROPERTIES = 4;
   public static final int BARREL_ID = 1;
   public static final int BARREL_COL = 2;
   public static final int BARREL_ROW = 3;

   public static final int BARREL_FIRE_COUNT = 3;
   public static final String FIRE_KEY = "fire";
   public static final String FIRE_ID_PREFIX = "fire";

   public static final String BGND_KEY = "background";
   public static final int BGND_NUM_PROPERTIES = 4;
   public static final int BGND_ID = 1;
   public static final int BGND_COL = 2;
   public static final int BGND_ROW = 3;

   public static final int COLOR_MASK = 0xffffff;
   public static final int KEYED_IMAGE_MIN = 5;
   public static final int KEYED_RED_IDX = 2;
   public static final int KEYED_GREEN_IDX = 3;
   public static final int KEYED_BLUE_IDX = 4;

   public static final int PROPERTY_KEY = 0;
}

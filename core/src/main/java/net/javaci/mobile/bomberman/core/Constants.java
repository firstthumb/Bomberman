package net.javaci.mobile.bomberman.core;


public class Constants {

    public static final String DEFAULT_LANGUAGE_KEYS[] = {"en", "de", "es", "fr","tr","it","pt"};

    public static final String ANDROID_DEVICE_TYPE = "1";
    public static final String PREF_KEY_GUEST_USER_ID = "heartsplus_guest_user_id";
    public static final String DEFAULT_GUEST_USERID = "0";
    public static final String GUEST_USER_ID_PREFIX = "99999";

    public static final String COMMON_ATLAS = "Common.atlas";
    public static final String GAMESCREEN_ATLAS = "GameScreen.atlas";
    public static final String MENU_ATLAS = "MenuScreen.atlas";
    public static final String INBOX_ATLAS = "InboxScreen.atlas";
    public static final String LOBBY_ATLAS = "LobbyScreen.atlas";
    public static final String VIP_ATLAS = "VIPScreen.atlas";
    public static final String TOP25_ATLAS = "Top25Screen.atlas";
    public static final String POPUP_ATLAS = "Popup.atlas";
    public static final String LOADING_BAR_ATLAS = "LoadingBar.atlas";
    public static final String BUY_CHIPS_ATLAS = "BuyChipsScreen.atlas";
    public static final String HELP_ATLAS = "HelpScreen.atlas";
    public static final String SKIN_ATLAS = "uiskin.atlas";

    public static final String ENABLED_TAB_FONT = "26pt_blue.fnt";
    public static final String DISABLED_TAB_FONT = "26pt_gray.fnt";
    public static final String SESSION_LOGS_PATH = "heartsplus";
    public static final String SESSION_LOG_SUFFIX = "hp";

    public static final String FACEBOOK_APP_PRODUCTION  = "186613301510488";
    public static final String FACEBOOK_APP_TEST        = "162163530635429";

    public static final String FONT_20 = "20pt.fnt";
    public static final String FONT_23 = "23pt.fnt";
    public static final String FONT_26 = "26pt.fnt";
    public static final String FONT_26_BLACK = "26pt_black.fnt";
    public static final String FONT_26_BLUE = "26pt_blue.fnt";
    public static final String FONT_26_GREEN = "26pt_green.fnt";
    public static final String FONT_26_GRAY = "26pt_gray.fnt";
    public static final String FONT_26_HOUSECHKA = "26pt_houschka.fnt";
    public static final String FONT_30 = "30pt.fnt";
    public static final String FONT_34 = "34pt.fnt";
    public static final String FONT_36 = "36pt.fnt";
    public static final String FONT_36_HOUSECHKA = "36pt_houschka.fnt";
    public static final String FONT_40_TITLE = "40pt_title.fnt";
    
    public static final String LOADING_WIDGET_BACKGROUND = "loading_back";
    public static final String LOADING_WIDGET_BAR = "loading_bar";
    public static final long LOADING_WIDGET_TIMEOUT  = 15000;

    public static final int PASS_CARDS_COUNT_DOWN = 2;
    public static final int THROW_CARD_COUNT_DOWN = 3;
    public static final int GAME_END = 4;


    public static final int TABLE_REFRESH_RATE = 10;
    public static final String BASE_64_PUBLIC_KEY = "OKKDKlCPDimsjmkI1y2DCSGHCCQECS0COKKDEiMECSGCjOieZYz4xaLsGaueEq+lBYMplo1XOireD9bvilN8gh1w0YuJQ1nIZp297kOlWtXnPGFH3ALqHFVnKzGoSuK/yQKfYHTZ/aOwLA14JLQ7lorIeTY5vmtzBRWnNlINlRsd1NMO6xNB1kwVbSpf3W8fZGz0gpkcNBiotHXUd2iZ/zwM5sKExTKOfODrlztusrUzIRJZ9qxM1psRXeC0H2pbMOMOTrS2Ed2ZH46bkCeczmCmNo3XOItpNENEW/4OfCqd7MBm/sRgEC6+ZF0djIdEoNBI+2oiHiLA2VVUcI7tKYpmiwa9Sag51wCLFAZF3PEQCl8/YLAffESgOOwEsl/jmSKFCSCD/DwCNu2MkqaomykU+jycQTvAn8S8Prieudo894wVnEcxIJGORVKWfw7SANievwxO3fyhEzMASdTynQ7MyFvzgJB0r5/LvoSa14YVaopng4rGpB1GIZk/mmMmedCnll2M7AWYeEo4bM25ssZxIebxZEz+gB1WoiUIBHmyKhWSKFCSCD";
    public static final String PEAKPAY_BASE_URL = "Zafer";
    public static final String PEAKPAY_APP_ID = "Zafer";
    public static final String PEAKPAY_SECRET = "Zafer";
    public static final String SHADED_CARD = "shadedCard";
    public static final String PROFILE_PICTURE_ACTOR_NAME_PREFIX = "hp_profile_picture_";
    public static final String POPUP_PROFILE_PICTURE_ACTOR_NAME_PREFIX = "hp_popup_profile_picture_";
    public static final String PLAYER_PROFILE_PICTURE_ACTOR_NAME_PREFIX = "hp_player_profile_picture_";
    public static final Object SCOREBOARD_PROFILE_PICTURE_ACTOR_NAME_PREFIX = "hp_scoreboard_profile_picture";
    public static final String DIRECTION_NONE = "NONE";
    public static final String VIP_TYPE = "vipType";
    public static final String JOINED_FRIEND_UID = "joinedFriendUid";
    public static final String IS_VIP = "isVip";
    public static final String NOT_VIP = "notVip";
    public static final long TABLE_PLAYER_COUNT = 4; //TODO 4 kisiden az olabilen odalar icin farkli cozum dusunelim.. response ile oyuncu sayisi gelecektir muhtemelen
    public static final int MAX_HAND_SIZE = 13; //TODO 4 kisiden az olursa hesaplanmasi lazim
    public static final int MAX_ROUND_PENALTY = 26; //Toplam ceza degisirse degismeli
    public static final int LEAVE_TABLE_NOT_ENOUGH_CHIPS = 1;

    public static final int LEAVE_TABLE_TYPE_AUTOKICK = 3;

    public static final String HEARTS_PLUS_APP_ADDRESS = "http://apps.facebook.com/heartsplus/";
    public static final int GIFT_CHIP_AMOUNT = 250;
    public static final int SERVER_STATE_PASS_CARDS = 2;
    public static final int MAX_ROUND_COUNT = 20;
    public static final int ROUND_RESULT_DISPLAY_TIME = 6;
    public static final int VIP_UNLOCK_LEVEL = 15;

    public static final String UI_SESSION_NAME = "session";
    public static final int FISH_ROOM_INDEX = 0;
    public static final int ALL_STAR_ROOM_INDEX = 9;
}

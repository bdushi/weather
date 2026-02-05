package obg.android.feature.analytics

object OBGAnalyticsAction {

    /** Explore **/
    const val OBG_ACTION_GAME_SORT = "game_sort"
    const val OBG_ACTION_RECENT_SEARCHES = "recent_searches"
    const val OBG_ACTION_APPLY_FILTER = "game_filter"

    /** Home **/
    const val OBG_ACTION_HOME_SELECT_ITEM = "home_select_item"
    const val OBG_ACTION_HOME_DEEP_LINK_CATEGORY = "deep_link_game_categories"
    const val OBG_ACTION_HOME_DEEP_LINK_MORE = "deep_link_more"
    const val OBG_ACTION_HOME_DEEP_LINK_URL = "deep_link_url"
    const val OBG_ACTION_HOME_OPEN_GAME = "home_open_game"
    const val OBG_ACTION_HOME_OPEN_CONTENT = "content"
    const val OBG_ACTION_HOME_TAB_BAR_CHANGE = "tab_bar_change"
    const val OBG_ACTION_NOTIFICATION = "push_consent"
    const val OBG_ACTION_NOTIFICATION_CLICKED = "push_clicked"

    /** Game **/
    const val OBG_ACTION_GAME_PLAY = "game_play" // real
    const val OBG_ACTION_GAME_PRACTICE = "game_practice"
    const val OBG_ACTION_GAME_FAVORITE_ADD = "game_favorite_add"
    const val OBG_ACTION_GAME_FAVORITE_REMOVE = "game_favorite_remove"
    const val OBG_ACTION_GAME_RATE = "game_rate"
    const val OBG_ACTION_GAME_LOAD_SUCCESS = "game_load_success"
    const val OBG_ACTION_GAME_LOAD_FAILURE = "game_load_failure"
    const val OBG_ACTION_GAME_SEARCH = "game_in_game_search"
    const val OBG_ACTION_GAME_CLOSE = "game_in_game_close"
    const val OBG_ACTION_GAME_DEPOSIT = "game_in_game_deposit"
    const val OBG_ACTION_GAME_TOURNAMENT = "game_in_game_tournaments"
    const val OBG_ACTION_GAME_TOURNAMENTS_EVENT = "game_tournaments"
    const val OBG_ACTION_GAME_RECOMMENDED_VIEWED = "games_recommended_viewed"
    const val OBG_ACTION_GAME_RECOMMENDED_CLICKED = "games_recommended_clicked"
    const val OBG_ACTION_GAME_RECOMMENDED_SWIPE = "games_recommended_swipe_interaction"

    /** Login **/
    const val OBG_ACTION_RESET_PW_SUBMIT = "forgotten_password_submit"
    const val OBG_ACTION_RESET_PW_SUCCESS = "forgotten_password_success"
    const val OBG_ACTION_RESET_PW_FAILED = "forgotten_password_failed"
    const val OBG_ACTION_LOGIN_SUCCESSFUL = "login_successful"
    const val OBG_ACTION_LOGIN_FAILED = "login_failed"
    const val OBG_ACTION_IDENTIFY = "identify"
    const val OBG_ACTION_LOGIN_INTENT = "login_submitted"
    const val OBG_ACTION_LOGIN_FORGOTTEN_PASSWORD_CLICK = "login_forgotten_password_click"
    const val OBG_ACTION_LOGIN_FIELD_CHANGED = "login_field_changed"
    const val OBG_ACTION_ACTIVATE_BIOMETRICS_LOGIN = "activate_biometrics_login"
    const val OBG_ACTION_REGISTRATION_INTENT = "registration_intent"
    const val OBG_ACTION_REGISTRATION_VERIFIED = "registration_verified"
    const val OBG_ACTION_REGISTRATION_CONFIRMED = "confirmed_registration"
    const val OBG_ACTION_REGISTRATION_FORM_NEXT = "registration_form_next"
    const val OBG_ACTION_REGISTRATION_FORM_OPEN = "registration_form_open"

    const val OBG_ACTION_REGISTRATION_FIELD_CHANGED = "registration_field_changed"
    const val OBG_ACTION_REGISTRATION_NEXT = "registration_next"
    const val OBG_ACTION_REGISTRATION_TICK_BOX_CHANGED = "registration_tick_box_changed"
    const val OBG_ACTION_REGISTRATION_FAILED = "registration_failed"
    const val OBG_ACTION_REGISTRATION_SUBMIT = "registration_submit"
    const val OBG_ACTION_REGISTRATION_NOT_SUBMITTED = "registration_not_submitted"
    const val OBG_ACTION_ACTIVATE_BANKID_LOGIN = "activate_bankID_login"
    const val OBG_ACTION_LOGIN_FORM_OPEN = "login_form_open"
    const val OBG_ACTION_LOGIN_TAB_CLICKED = "login_tab_clicked"
    const val OBG_ACTION_LOGIN_BIOMETRICS_ACTIVATION_OPEN = "login_biometrics_activation_open"
    const val OBG_ACTION_BANK_ID_OPEN = "bankid_open"

    /** More **/
    const val OBG_ACTION_PROFILE_IMAGE_ADD = "profile_image_add"
    const val OBG_ACTION_PROFILE_IMAGE_REMOVE = "profile_image_remove"
    const val OBG_ACTION_PROFILE_RETRIEVE = "profile_retrieve"
    const val OBG_ACTION_CHANGE_PW = "change_password"
    const val OBG_ACTION_LIMIT_REMOVE = "limit_remove"
    const val OBG_ACTION_LIMIT_SET = "limit_set"
    const val OBG_ACTION_SELF_EXCLUSION = "self_exclusion"
    const val OBG_ACTION_SELF_ASSESSMENT = "self_assessment"
    const val OBG_ACTION_SELF_ASSESSMENT_EVALUATION = "self_assessment_evaluation"
    const val OBG_ACTION_FIRST_DEPOSIT = "payment_first_deposit_confirmed"
    const val OBG_ACTION_DEPOSIT_CONFIRMED = "payment_deposit_confirmed"
    const val OBG_ACTION_WITHDRAW_CREATED = "payment_withdraw_created"
    const val OBG_ACTION_TOTAL_DEPOSIT_CONFIRMED = "payment_total_deposit_confirmed"

    /** Promotions **/
    const val OBG_ACTION_PROMOTIONS = "promotion"

    const val OBG_ACTION_LOGOUT = "logout"

    const val OBG_ACTION_PROMOTIONS_LOAD_SUCCESS = "promotions_load_success"
    const val OBG_ACTION_PROMOTIONS_LOAD_FAILURE = "promotions_load_failure"

    const val OBG_ACTION_MESSAGES_LOAD_SUCCESS = "messages_load_success"
    const val OBG_ACTION_MESSAGES_LOAD_FAILURE = "messages_load_failure"

    const val OBG_ACTION_DEPOSIT_LOAD_SUCCESS = "deposit_load_success"
    const val OBG_ACTION_DEPOSIT_LOAD_FAILURE = "deposit_load_failure"

    const val OBG_ACTION_WITHDRAW_LOAD_SUCCESS = "withdraw_load_success"
    const val OBG_ACTION_WITHDRAW_LOAD_FAILURE = "withdraw_load_failure"

    /** WebView **/
    const val OBG_ACTION_WRAPPED_WEB_OPEN = "wrapped_web_open"
    const val OBG_ACTION_WRAPPED_WEB_CLOSE = "wrapped_web_close"

    /** One Trust Consent **/
    const val OBG_ACTION_CONSENT_PRIVACY = "privacy_consent"
    const val OBG_ACTION_CONSENT_PRIVACY_PREFERENCES_CLICKED = "privacy_preferences_clicked"
    const val OBG_ACTION_CONSENT_PRIVACY_FAILED = "privacy_consent_failed"
    const val OBG_ACTION_CONSENT_PRIVACY_CHANGED = "privacy_consent_changed"

    /** Messages **/
    const val OBG_ACTION_MESSAGES = "messages"
    const val OBG_ACTION_MESSAGE_VIEWED = "message_viewed"
    const val OBG_ACTION_MESSAGE_DELETED = "message_deleted"
    const val OBG_ACTION_MESSAGE_CLICKED = "message_clicked"
    const val OBG_ACTION_MESSAGE_TAB_CHANGED = "message_tab_changed"

    /** Bonus **/
    const val OBG_ACTION_BONUS_VIEWED = "bonus_viewed"
    const val OBG_ACTION_BONUS_TOC_VIEWED = "bonus_toc_viewed"
    const val OBG_ACTION_BONUS_CTA_CLICKED = "bonus_cta_clicked"
    const val OBG_ACTION_BONUS_ADDITIONAL_OK = "bonus_additional_ok"
    const val OBG_ACTION_BONUS_ADDITIONAL_CANCEL = "bonus_additional_cancel"
    const val OBG_ACTION_BONUS_QUICK_AMOUNT = "bonus_quick_amount"
    const val OBG_ACTION_BONUS_CLAIMED = "bonus_claimed"
    const val OBG_ACTION_BONUS_FORFEITED = "bonus_forfeited"

    /** SELECT BONUS PAGE **/
    const val OBG_ACTION_SELECT_BONUS_SHOWN = "select_bonus_shown"
    const val OBG_ACTION_SELECT_BONUS_LATER = "select_bonus_later"
    const val OBG_ACTION_SELECT_BONUS_ONLY_DEPOSIT = "select_bonus_only_deposit"

    /** ONBOARDING **/
    const val OBG_ACTION_ONBOARDING_POPUP_SHOWN = "onboarding_popup_shown"
    const val OBG_ACTION_ONBOARDING_POPUP_CTA_CLICKED = "onboarding_popup_cta_clicked"
    const val OBG_ACTION_ONBOARDING_POPUP_LATER_CTA_CLICKED = "onboarding_popup_later_clicked"
    const val OBG_ACTION_ONBOARDING_MODULE_SHOWN = "onboarding_module_shown"
    const val OBG_ACTION_ONBOARDING_MODULE_CLICKED = "onboarding_module_clicked"

    /** CTA to remove session limits **/
    const val SGA_PROP_ACTION_CTA_REMOVE_SESSION_LIMITS = "session_limits_removed"

    /** Tournaments **/
    const val OBG_ACTION_BET_LIMIT_REMOVE = "bet_limit_remove"
    const val OBG_ACTION_BET_LIMIT_SET = "bet_limit_set"

    /** Jalla **/
    const val OBG_ACTION_SPLASH_PLAY_NOW = "splash_play_now"
    const val OBG_ACTION_SPLASH_CONTINUE_PLAYING = "splash_continue_playing"
    const val OBG_ACTION_SPLASH_CHOOSE_BETWEEN = "splash_browse"
    const val OBG_ACTION_QUICK_SELECT_AMOUNT = "deposit_selection_quick_amount"
    const val OBG_ACTION_PAYMENT_PROVIDER_SELECT = "deposit_selection_provider_change"
    const val OBG_ACTION_DEPOSIT_MANUAL_AMOUNT = "deposit_selection_manual_amount"
    const val OBG_ACTION_DEPOSIT_BACK = "deposit_selection_back_button"
    const val OBG_ACTION_DEPOSIT_BANKID_VERIFY = "deposit_selection_start_button"
    const val OBG_ACTION_DAILY_LIMIT_CHANGE = "deposit_limit_daily_change"
    const val OBG_ACTION_WEEKLY_LIMIT_CHANGE = "deposit_limit_weekly_change"
    const val OBG_ACTION_MONTHLY_LIMIT_CHANGE = "deposit_limit_monthly_change"
    const val OBG_ACTION_DEPOSIT_LIMIT_SET = "deposit_limit_set"
    const val OBG_ACTION_DEPOSIT_LIMIT_CHECK = "deposit_limit_check"
    const val OBG_ACTION_DEPOSIT_CANCEL = "deposit_cancel"
    const val OBG_ACTION_DEPOSIT_BACK_BUTTON = "deposit_back_button"
    const val OBG_ACTION_BANKID_SUCCESS = "bankid_verification_success"
    const val OBG_ACTION_BANKID_MISSING = "error_bankid_missing"
    const val OBG_ACTION_BANKID_MISSING_INSTALL = "error_bankid_missing_install"
    const val OBG_ACTION_BANKID_MISSING_CLOSE = "error_bankid_missing_close"
    const val OBG_ACTION_DEPOSIT_INITIATED = "deposit_initiated"
    const val OBG_ACTION_ERROR_DETAILS = "error_details"
    const val OBG_ACTION_ERROR_TRY_AGAIN = "error_try_again_button"
    const val OBG_ACTION_ERROR_GO_HOME = "error_go_to_home_button"
    const val OBG_ACTION_ERROR_HELP = "error_help_button"

    /** Bet Limit **/
    const val OBG_ACTION_TOURNAMENT_CLICKED = "tournament_clicked"
    const val OBG_ACTION_TOURNAMENT_SHOW_ALL_CLICKED = "tournament_show_all_clicked"

    /** Sportsbook **/

    // NATIVE

    const val SB_BOTTOM_MENU_CLICK = "bottom_menu"
    const val SB_EVENT_SWITCHER_CLICK = "event_switcher_click"
    const val SB_EVENT_SWITCHER_SELECT = "event_switcher_select"
    const val SB_SEARCH_INTENT = "search_intent"
    const val SB_SEARCH_INITIATION = "search_initiation"
    const val SB_SEARCH_RESULT = "search_result"
    const val SB_SEARCH_RESULT_CLICK = "search_result_click"
    const val SB_SEARCH_NO_RESULT = "search_no_result"
    const val SB_MENU_QUICKLINKS = "menu_quicklinks"
    const val SB_LEFT_MENU_LINKS = "left_menu_links"
    const val SB_SEARCH_LINKS = "search_links"
    const val SB_BET_SETTLEMENT_NOTIFICATION = "bet_settlement_notification"
    const val SB_BET_SETTLEMENT_NOTIFICATION_CLICK = "bet_settlement_notification_click"
    const val OBG_ACTION_SPORTSBOOK = "Sportsbook"

    // IFRAME

    const val SB_QUICKLINKS_SCROLLER = "QuickLinksScroller"
    const val SB_WIDGET_CLICK = "SportsbookWidgetClick"
    const val SB_PREFERENCE_CHANGE = "SportsbookPreferenceChange"
    const val SB_MARKET_SELECTION_CLICK = "MarketSelectorClick"
    const val SB_MARKET_SELECTOR_APPLIED = "MarketSelectorApplied"
    const val SB_SETTINGS_CLICK = "SettingsClick"
    const val SB_CONTENT_LINKS_CLICK = "ContentLinksClick"
    const val SB_STATS_CLICK = "StatsClick"
    const val SB_FULL_BET_HISTORY_CLICK = "FullBetHistoryClick"
    const val SB_PLACE_BET_CLICK = "PlaceBetClick"
    const val SB_LIVE_EVENT_VISIT = "LiveEventVisit"
    const val SB_LIVESTREAM_INTERACTION = "LiveStreamInteraction"
    const val SB_PIN_CLICK = "PinClick"
    const val SB_EVENT_PAGE_VISIT = "EventPageVisit"
    const val SB_STATS_LOADING_TIME = "StatsLoadingTime"

    // COMMON AREAS
    const val SB_CHANGE_PASSWORD_CONFIRM = "change_password_confirm"
    const val SB_LOGIN_USERNAME = "login_username"
    const val SB_LOGIN_BIOMETRICS = "login_biometrics"
    const val SB_ACTIVATE_BIOMETRIC_LOGIN = "activate_biometric_login"
    const val SB_FORGOT_PASSWORD = "forgot_password"
    const val SB_RESET_PASSWORD = "reset_password"
    const val SB_MY_ACCOUNT_MENU = "my_account_menu"
    const val SB_REGISTRATION_OPEN = "registration_open"
    const val SB_REGISTRATION_FIELD_CHANGE = "registration_field_change"

    // CASINO FUNNEL – NG-7068
    const val OBG_ACTION_CASINO_FUNNEL = "Casino_Funnel"

    // LOGIN FUNNEL
    const val OBG_ACTION_LOGIN_FUNNEL = "Login_Funnel"

    // Tracking tag map Registration - NG-6840
    const val OBG_ACTION_REGISTRATION_FUNNEL = "Registration_Funnel"

    // Tracking tag map Gamin - NG-7185
    const val OBG_ACTION_GAMING_FUNNEL = "Gaming_Funnel"
    const val OBG_ACTION_CLICKS = "Clicks"
    const val OBG_ACTION_PAYMENTS = "Payments"
    const val OBG_ACTION_SEARCH = "Search"
    const val OBG_ACTION_FEATURE = "Feature"
    const val OBG_ACTION_CONSENT = "Consent"
    const val OBG_ACTION_VERIFICATION = "Verification"
    const val OBG_ACTION_SCREEN_VIEW = "screen_view"

    // GARBAGE DOMAIN
    const val OBG_ACTION_GARBAGE_DOMAIN_BLOCKED = "garbage_domain_blocked"
    const val OBG_ACTION_GARBAGE_DOMAIN_SUCCESS = "garbage_domain_success"
    const val OBG_ACTION_SB_IFRAME_FAILURE = "sportsbook_load_failure"
    const val OBG_ACTION_SB_IFRAME_SUCCESS = "sportsbook_load_success"

    // Casino tag map
    const val OBG_ACTION_GAMES = "Games"

    // DEVICE-CHECK
    const val OBG_ACTION_DEVICE_CHECK_SUCCESS = "device_check_success"
    const val OBG_ACTION_DEVICE_CHECK_FAILURE = "device_check_failure"
}

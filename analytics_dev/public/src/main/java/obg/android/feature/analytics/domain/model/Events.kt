package obg.android.feature.analytics.domain.model

import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AFInAppEventType
import com.google.firebase.analytics.FirebaseAnalytics
import obg.android.feature.analytics.OBGAnalyticsAction
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_ACTIVATE_BANKID_LOGIN
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_ACTIVATE_BIOMETRICS_LOGIN
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_APPLY_FILTER
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_BANKID_MISSING
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_BANKID_MISSING_CLOSE
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_BANKID_MISSING_INSTALL
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_BANKID_SUCCESS
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_BANK_ID_OPEN
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_BET_LIMIT_REMOVE
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_BET_LIMIT_SET
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_BONUS_ADDITIONAL_CANCEL
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_BONUS_ADDITIONAL_OK
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_BONUS_CLAIMED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_BONUS_CTA_CLICKED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_BONUS_FORFEITED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_BONUS_QUICK_AMOUNT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_BONUS_TOC_VIEWED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_BONUS_VIEWED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_CASINO_FUNNEL
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_CLICKS
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_CONSENT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_CONSENT_PRIVACY
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_CONSENT_PRIVACY_CHANGED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_CONSENT_PRIVACY_PREFERENCES_CLICKED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_DAILY_LIMIT_CHANGE
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_DEPOSIT_BACK
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_DEPOSIT_BANKID_VERIFY
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_DEPOSIT_CANCEL
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_DEPOSIT_CONFIRMED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_DEPOSIT_INITIATED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_DEPOSIT_LIMIT_CHECK
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_DEPOSIT_LIMIT_SET
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_DEPOSIT_MANUAL_AMOUNT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_DEVICE_CHECK_FAILURE
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_ERROR_DETAILS
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_ERROR_GO_HOME
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_ERROR_HELP
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_ERROR_TRY_AGAIN
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_FEATURE
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_FIRST_DEPOSIT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_GAMES
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_GAME_CLOSE
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_GAME_DEPOSIT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_GAME_RATE
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_GAME_RECOMMENDED_CLICKED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_GAME_RECOMMENDED_SWIPE
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_GAME_RECOMMENDED_VIEWED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_GAME_SEARCH
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_GAME_SORT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_GAME_TOURNAMENT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_GAMING_FUNNEL
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_GARBAGE_DOMAIN_BLOCKED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_GARBAGE_DOMAIN_SUCCESS
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_HOME_DEEP_LINK_CATEGORY
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_HOME_DEEP_LINK_MORE
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_HOME_DEEP_LINK_URL
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_HOME_OPEN_CONTENT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_HOME_OPEN_GAME
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_HOME_SELECT_ITEM
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_LIMIT_REMOVE
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_LIMIT_SET
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_LOGIN_BIOMETRICS_ACTIVATION_OPEN
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_LOGIN_FAILED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_LOGIN_FIELD_CHANGED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_LOGIN_FORM_OPEN
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_LOGIN_FUNNEL
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_LOGIN_INTENT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_LOGIN_SUCCESSFUL
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_LOGIN_TAB_CLICKED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_LOGOUT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_MESSAGES
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_MESSAGE_CLICKED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_MESSAGE_DELETED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_MESSAGE_TAB_CHANGED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_MESSAGE_VIEWED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_MONTHLY_LIMIT_CHANGE
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_NOTIFICATION
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_ONBOARDING_MODULE_CLICKED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_ONBOARDING_MODULE_SHOWN
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_ONBOARDING_POPUP_CTA_CLICKED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_ONBOARDING_POPUP_LATER_CTA_CLICKED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_ONBOARDING_POPUP_SHOWN
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_PAYMENTS
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_PAYMENT_PROVIDER_SELECT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_PROFILE_IMAGE_ADD
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_PROFILE_IMAGE_REMOVE
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_PROFILE_RETRIEVE
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_PROMOTIONS
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_QUICK_SELECT_AMOUNT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_REGISTRATION_CONFIRMED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_REGISTRATION_FAILED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_REGISTRATION_FIELD_CHANGED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_REGISTRATION_FORM_NEXT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_REGISTRATION_FORM_OPEN
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_REGISTRATION_FUNNEL
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_REGISTRATION_NEXT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_REGISTRATION_NOT_SUBMITTED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_REGISTRATION_SUBMIT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_REGISTRATION_TICK_BOX_CHANGED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_REGISTRATION_VERIFIED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_RESET_PW_SUBMIT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_SB_IFRAME_FAILURE
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_SB_IFRAME_SUCCESS
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_SEARCH
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_SELECT_BONUS_ONLY_DEPOSIT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_SELF_ASSESSMENT
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_SELF_ASSESSMENT_EVALUATION
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_SPLASH_CHOOSE_BETWEEN
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_SPORTSBOOK
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_TOTAL_DEPOSIT_CONFIRMED
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_VERIFICATION
import obg.android.feature.analytics.OBGAnalyticsAction.OBG_ACTION_WEEKLY_LIMIT_CHANGE
import obg.android.feature.analytics.OBGAnalyticsAction.SB_BOTTOM_MENU_CLICK
import obg.android.feature.analytics.OBGAnalyticsAction.SB_CONTENT_LINKS_CLICK
import obg.android.feature.analytics.OBGAnalyticsAction.SB_EVENT_PAGE_VISIT
import obg.android.feature.analytics.OBGAnalyticsAction.SB_EVENT_SWITCHER_CLICK
import obg.android.feature.analytics.OBGAnalyticsAction.SB_EVENT_SWITCHER_SELECT
import obg.android.feature.analytics.OBGAnalyticsAction.SB_FULL_BET_HISTORY_CLICK
import obg.android.feature.analytics.OBGAnalyticsAction.SB_LEFT_MENU_LINKS
import obg.android.feature.analytics.OBGAnalyticsAction.SB_LIVESTREAM_INTERACTION
import obg.android.feature.analytics.OBGAnalyticsAction.SB_LIVE_EVENT_VISIT
import obg.android.feature.analytics.OBGAnalyticsAction.SB_MARKET_SELECTION_CLICK
import obg.android.feature.analytics.OBGAnalyticsAction.SB_MARKET_SELECTOR_APPLIED
import obg.android.feature.analytics.OBGAnalyticsAction.SB_MENU_QUICKLINKS
import obg.android.feature.analytics.OBGAnalyticsAction.SB_PIN_CLICK
import obg.android.feature.analytics.OBGAnalyticsAction.SB_PLACE_BET_CLICK
import obg.android.feature.analytics.OBGAnalyticsAction.SB_PREFERENCE_CHANGE
import obg.android.feature.analytics.OBGAnalyticsAction.SB_QUICKLINKS_SCROLLER
import obg.android.feature.analytics.OBGAnalyticsAction.SB_SEARCH_INITIATION
import obg.android.feature.analytics.OBGAnalyticsAction.SB_SEARCH_INTENT
import obg.android.feature.analytics.OBGAnalyticsAction.SB_SEARCH_LINKS
import obg.android.feature.analytics.OBGAnalyticsAction.SB_SEARCH_NO_RESULT
import obg.android.feature.analytics.OBGAnalyticsAction.SB_SEARCH_RESULT_CLICK
import obg.android.feature.analytics.OBGAnalyticsAction.SB_SETTINGS_CLICK
import obg.android.feature.analytics.OBGAnalyticsAction.SB_STATS_CLICK
import obg.android.feature.analytics.OBGAnalyticsAction.SB_STATS_LOADING_TIME
import obg.android.feature.analytics.OBGAnalyticsAction.SB_WIDGET_CLICK
import obg.android.feature.analytics.OBGAnalyticsAction.SGA_PROP_ACTION_CTA_REMOVE_SESSION_LIMITS
import obg.android.feature.analytics.OBGAnalyticsPage.OBG_CONFIRM_CHANGE_PW
import obg.android.feature.analytics.OBGAnalyticsPage.OBG_VIEW_CHANGE_PW
import obg.android.feature.analytics.OBGAnalyticsPage.OBG_VIEW_GAME_FILTER
import obg.android.feature.analytics.OBGAnalyticsPage.OBG_VIEW_SET_LIMIT
import obg.android.feature.analytics.OBGAnalyticsPage.SB_SEARCH
import obg.android.feature.analytics.OBGAnalyticsPage.SB_SEARCH_RESULT
import obg.android.feature.analytics.OBGAnalyticsProperties
import obg.android.feature.analytics.OBGAnalyticsProperties.Native_App_Name
import obg.android.feature.analytics.OBGAnalyticsProperties.Native_App_Version
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_BANK_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_BANK_ID_FORM_OPEN
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_BANK_ID_START
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CHANGE_PASSWORD_FAILED
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CHANGE_PASSWORD_FORM_SUBMIT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CHANGE_PASSWORD_SUCCESS
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_BET_CONFIRMED
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_BURGER_MENU_CLICK
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_CAROUSEL_NAVIGATION_CLICK
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_DEPOSIT_LIMITS_CHANGE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_DEVICE_CHECK_STATE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_DIRECTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_GAME_CLICK
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_GAME_SEARCH
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_GARBAGE_DOMAIN_URL
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_NOTIFICATION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_NOTIFICATION_VERIFY_ACCOUNT_CLICK
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_NOTIFICATION_VERIFY_ACCOUNT_OPEN
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_NUMBERED_DOMAIN_URL
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_SB_IFRAME_COUNTRY_CODE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_SB_IFRAME_URL
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_SLIDE_NUMBER
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_SPORTSBOOK_SEARCH
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CONST_SWISH_APP_START
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_CREATE_ACCOUNT_CLICK
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_LOGIN_FAILED
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_LOGIN_FIELD_CHANGE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_LOGIN_FORGOTTEN_PASSWORD_CLICK
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_LOGIN_FORGOTTEN_PASSWORD_FAILED
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_LOGIN_FORGOTTEN_PASSWORD_SUCCESS
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_LOGIN_FORM_CLOSE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_LOGIN_FORM_OPEN
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_LOGIN_INTENTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_LOGIN_PASSWORD_SUBMIT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_LOGIN_SUBMITTED
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_LOGIN_TAB_CLICKS
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_MY_ACCOUNT_CLICK
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_OPEN_MOBILE_BANK_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ACCOUNT_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ADJUSTED_DEPOSIT_AMOUNT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ALLOW_AD_PERSONALIZATION_SIGNALS
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ALLOW_GOOGLE_SIGNALS
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_APPSFLYER_AAID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_APPSFLYER_CUID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_APPSFLYER_DEV_KEY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_APPSFLYER_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_APP_PRODUCT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_BALANCE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_BIOMETRICS_ENABLED
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_BONUS_EXPIRY_DATE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_BONUS_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_BONUS_SELECTED_AMOUNT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_BONUS_STATE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_BONUS_TITLE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_BONUS_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_BONUS_TYPE_CAP
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_BONUS_USER_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_BONUS_WAGERED
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_BRAND_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CASINO_ACTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CASINO_EVENT_ACTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CASINO_HOME_ITEM_EVENT_ACTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CASINO_HOME_SECTION_EVENT_ACTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CASINO_ITEM_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CASINO_ITEM_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CASINO_SECTION_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CLICKED_FROM
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CONSENT_PRIVACY_CLICKED_FROM
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CONSENT_PRIVACY_DOMAIN_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CONSENT_PRIVACY_GIVEN
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CONTENT_GROUP
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CTA_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CTA_URL
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CURRENCY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_CUSTOMER_STATES_EVENT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_DEEPLINK
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_DEPOSIT_AMOUNT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_DEPOSIT_DAILY_LIMIT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_DEPOSIT_IS_FIRST_DEPOSIT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_DEPOSIT_MONTHLY_LIMIT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_DEPOSIT_PAYMENT_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_DEPOSIT_PROVIDER
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_DEPOSIT_WEEKLY_LIMIT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_DEVICE_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ERROR
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ERROR_MESSAGE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_EVENT_ACTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_EVENT_PHASE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_FILTER_ONLY_JACKPOT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_FILTER_PROVIDERS
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_FIRST_LOGIN
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_FROM_SCREEN
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GAME_CATEGORY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GAME_CATEGORY_CAPS
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GAME_CLICKED_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GAME_CLICKED_INDEX
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GAME_CLICKED_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GAME_GAMES_MODULE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GAME_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GAME_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GAME_NAME_CAPS
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GAME_PAGE_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GAME_PAGE_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GAME_PROVIDER
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GAME_RATE_VALUE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GTM_DEEPLINK
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GTM_FILTER_PROVIDERS
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GTM_GAME_CATEGORY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GTM_GAME_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GTM_ITEM_SECTION_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GTM_ITEM_TITLE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GTM_SORT_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GTM_TAB_BAR_FROM_KEY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GTM_TAB_BAR_TO_KEY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GUID_EVENT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GUID_USER
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_INTERFACE_BRAND
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_INTERFACE_COMPONENT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_INTERFACE_SEARCH_TERM
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_INTERFACE_SECTION_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_INTERFACE_SITE_LANGUAGE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_INTERFACE_WIDGETNAME
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_IS_DEPOSIT_AMOUNT_ALLOWED
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_CAPTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_CTA
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_CTA_TEXT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_SECTION_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_SECTION_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_TITLE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_VIDEO_URL
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_JURISDICTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_LOCALE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_LOGIN_CURRENCY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_LOGIN_FAILURE_REASON
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_LOGIN_FIELD_NAME_CHANGED
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_LOGIN_METHOD
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_LOGIN_METHOD_V2
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_LOGIN_STATUS
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_LOGIN_TAB
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_LOGIN__BALANCE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_MESSAGE_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_NOTIFICATION_CONSENT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_NOTIFICATION_DEEPLINK
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_NUMBER_OF_BONUSES
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ONBOARDING_MODULE_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_PAYMENT_AMOUNT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_PAYMENT_CURRENCY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_PAYMENT_DEPOSIT_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_PAYMENT_METHOD
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_PAYMENT_REFERENCE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_PAYMENT_SEL_AMOUNT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_PAYMENT_SEL_CURRENCY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_PAYMENT_STATUS
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_PAYMENT_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTER_ACCOUNT_CLICKED_FROM
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_ACTIVITY_SELECTION_EVENT_ACTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_BOX_CHANGED
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_BOX_CHANGED_TO
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_CHANGE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_COMPLETE_REGISTRATION_FROM_OPEN_EVENT_ACTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_COMPLETE_VERIFIED_EVENT_ACTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_COMPONENT_COUNTRY_CODE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_ERROR
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_EVENT_ACTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_FAILED_EVENT_ACTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_FIELD_NAME_CHANGED
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_FORM_PAGE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_FROM_FIELDS
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_FROM_FIELD_ERROR
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_I_AM_NOT_SWEDISH_RESIDENT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_NRC_EVENT_ACTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_SUBMIT_EVENT_ACTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REGISTRATION_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REMEMBER_ME
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_REVENUE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_SCREEN
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_SCREEN_CLASS
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_SCREEN_IDENTIFIER
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_SCREEN_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_SESSION_LIMIT_CHECK_PERIOD
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_SHARE_THE_GAME_EVENT_ACTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_SIGNIN_METHOD
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_SORT_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TAB_BAR_FROM
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TAB_BAR_FROM_KEY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TAB_BAR_TO
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TAB_BAR_TO_KEY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TAB_CLICKED
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TECHNICAL_ERROR
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TECHNICAL_EVENT_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TECHNICAL_PLATFORM_DELIVERY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TECHNICAL_PLATFORM_ENV
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TECHNICAL_PLATFORM_USED
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TECHNICAL_RAW_USER_AGENT_INFO
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TECHNICAL_SCREEN_ORIENTATION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TECHNICAL_SCREEN_RESOLUTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TECHNICAL_START_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TECHNICAL_TARGETING_CONSENT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TECHNICAL_WINDOW_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TOURNAMENT_EVENT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TOURNAMENT_EVENT_ITEM_CLICKED
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TOURNAMENT_EVENT_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TOURNAMENT_EVENT_TOURNAMENT_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TOURNAMENT_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TRAFFIC_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_USER_COUNTRY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_USER_CUSTOMER_LEVEL
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_USER_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_USER_REGISTRATION_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_USER_REG_METHOD
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_USER_REG_STEP
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_VERSION_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_RESET_PASSWORD_FAILED
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_RESET_PASSWORD_FORM_OPEN
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_RESET_PASSWORD_FORM_SUBMIT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_RESET_PASSWORD_SUCCESS
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_SAME_DEVICE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_TERMS_AND_CONDITIONS_ACCEPTANCE
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_ACTION
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_APP_LOADER_LOAD_IFRAME_DURATION
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_BET_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_CATEGORY_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_CATEGORY_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_COMPETITION_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_COMPETITION_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_COUPON_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_EVENT
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_EVENT_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_EVENT_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_EVENT_PHASE
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_EVENT_SWITCHER_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_FILTER_ENABLED
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_FILTER_ITEM
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_GAME_LIST
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_ITEM_CLICKED
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_LOBBY
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_LOBBY_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_LOCATION
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_MARKET_TEMPLATE_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_MENU_CATEGORY
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_MENU_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_PAGE_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_PROP_BET_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_PROP_BONUS_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_PROP_CATEGORY_DETAILS
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_PROP_COMPETITION_DETAILS
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_PROP_COUPON_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_PROP_EVENT_DETAILS
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_PROP_MARKET_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_PROP_MARKET_TEMPLATE_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_PROP_REMEMBER_STAKE_FLAG
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_PROVIDER
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_REMEMBER_STAKE_FLAG
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_SCREEN_VIEW
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_SEARCH_EVENT_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_SEARCH_POSITION
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_SEARCH_TERM
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_SECTION_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_STATUS
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_SUBCAT_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_SUBCAT_NAME
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_TABNAME
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_TOTAL_STAKE
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_VIEW_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_VIRTUAL_TITLE
import obg.android.feature.analytics.OBGAnalyticsProperties.SB_VIRTUAL_URL
import obg.android.feature.analytics.OBGAnalyticsProperties.SGA_PROP_CTA_REMOVE_SESSION_LIMITS
import obg.android.feature.analytics.domain.model.event.Event
import obg.android.feature.analytics.domain.model.event.EventConfiguration
import obg.android.feature.analytics.domain.model.event.EventConfigurator
import obg.android.feature.analytics.domain.model.event.PropertyConfigurator
import obg.android.feature.analytics.domain.model.event.ServiceSpecificEventConfiguration
import obg.android.feature.analytics.models.DefaultScreenAnalyticsModel
import obg.android.feature.analytics.models.DepositCompletedAnalyticsModel
import obg.android.feature.analytics.models.GameAnalyticsExtrasModel
import obg.android.feature.analytics.models.GamePlayAnalyticsExtrasModel
import obg.android.feature.analytics.models.GameRecommendedClickedAnalyticsModel
import obg.android.feature.analytics.models.HomeItemAnalyticsModel
import obg.android.feature.analytics.models.LobbyViewPageAnalyticsModel
import obg.android.feature.analytics.models.OBGAnalyticsMainSportsBookModel
import obg.android.feature.analytics.models.OBGAnalyticsTabBarChangeModel
import obg.android.feature.analytics.models.PinClickAnalyticsModel
import obg.android.feature.analytics.models.RateGameAnalyticsModel
import obg.android.feature.analytics.models.SetFavoriteGameAnalyticsModel
import obg.android.feature.analytics.models.SportsBookWidgetItemClickAnalyticsModel
import obg.android.feature.analytics.models.StatsClickAnalyticsModel
import obg.android.feature.analytics.models.SwitcherSelectActionAnalyticsModel
import obg.android.feature.analytics.utils.processTextForGTM
import obg.android.shared.domain.model.analytics.ScreenInfo

/**
 * All analytic event definitions.
 *
 * To keep analytics module independent from other modules, for event parameters use only
 * primitive types or types defined entirely in this module.
 */
sealed class Events(
    override val name: String
) : Event {

    sealed class Action(
        override val name: String,
        override val configuration: EventConfiguration
    ) : Events(name) {
        override val serviceId: ServiceId? = Analytics.FIREBASE

        sealed class ConfigActions(
            action: String,
            configuration: EventConfiguration
        ) : Action(action, configuration) {
            sealed class GarbageDomain(
                action: String,
                configuration: EventConfiguration
            ) : ConfigActions(action, configuration) {
                data class Success(
                    val garbageDomainUrl: String,
                    val numberedDomainUrl: String
                ) : GarbageDomain(
                    action = OBG_ACTION_GARBAGE_DOMAIN_SUCCESS,
                    configuration = {
                        defaultFirebaseParams()
                        set(OBG_CONST_GARBAGE_DOMAIN_URL, garbageDomainUrl)
                        set(OBG_CONST_NUMBERED_DOMAIN_URL, numberedDomainUrl)
                    }
                )

                data class Blocked(
                    val garbageDomainUrl: String,
                ) : GarbageDomain(
                    action = OBG_ACTION_GARBAGE_DOMAIN_BLOCKED,
                    configuration = {
                        set(OBG_CONST_GARBAGE_DOMAIN_URL, garbageDomainUrl)
                    }
                )
            }

            sealed class DeviceStatus(
                action: String,
                status: String
            ) : Action(
                action,
                configuration = {
                    defaultFirebaseParams()
                    set(OBG_CONST_DEVICE_CHECK_STATE, status)
                }
            ) {

                data class Success(
                    val status: String,
                ) : DeviceStatus(
                    action = OBG_ACTION_DEVICE_CHECK_FAILURE,
                    status = status
                )

                data class Failure(
                    val status: String,
                ) : DeviceStatus(
                    action = OBG_ACTION_DEVICE_CHECK_FAILURE,
                    status = status
                )
            }
        }

        data class PrivacyConsentFailed(
            val domainId: String,
            val storageLocation: String,
            val language: String
        ) : Action(
            OBG_PROP_CONSENT_PRIVACY_DOMAIN_ID,
            {
                defaultFirebaseParams()
            }
        )

        sealed class IFrameActions(
            action: String,
            iFrameCountryCode: String,
            iFrameUrl: String
        ) : Action(
            name = action,
            configuration = {
                defaultFirebaseParams()
                set(OBG_CONST_SB_IFRAME_COUNTRY_CODE, iFrameCountryCode)
                set(OBG_CONST_SB_IFRAME_URL, iFrameUrl)
            }
        ) {
            data class Success(
                val iFrameCountryCode: String,
                val iFrameUrl: String
            ) : IFrameActions(
                action = OBG_ACTION_SB_IFRAME_SUCCESS,
                iFrameCountryCode = iFrameCountryCode,
                iFrameUrl = iFrameUrl
            )

            data class Failure(
                val iFrameCountryCode: String,
                val iFrameUrl: String
            ) : IFrameActions(
                action = OBG_ACTION_SB_IFRAME_FAILURE,
                iFrameCountryCode = iFrameCountryCode,
                iFrameUrl = iFrameUrl
            )
        }

        sealed class SearchActions(
            action: String,
            configuration: EventConfiguration
        ) : Action(
            name = action,
            configuration = configuration
        ) {
            data object Initiation : SearchActions(
                action = SB_SEARCH_INITIATION,
                configuration = {
                    defaultFirebaseParams()
                }
            )

            data class NoResult(
                val query: String
            ) : SearchActions(
                action = SB_SEARCH_NO_RESULT,
                configuration = {
                    defaultFirebaseParams()
                    set(SB_SEARCH_TERM, query)
                }
            )

            data class Result(
                val query: String,
                val result: String,
                val searchPosition: String
            ) : SearchActions(
                action = SB_SEARCH_RESULT,
                configuration = {
                    defaultFirebaseParams()
                    set(SB_SEARCH_TERM, query)
                    set(SB_SEARCH_RESULT, result)
                    set(SB_SEARCH_POSITION, searchPosition)
                }
            )

            data class Links(
                val menuName: String,
                val menuCategory: String
            ) : SearchActions(
                action = SB_SEARCH_LINKS,
                configuration = {
                    defaultFirebaseParams()
                    set(SB_MENU_NAME, menuName)
                    set(SB_MENU_CATEGORY, menuCategory)
                }
            )
        }

        sealed class MenuAction(
            action: String,
            menuName: String
        ) : Action(
            action,
            {
                defaultFirebaseParams()
                set(SB_MENU_NAME, menuName)
            }
        ) {
            data class Click(
                val menuName: String
            ) : MenuAction(
                action = SB_LEFT_MENU_LINKS,
                menuName = menuName
            )

            data class QuickLink(
                val menuName: String
            ) : MenuAction(
                action = SB_MENU_QUICKLINKS,
                menuName = menuName
            )
        }

        data object SearchIntentAction : Action(
            SB_SEARCH_INTENT,
            {
                defaultFirebaseParams()
            }
        )

        sealed class BetActions(
            val action: String
        ) : Action(
            action,
            {
                defaultFirebaseParams()
            }
        ) {
            data object LimitSet : BetActions(action = OBG_ACTION_BET_LIMIT_SET)
            data object LimitRemove : BetActions(action = OBG_ACTION_BET_LIMIT_REMOVE)
        }

        data class FilterAppliedAction(
            val filterProviders: String,
            val gtmFilterProviders: String,
            val filterOnlyJackpot: String,
            val gtmGameCategory: String
        ) : Action(
            OBG_ACTION_APPLY_FILTER,
            {
                defaultFirebaseParams()
                set(OBG_PROP_FILTER_PROVIDERS, filterProviders)
                set(OBG_PROP_GTM_FILTER_PROVIDERS, gtmFilterProviders)
                set(OBG_PROP_FILTER_ONLY_JACKPOT, filterOnlyJackpot)
                set(OBG_PROP_GTM_GAME_CATEGORY, gtmGameCategory)
            }
        )

        sealed class BiometricAction(
            biometricAction: String,
            hasSavedBiometrics: String,
            isRememberMeActivated: String
        ) : Action(
            biometricAction,
            {
                defaultFirebaseParams()
                set(OBG_PROP_BIOMETRICS_ENABLED, hasSavedBiometrics)
                set(OBG_PROP_REMEMBER_ME, isRememberMeActivated)
            }
        ) {
            data class ResetPassword(
                val hasSavedBiometrics: String,
                val isRememberMeActivated: String
            ) : BiometricAction(
                OBG_ACTION_RESET_PW_SUBMIT,
                hasSavedBiometrics,
                isRememberMeActivated
            )

            data class ActivationOpen(
                val hasSavedBiometrics: String,
                val isRememberMeActivated: String
            ) : BiometricAction(
                OBG_ACTION_LOGIN_BIOMETRICS_ACTIVATION_OPEN,
                hasSavedBiometrics,
                isRememberMeActivated
            )

            data class ActivateLogin(
                val hasSavedBiometrics: String,
                val isRememberMeActivated: String
            ) : BiometricAction(
                OBG_ACTION_ACTIVATE_BIOMETRICS_LOGIN,
                hasSavedBiometrics,
                isRememberMeActivated
            )
        }

        data class LoginFieldChanged(
            val hasSavedBiometrics: String,
            val isRememberMeActivated: String,
            val tab: String,
            val fieldChanged: String
        ) : Action(
            OBG_ACTION_LOGIN_FIELD_CHANGED,
            {
                defaultFirebaseParams()
                set(OBG_PROP_LOGIN_TAB, tab)
                set(OBG_PROP_LOGIN_FIELD_NAME_CHANGED, fieldChanged)
                set(OBG_PROP_BIOMETRICS_ENABLED, hasSavedBiometrics)
                set(OBG_PROP_REMEMBER_ME, isRememberMeActivated)
            }
        )

        sealed class OnBoardingPopUpCtaAction(
            val action: String,
        ) : Action(
            action,
            {
                defaultFirebaseParams()
            }
        ) {
            data object Skip : OnBoardingPopUpCtaAction(
                action = OBG_ACTION_ONBOARDING_POPUP_LATER_CTA_CLICKED
            )

            data object Claim : OnBoardingPopUpCtaAction(
                action = OBG_ACTION_ONBOARDING_POPUP_CTA_CLICKED
            )
        }

        data object BankIdMissingInstallAction : Action(
            OBG_ACTION_BANKID_MISSING_INSTALL,
            {
                defaultFirebaseParams()
            }
        )

        data object BankIdMissingCloseAction : Action(
            OBG_ACTION_BANKID_MISSING_CLOSE,
            {
                defaultFirebaseParams()
            }
        )

        data class MessageClickAction(
            val messageId: String,
            val ctaType: String,
            val ctaUrl: String,
        ) : Action(
            OBG_ACTION_MESSAGE_CLICKED,
            {
                defaultFirebaseParams()
                set(OBG_PROP_MESSAGE_ID, messageId)
                set(OBG_PROP_CTA_TYPE, ctaType)
                set(OBG_PROP_CTA_URL, ctaUrl)
            }
        )

        data class MessageDeletedAction(
            val messageId: String
        ) : Action(
            OBG_ACTION_MESSAGE_DELETED,
            {
                defaultFirebaseParams()
                set(OBG_PROP_MESSAGE_ID, messageId)
            }
        )

        data class MessageTabChangedAction(
            val tabClicked: String
        ) : Action(
            OBG_ACTION_MESSAGE_TAB_CHANGED,
            {
                defaultFirebaseParams()
                set(OBG_PROP_TAB_CLICKED, tabClicked)
            }
        )

        data class MessageViewedAction(
            val messageId: String
        ) : Action(
            OBG_ACTION_MESSAGE_VIEWED,
            {
                defaultFirebaseParams()
                set(OBG_PROP_MESSAGE_ID, messageId)
            }
        )

        data class QuickLinksClickAction(
            val itemClicked: String,
        ) : Action(
            SB_QUICKLINKS_SCROLLER,
            {
                defaultFirebaseParams()
                set(SB_ITEM_CLICKED, itemClicked)
            }
        )

        data class SportsBookWidgetItemClickAction(
            val analyticsModel: SportsBookWidgetItemClickAnalyticsModel
        ) : Action(
            SB_WIDGET_CLICK,
            {
                defaultFirebaseParams()
                set(SB_CATEGORY_ID, analyticsModel.categoryId)
                set(SB_CATEGORY_NAME, analyticsModel.categoryName)
                set(SB_EVENT_ID, analyticsModel.eventId)
                set(SB_EVENT_NAME, analyticsModel.eventName)
                set(SB_ACTION, analyticsModel.action)
                set(SB_LOCATION, analyticsModel.location)
                set(SB_BET_TYPE, analyticsModel.betType)
                set(SB_REMEMBER_STAKE_FLAG, analyticsModel.rememberStakeFlag)
            }
        )

        data class SportsBookPreferenceChangeAction(
            val location: String,
            val filterItem: String,
            val viewType: String
        ) : Action(
            SB_PREFERENCE_CHANGE,
            {
                defaultFirebaseParams()
                set(SB_LOCATION, location)
                set(SB_FILTER_ITEM, filterItem)
                set(SB_VIEW_TYPE, viewType)
            }
        )

        data class MarketSelectionClickAction(
            val location: String,
            val categoryName: String,
            val categoryId: String,
            val filterEnabled: String
        ) : Action(
            SB_MARKET_SELECTION_CLICK,
            {
                defaultFirebaseParams()
                set(SB_LOCATION, location)
                set(SB_CATEGORY_NAME, categoryName)
                set(SB_FILTER_ENABLED, filterEnabled)
                set(SB_CATEGORY_ID, categoryId)
            }
        )

        data class MarketSelectorAppliedAction(
            val filterItem: String,
            val categoryId: String,
            val categoryName: String,
            val marketTemplateId: String
        ) : Action(
            SB_MARKET_SELECTOR_APPLIED,
            {
                defaultFirebaseParams()
                set(SB_FILTER_ITEM, filterItem)
                set(SB_CATEGORY_NAME, categoryName)
                set(SB_CATEGORY_ID, categoryId)
                set(SB_MARKET_TEMPLATE_ID, marketTemplateId)
            }
        )

        data class SettingsClickAction(
            val settingsClick: String,
        ) : Action(
            SB_SETTINGS_CLICK,
            {
                defaultFirebaseParams()
                set(SB_SECTION_NAME, settingsClick)
            }
        )

        data class ContentLinksClickAction(
            val itemClicked: String,
        ) : Action(
            SB_CONTENT_LINKS_CLICK,
            {
                defaultFirebaseParams()
                set(SB_ITEM_CLICKED, itemClicked)
            }
        )

        data class FullBetHistoryClickAction(
            val sectionName: String,
        ) : Action(
            SB_FULL_BET_HISTORY_CLICK,
            {
                defaultFirebaseParams()
                set(SB_SECTION_NAME, sectionName)
            }
        )

        data class PlaceBetClickAction(
            val totalStake: String,
            val rememberStakeFlag: String,
            val betType: String,
            val couponType: String,
        ) : Action(
            SB_PLACE_BET_CLICK,
            {
                defaultFirebaseParams()
                set(SB_TOTAL_STAKE, totalStake)
                set(SB_REMEMBER_STAKE_FLAG, rememberStakeFlag)
                set(SB_BET_TYPE, betType)
                set(SB_COUPON_TYPE, couponType)
            }
        )

        data class LiveEventVisitAction(
            val eventId: String,
            val eventName: String,
            val tabName: String
        ) : Action(
            SB_LIVE_EVENT_VISIT,
            {
                defaultFirebaseParams()
                set(SB_EVENT_ID, eventId)
                set(SB_EVENT_NAME, eventName)
                set(SB_TABNAME, tabName)
            }
        )

        data class LiveStreamInteractionAction(
            val eventId: String,
            val action: String,
            val tabName: String
        ) : Action(
            SB_LIVESTREAM_INTERACTION,
            {
                defaultFirebaseParams()
                set(SB_EVENT_ID, eventId)
                set(SB_ACTION, action)
                set(SB_TABNAME, tabName)
            }
        )

        data class PageVisitAction(
            val tabName: String,
        ) : Action(
            SB_EVENT_PAGE_VISIT,
            {
                defaultFirebaseParams()
                set(SB_PAGE_NAME, tabName)
            }
        )

        data class StatsClickAction(
            val analyticsModel: StatsClickAnalyticsModel
        ) : Action(
            SB_STATS_CLICK,
            {
                defaultFirebaseParams()
                set(SB_CATEGORY_ID, analyticsModel.categoryId)
                set(SB_CATEGORY_NAME, analyticsModel.categoryName)
                set(SB_EVENT_ID, analyticsModel.eventId)
                set(SB_EVENT_PHASE, analyticsModel.eventPhase)
                set(SB_COMPETITION_ID, analyticsModel.competitionId)
                set(SB_COMPETITION_NAME, analyticsModel.competitionName)
                set(SB_PROVIDER, analyticsModel.provider)
            }
        )

        data class PinClickAction(
            val analyticsModel: PinClickAnalyticsModel
        ) : Action(
            SB_PIN_CLICK,
            {
                defaultFirebaseParams()
                set(SB_STATUS, analyticsModel.status)
                set(SB_CATEGORY_ID, analyticsModel.categoryId)
                set(SB_CATEGORY_NAME, analyticsModel.categoryName)
                set(SB_EVENT_ID, analyticsModel.eventId)
                set(SB_COMPETITION_ID, analyticsModel.competitionId)
                set(SB_COMPETITION_NAME, analyticsModel.competitionName)
            }
        )

        data object DepositBackAction : Action(
            OBG_ACTION_DEPOSIT_BACK,
            { defaultFirebaseParams() }
        )

        data class StatsLoadingTime(
            val loadingFrames: String
        ) : Action(
            SB_STATS_LOADING_TIME,
            {
                defaultFirebaseParams()
                set(SB_APP_LOADER_LOAD_IFRAME_DURATION, loadingFrames)
            }
        )

        sealed class AuthWithBankId(
            configurator: EventConfiguration
        ) : Action(
            OBG_ACTION_DEPOSIT_BANKID_VERIFY,
            configurator
        ) {
            data class Error(
                val error: String
            ) : AuthWithBankId(
                configurator = {
                    defaultFirebaseParams()
                    set(OBG_PROP_ERROR, error)
                }
            )

            data class Full(
                val amount: String,
                val provider: String,
                val error: String
            ) : AuthWithBankId(
                configurator = {
                    defaultFirebaseParams()
                    set(OBG_PROP_DEPOSIT_AMOUNT, amount)
                    set(OBG_PROP_DEPOSIT_PROVIDER, provider)
                    set(OBG_PROP_ERROR, error)
                }
            )
        }

        sealed class DepositAction(
            val action: String,
            val eventConfiguration: EventConfiguration
        ) : Action(
            action,
            configuration = eventConfiguration
        ) {
            data class Initiated(
                val depositAmount: String,
                val depositAdjustedAmount: String,
                val depositProvider: String,
                val error: String
            ) : DepositAction(
                action = OBG_ACTION_DEPOSIT_INITIATED,
                eventConfiguration = {
                    defaultFirebaseParams()
                    set(OBG_PROP_DEPOSIT_AMOUNT, depositAmount)
                    set(OBG_PROP_ADJUSTED_DEPOSIT_AMOUNT, depositAdjustedAmount)
                    set(OBG_PROP_DEPOSIT_PROVIDER, depositProvider)
                    set(OBG_PROP_ERROR, error)
                }
            )

            data class SelectPaymentProvider(
                val provider: String,
            ) : DepositAction(
                action = OBG_ACTION_PAYMENT_PROVIDER_SELECT,
                eventConfiguration = {
                    defaultFirebaseParams()
                    set(OBG_PROP_DEPOSIT_PROVIDER, provider)
                }
            )

            data class QuickSelectAmount(
                val amount: String,
            ) : DepositAction(
                action = OBG_ACTION_QUICK_SELECT_AMOUNT,
                eventConfiguration = {
                    defaultFirebaseParams()
                    set(OBG_PROP_DEPOSIT_AMOUNT, amount)
                }
            )

            data class ManualAmount(
                val amount: String,
                val error: String,
            ) : DepositAction(
                action = OBG_ACTION_DEPOSIT_MANUAL_AMOUNT,
                eventConfiguration = {
                    defaultFirebaseParams()
                    set(OBG_PROP_DEPOSIT_AMOUNT, amount)
                    set(OBG_PROP_ERROR, error)
                }
            )

            data class LimitSet(
                val daily: String,
                val weekly: String,
                val monthly: String,
                val error: String,
            ) : DepositAction(
                action = OBG_ACTION_DEPOSIT_LIMIT_SET,
                eventConfiguration = {
                    defaultFirebaseParams()
                    set(OBG_PROP_DEPOSIT_DAILY_LIMIT, daily)
                    set(OBG_PROP_DEPOSIT_WEEKLY_LIMIT, weekly)
                    set(OBG_PROP_DEPOSIT_MONTHLY_LIMIT, monthly)
                    set(OBG_PROP_ERROR, error)
                }
            )

            data class DailyLimitChange(
                val limit: String
            ) : DepositAction(
                action = OBG_ACTION_DAILY_LIMIT_CHANGE,
                eventConfiguration = {
                    defaultFirebaseParams()
                    set(OBG_PROP_DEPOSIT_DAILY_LIMIT, limit)
                }
            )

            data class WeeklyLimitChange(
                val limit: String
            ) : DepositAction(
                action = OBG_ACTION_WEEKLY_LIMIT_CHANGE,
                eventConfiguration = {
                    defaultFirebaseParams()
                    set(OBG_PROP_DEPOSIT_WEEKLY_LIMIT, limit)
                }
            )

            data class MonthlyLimitChange(
                val limit: String
            ) : DepositAction(
                action = OBG_ACTION_MONTHLY_LIMIT_CHANGE,
                eventConfiguration = {
                    defaultFirebaseParams()
                    set(OBG_PROP_DEPOSIT_MONTHLY_LIMIT, limit)
                }
            )

            data class LimitCheck(
                val amountAllowed: String,
                val depositAmount: String,
                val depositAdjustedAmount: String,
            ) : DepositAction(
                action = OBG_ACTION_DEPOSIT_LIMIT_CHECK,
                eventConfiguration = {
                    defaultFirebaseParams()
                    set(OBG_PROP_IS_DEPOSIT_AMOUNT_ALLOWED, amountAllowed)
                    set(OBG_PROP_DEPOSIT_AMOUNT, depositAmount)
                    set(OBG_PROP_ADJUSTED_DEPOSIT_AMOUNT, depositAdjustedAmount)
                }
            )

            data object Success : DepositAction(
                action = OBG_ACTION_BANKID_SUCCESS,
                eventConfiguration = {
                    defaultFirebaseParams()
                }
            )

            data object Cancel : DepositAction(
                action = OBG_ACTION_DEPOSIT_CANCEL,
                eventConfiguration = {
                    defaultFirebaseParams()
                }
            )

            sealed class Completed(
                val completedAction: String,
                val model: DepositCompletedAnalyticsModel
            ) : DepositAction(
                action = completedAction,
                eventConfiguration = {
                    defaultFirebaseParams()
                    set(OBG_PROP_DEPOSIT_PAYMENT_TYPE, model.paymentType)
                    set(OBG_PROP_CURRENCY, model.currency)
                    set(OBG_PROP_DEPOSIT_AMOUNT, model.amount)
                    set(OBG_PROP_DEPOSIT_IS_FIRST_DEPOSIT, model.isFirstDeposit)
                    set("af_$OBG_PROP_PAYMENT_TYPE", model.paymentType)
                    set("af_$OBG_PROP_PAYMENT_REFERENCE", model.paymentReference)
                    set("af_$OBG_PROP_REVENUE", model.amount)
                    service(serviceId = Analytics.APPS_FLYER) {
                        name { "af_$completedAction" }
                    }
                }
            ) {
                /**
                 * Forcing the serviceId to null to allow the eventConfiguration
                 * implements both services: appflyer & firebase analytics
                 */
                override val serviceId = null

                data class TotalConfirmed(
                    val analyticsModel: DepositCompletedAnalyticsModel
                ) : Completed(
                    completedAction = OBG_ACTION_TOTAL_DEPOSIT_CONFIRMED,
                    model = analyticsModel
                )

                data class Confirmed(
                    val analyticsModel: DepositCompletedAnalyticsModel
                ) : Completed(
                    completedAction = OBG_ACTION_DEPOSIT_CONFIRMED,
                    model = analyticsModel
                )

                data class FirstDeposit(
                    val analyticsModel: DepositCompletedAnalyticsModel
                ) : Completed(
                    completedAction = OBG_ACTION_FIRST_DEPOSIT,
                    model = analyticsModel
                )
            }
        }

        data object LimitRemoveAction : Action(
            OBG_ACTION_LIMIT_REMOVE,
            {
                defaultFirebaseParams()
            }
        )

        data object LimitSeAction : Action(
            OBG_ACTION_LIMIT_SET,
            {
                defaultFirebaseParams()
            }
        )

        data object ErrorTryAgain : Action(
            OBG_ACTION_ERROR_TRY_AGAIN,
            {
                defaultFirebaseParams()
            }
        )

        data object ErrorGoHome : Action(
            OBG_ACTION_ERROR_GO_HOME,
            {
                defaultFirebaseParams()
            }
        )

        data object ErrorHelp : Action(
            OBG_ACTION_ERROR_HELP,
            {
                defaultFirebaseParams()
            }
        )

        data class LoginIntentAction(
            val method: String,
            val isBiometricEnabled: Boolean?,
            val isRememberMeOn: Boolean?
        ) : Action(
            OBG_ACTION_LOGIN_INTENT,
            {
                defaultFirebaseParams()
                set(OBG_PROP_LOGIN_METHOD, method)
                isBiometricEnabled?.also {
                    set(OBG_PROP_BIOMETRICS_ENABLED, it.toString())
                }
                isRememberMeOn?.also {
                    set(OBG_PROP_REMEMBER_ME, it.toString())
                }
            }
        )

        data class LoginTabClickAction(
            val tabName: String,
            val isBiometricEnabled: String,
            val isRememberMeActivated: String
        ) : Action(
            OBG_ACTION_LOGIN_TAB_CLICKED,
            {
                defaultFirebaseParams()
                set(OBG_PROP_LOGIN_TAB, tabName)
                set(OBG_PROP_BIOMETRICS_ENABLED, isBiometricEnabled)
                set(OBG_PROP_REMEMBER_ME, isRememberMeActivated)
            }
        )

        data class BankIdOpenAction(
            val isBiometricEnabled: String,
            val isRememberMeActivated: String
        ) : Action(
            OBG_ACTION_BANK_ID_OPEN,
            {
                defaultFirebaseParams()
                set(OBG_PROP_BIOMETRICS_ENABLED, isBiometricEnabled)
                set(OBG_PROP_REMEMBER_ME, isRememberMeActivated)
            }
        )

        data object SplashChooseBetweenAction : Action(
            OBG_ACTION_SPLASH_CHOOSE_BETWEEN,
            {
                defaultFirebaseParams()
            }
        )

        data class RegistrationTickBoxChangedAction(
            val boxChangedValue: String,
            val isChecked: String
        ) : Action(
            OBG_ACTION_REGISTRATION_TICK_BOX_CHANGED,
            {
                defaultFirebaseParams()
                set(OBG_PROP_REGISTRATION_BOX_CHANGED, boxChangedValue)
                set(OBG_PROP_REGISTRATION_BOX_CHANGED_TO, isChecked)
            }
        )

        data object OpenGameAction : Action(
            OBG_ACTION_HOME_OPEN_GAME,
            {
                defaultFirebaseParams()
            }
        )

        data class BankIdMissingAction(
            val viewName: String = ""
        ) : Action(
            OBG_ACTION_BANKID_MISSING,
            {
                defaultFirebaseParams()
                if (viewName.isNotEmpty()) {
                    set(OBG_PROP_FROM_SCREEN, viewName)
                }
            }
        )

        data object RegistrationNotSubmittedAction : Action(
            OBG_ACTION_REGISTRATION_NOT_SUBMITTED,
            {
                defaultFirebaseParams()
            }
        )

        data class ApplySortingAction(
            val sortType: String,
            val gtmSortType: String,
        ) : Action(
            OBG_ACTION_GAME_SORT,
            {
                defaultFirebaseParams()
                set(OBG_PROP_SORT_TYPE, sortType)
                set(OBG_PROP_GTM_SORT_TYPE, gtmSortType)
            }
        )

        data class BonusSelectOnlyDeposit(
            val numberOfBonuses: String,
        ) : Action(
            OBG_ACTION_SELECT_BONUS_ONLY_DEPOSIT,
            {
                defaultFirebaseParams()
                set(OBG_PROP_NUMBER_OF_BONUSES, numberOfBonuses)
            }
        )

        data class BonusViewedAction(
            val bonusId: String,
            val bonusState: String,
            val bonusType: String,
        ) : Action(
            OBG_ACTION_BONUS_VIEWED,
            {
                bonusActionEventConfiguration(bonusId = bonusId, bonusType = bonusType)
            }
        )

        data class BonusCtaClickAction(
            val bonusId: String,
            val bonusState: String,
            val bonusType: String,
        ) : Action(
            OBG_ACTION_BONUS_CTA_CLICKED,
            {
                bonusActionEventConfiguration(bonusId = bonusId, bonusType = bonusType)
                set(OBG_PROP_BONUS_STATE, bonusState)
            }
        )

        data class BonusForfeitedAction(
            val bonusId: String,
            val bonusState: String,
            val bonusType: String,
            val bonusWagered: String,
            val bonusExpDate: String

        ) : Action(
            OBG_ACTION_BONUS_FORFEITED,
            {
                bonusActionEventConfiguration(bonusId = bonusId, bonusType = bonusType)
                set(OBG_PROP_BONUS_STATE, bonusState)
            }
        )

        data class BonusTocViewedAction(
            val bonusId: String,
            val bonusState: String,
            val bonusType: String,

        ) : Action(
            OBG_ACTION_BONUS_TOC_VIEWED,
            {
                bonusActionEventConfiguration(bonusId = bonusId, bonusType = bonusType)
                set(OBG_PROP_BONUS_STATE, bonusState)
            }
        )

        data class BonusAdditionalOkAction(
            val bonusId: String,
            val amount: String,
            val bonusType: String,
        ) : Action(
            OBG_ACTION_BONUS_ADDITIONAL_OK,
            {
                bonusActionEventConfiguration(bonusId = bonusId, bonusType = bonusType)
                set(OBG_PROP_BONUS_SELECTED_AMOUNT, amount)
            }
        )

        data class BonusAdditionalCancelAction(
            val bonusId: String,
            val amount: String,
            val bonusType: String,
        ) : Action(
            OBG_ACTION_BONUS_ADDITIONAL_CANCEL,
            {
                bonusActionEventConfiguration(bonusId = bonusId, bonusType = bonusType)
                set(OBG_PROP_BONUS_SELECTED_AMOUNT, amount)
            }
        )

        data class BonusQuickAmountAction(
            val bonusId: String,
            val bonusSelectedAmount: String,
            val bonusType: String,
        ) : Action(
            OBG_ACTION_BONUS_QUICK_AMOUNT,
            {
                bonusActionEventConfiguration(bonusId = bonusId, bonusType = bonusType)
                set(OBG_PROP_BONUS_SELECTED_AMOUNT, bonusSelectedAmount)
            }
        )

        sealed class BonusClaimedAction(
            configuration: EventConfiguration,
        ) : Action(
            OBG_ACTION_BONUS_CLAIMED,
            configuration = configuration
        ) {
            data class Simple(
                val bonusType: String,
                val bonusId: String
            ) : BonusClaimedAction(
                {
                    bonusActionEventConfiguration(bonusId = bonusId, bonusType = bonusType)
                }
            )

            data class LockFunds(
                val bonusId: String,
                val amount: String,
                val bonusType: String,
            ) : BonusClaimedAction(
                {
                    bonusActionEventConfiguration(bonusId = bonusId, bonusType = bonusType)
                    set(OBG_PROP_BONUS_SELECTED_AMOUNT, bonusType)
                }
            )
        }

        data class SetUserIdAction(
            val userId: String,
        ) : Action(
            OBG_PROP_USER_ID,
            {
                set(OBG_PROP_USER_ID, userId)
            }
        )

        data class SelectItemHomeAction(
            val analyticModel: HomeItemAnalyticsModel
        ) : Action(
            OBG_ACTION_HOME_SELECT_ITEM,
            {
                set(OBG_PROP_ITEM_CTA, analyticModel.itemCTA)
                set(OBG_PROP_ITEM_CTA_TEXT, analyticModel.itemCTAText)
                set(OBG_PROP_ITEM_CAPTION, analyticModel.itemCaption)
                set(OBG_PROP_ITEM_ID, analyticModel.itemId)
                set(OBG_PROP_ITEM_SECTION_ID, analyticModel.itemSectionId)
                set(OBG_PROP_GTM_ITEM_SECTION_ID, analyticModel.itemSectionId.processTextForGTM())
                set(OBG_PROP_ITEM_SECTION_TYPE, analyticModel.itemSectionType)
                set(OBG_PROP_ITEM_TITLE, analyticModel.itemTitle)
                set(OBG_PROP_GTM_ITEM_TITLE, analyticModel.itemTitle.processTextForGTM())
                set(OBG_PROP_ITEM_TYPE, analyticModel.itemType)
                set(OBG_PROP_ITEM_VIDEO_URL, analyticModel.itemVideoUrl)
                require(OBG_PROP_ACCOUNT_TYPE)
            }
        )

        data class SwitcherClickAction(
            val eventName: String,
            val subCategoryId: String
        ) : Action(
            SB_EVENT_SWITCHER_CLICK,
            {
                defaultFirebaseParams()
                set(SB_EVENT_NAME, eventName)
                set(SB_SUBCAT_ID, subCategoryId)
            }
        )

        data class SearchResultClickAction(
            val event: String,
            val searchPosition: String,
            val resultTitle: String
        ) : Action(
            SB_SEARCH_RESULT_CLICK,
            {
                set(SB_SEARCH_RESULT, event)
                set(SB_SEARCH_EVENT_NAME, resultTitle)
                set(SB_SEARCH_POSITION, searchPosition)
            }
        )

        data class SwitcherSelectAction(
            val analyticsModel: SwitcherSelectActionAnalyticsModel
        ) : Action(
            SB_EVENT_SWITCHER_SELECT,
            {
                set(SB_EVENT_ID, analyticsModel.eventId)
                set(SB_EVENT_SWITCHER_NAME, analyticsModel.eventSwitcherName)
                set(SB_SUBCAT_ID, analyticsModel.subCategoryId)
                set(SB_SUBCAT_NAME, analyticsModel.subCategoryName)
                set(SB_MENU_NAME, analyticsModel.subMenuName)
            }
        )

        data object ViewGameFilterAction : Action(
            OBG_VIEW_GAME_FILTER,
            {
                require(OBG_PROP_ACCOUNT_TYPE)
                viewNameParams(viewName = null)
            }
        )

        data class RegisterConfirmedEvent(
            val viewName: String,
        ) : Action(
            OBG_ACTION_REGISTRATION_VERIFIED,
            {
                defaultFirebaseParams()
                service(serviceId = Analytics.FIREBASE) {
                    set(OBG_PROP_SIGNIN_METHOD, "BankId")
                }

                service(serviceId = Analytics.APPS_FLYER) {
                    name { "af_$OBG_ACTION_REGISTRATION_VERIFIED" }
                    defaultAnalyticsParams(viewName = viewName, screenId = "0000000")
                }
            }
        ) {
            /**
             * Forcing the serviceId to null to allow the eventConfiguration
             * implements both services: appflyer & firebase analytics
             */
            override val serviceId = null
        }

        data class GameCloseAction(
            val balance: String,
            val analyticsModel: GamePlayAnalyticsExtrasModel
        ) : Action(
            OBG_ACTION_GAME_CLOSE,
            {
                defaultFirebaseParams()
                set(OBG_PROP_BALANCE, balance)
                set(OBG_PROP_GAME_NAME, analyticsModel.gameName)
                set(OBG_PROP_GAME_CATEGORY, analyticsModel.gameCategory)
                set(OBG_PROP_GTM_GAME_CATEGORY, analyticsModel.gtmGameCategory)
                set(OBG_PROP_GTM_GAME_NAME, analyticsModel.gtmGameName)
                set(OBG_PROP_GAME_PROVIDER, analyticsModel.gameProvider)
                set(OBG_PROP_GAME_ID, analyticsModel.gameId)
            }
        )

        data class GamePlayAction(
            val action: String,
            val analyticsModel: GameAnalyticsExtrasModel
        ) : Action(
            action,
            {
                defaultFirebaseParams()
                set(OBG_PROP_GAME_NAME, analyticsModel.gameName)
                set(OBG_PROP_GAME_CATEGORY, analyticsModel.gameCategory.joinToString(":"))
                set(OBG_PROP_GTM_GAME_CATEGORY, analyticsModel.gtmGameCategory.joinToString(":"))
                set(OBG_PROP_GTM_GAME_NAME, analyticsModel.gtmGameName)
                set(OBG_PROP_GAME_PROVIDER, analyticsModel.gameProvider)
                set(OBG_PROP_GAME_ID, analyticsModel.gameId)

                service(serviceId = Analytics.APPS_FLYER) {
                    name { "af_$action" }
                    defaultAnalyticsParams()
                }
            }
        ) {
            /**
             * Forcing the serviceId to null to allow the eventConfiguration
             * implements both services: appflyer & firebase analytics
             */
            override val serviceId = null
        }

        data class GameSearchAction(
            val analyticsModel: GamePlayAnalyticsExtrasModel
        ) : Action(
            OBG_ACTION_GAME_SEARCH,
            {
                defaultFirebaseParams()
                set(OBG_PROP_GAME_NAME, analyticsModel.gameName)
                set(OBG_PROP_GAME_CATEGORY, analyticsModel.gameCategory)
                set(OBG_PROP_GTM_GAME_CATEGORY, analyticsModel.gtmGameCategory)
                set(OBG_PROP_GTM_GAME_NAME, analyticsModel.gtmGameName)
                set(OBG_PROP_GAME_PROVIDER, analyticsModel.gameProvider)
                set(OBG_PROP_GAME_ID, analyticsModel.gameId)
            }
        )

        data class GameRecommendedClickedAction(
            val analyticsModel: GameRecommendedClickedAnalyticsModel
        ) : Action(
            OBG_ACTION_GAME_RECOMMENDED_CLICKED,
            {
                defaultFirebaseParams()
                set(OBG_PROP_GAME_PAGE_NAME, analyticsModel.gamePageName)
                set(OBG_PROP_GAME_PAGE_ID, analyticsModel.gamePageId)
                set(OBG_PROP_GAME_CLICKED_ID, analyticsModel.gameClickedId)
                set(OBG_PROP_GAME_CLICKED_NAME, analyticsModel.gameClickedName)
                set(OBG_PROP_GAME_CLICKED_INDEX, analyticsModel.gameClickedIndex)
            }
        )

        data class GameRecommendedViewedEvent(
            val gamesGamesModule: String,
            val gamePageName: String,
            val gamePageId: String
        ) : Action(OBG_ACTION_GAME_RECOMMENDED_VIEWED, {
            defaultFirebaseParams()
            set(OBG_PROP_GAME_GAMES_MODULE, gamesGamesModule)
            set(OBG_PROP_GAME_PAGE_NAME, gamePageName)
            set(OBG_PROP_GAME_PAGE_ID, gamePageId)
        })

        data class GameRecommendedNoScrollingEvent(
            val gamesGamesModule: String,
            val gamePageName: String,
            val gamePageId: String
        ) : Action(OBG_ACTION_GAME_RECOMMENDED_SWIPE, {
            defaultFirebaseParams()
            set(OBG_PROP_GAME_GAMES_MODULE, gamesGamesModule)
            set(OBG_PROP_GAME_PAGE_NAME, gamePageName)
            set(OBG_PROP_GAME_PAGE_ID, gamePageId)
        })

        data class RateGameAction(
            val analyticsModel: RateGameAnalyticsModel
        ) : Action(OBG_ACTION_GAME_RATE, {
            defaultFirebaseParams()
            set(OBG_PROP_GAME_CATEGORY, analyticsModel.gameCategory)
            set(OBG_PROP_GAME_ID, analyticsModel.gameId)
            set(OBG_PROP_GAME_NAME, analyticsModel.gameName)
            set(OBG_PROP_BALANCE, analyticsModel.balance)
            set(OBG_PROP_GTM_GAME_CATEGORY, analyticsModel.gtmGameCategory)
            set(OBG_PROP_GTM_GAME_NAME, analyticsModel.gtmGameName)
            set(OBG_PROP_GAME_PROVIDER, analyticsModel.gameProvider)
            set(OBG_PROP_GAME_RATE_VALUE, analyticsModel.gameRateValue)
        })

        data class SetFavoriteGameAction(
            val action: String,
            val analyticsModel: SetFavoriteGameAnalyticsModel
        ) : Action(name = action, {
            defaultFirebaseParams()
            set(OBG_PROP_GAME_CATEGORY, analyticsModel.gameCategory.joinToString(":"))
            set(OBG_PROP_GAME_ID, analyticsModel.gameId)
            set(OBG_PROP_GAME_NAME, analyticsModel.gameName)
            set(OBG_PROP_BALANCE, analyticsModel.balance)
        })

        data class DepositFailed(
            val viewName: String,
            val errorMessage: String
        ) : Action(OBG_ACTION_ERROR_DETAILS, {
            defaultFirebaseParams()
            set(OBG_PROP_FROM_SCREEN, viewName)
            set(OBG_PROP_ERROR_MESSAGE, errorMessage)
        })

        data class ConsentPrivacyAction(
            val consentPrivacyGiven: String,
            val consentPrivacyClickedFrom: String
        ) : Action(OBG_ACTION_CONSENT_PRIVACY, {
            defaultFirebaseParams()
            set(OBG_PROP_CONSENT_PRIVACY_GIVEN, consentPrivacyGiven)
            set(OBG_PROP_CONSENT_PRIVACY_CLICKED_FROM, consentPrivacyClickedFrom)
        })

        data object PaymentWebOnDepositDialog :
            Action(OBG_ACTION_GAME_DEPOSIT, { defaultFirebaseParams() })

        data class DisplayAuthScreen(
            val clickedFrom: String
        ) : Action(OBG_ACTION_LOGIN_FORM_OPEN, {
            defaultFirebaseParams()
            set(OBG_PROP_CLICKED_FROM, clickedFrom)
        })

        data class RegistrationFormNext(
            val pageName: String
        ) : Action(OBG_ACTION_REGISTRATION_FORM_NEXT, {
            defaultFirebaseParams()
            set(OBG_PROP_REGISTRATION_FORM_PAGE, pageName)
        })

        data class RegistrationFormOpenAction(
            val originForm: String,
            val jurisdiction: String,
            val registrationType: String,
        ) : Action(OBG_ACTION_REGISTRATION_FORM_OPEN, {
            defaultFirebaseParams()
            set(OBG_PROP_JURISDICTION, jurisdiction)
            set(OBG_PROP_REGISTRATION_TYPE, registrationType)
            set(OBG_PROP_REGISTER_ACCOUNT_CLICKED_FROM, originForm)
        })

        data class RegistrationFieldChangedAction(
            val pageName: String,
            val fieldName: String
        ) : Action(OBG_ACTION_REGISTRATION_FIELD_CHANGED, {
            defaultFirebaseParams()
            set(OBG_PROP_REGISTRATION_FORM_PAGE, pageName)
            set(OBG_PROP_REGISTRATION_FIELD_NAME_CHANGED, fieldName)
        })

        data object RegistrationNextAction : Action(
            OBG_ACTION_REGISTRATION_NEXT,
            {
                defaultFirebaseParams()
            }
        )

        data object RegistrationSubmitAction : Action(
            OBG_ACTION_REGISTRATION_SUBMIT,
            {
                defaultFirebaseParams()
            }
        )

        data object GameTournamentAction :
            Action(OBG_ACTION_GAME_TOURNAMENT, { defaultFirebaseParams() })

        data object ConsentPrivacyChanged :
            Action(OBG_ACTION_CONSENT_PRIVACY_CHANGED, { defaultFirebaseParams() })

        data object LoginWithTokenAction :
            Action(
                OBG_ACTION_ACTIVATE_BANKID_LOGIN,
                {
                    defaultFirebaseParams()
                    set(OBG_PROP_SIGNIN_METHOD, "BankId")
                }
            )

        data class BottomMenuClickAction(
            val menuName: String,
        ) : Action(
            SB_BOTTOM_MENU_CLICK,
            {
                defaultFirebaseParams()
                set(SB_MENU_NAME, menuName)
            }
        )

        data class ShowOneTrustPreferenceCenter(
            val source: String
        ) : Action(OBG_ACTION_CONSENT_PRIVACY_PREFERENCES_CLICKED, {
            defaultFirebaseParams()
            set(OBG_PROP_CONSENT_PRIVACY_CLICKED_FROM, source)
        })

        data class MainNavigationChange(
            val model: OBGAnalyticsMainSportsBookModel
        ) : Action("screen_view", {
            defaultFirebaseParams()
            set(SB_EVENT_NAME, model.eventName)
            set(OBG_PROP_SCREEN_NAME, model.obgPropScreenName)
            set(OBG_PROP_SCREEN_CLASS, model.obgPropScreenClass)
            require(
                OBG_PROP_LOCALE,
                OBG_PROP_BRAND_ID,
                OBG_PROP_APP_PRODUCT,
                OBG_PROP_USER_ID,
                OBG_PROP_APP_PRODUCT
            )
        })

        data object HomeOpenContentAction :
            Action(OBG_ACTION_HOME_OPEN_CONTENT, { defaultFirebaseParams() })

        data object ExternalPageContent : Action("screen_view", {
            defaultFirebaseParams()
        })

        data object LogoutAction : Action(
            OBG_ACTION_LOGOUT,
            {
                defaultFirebaseParams()
            }
        )

        data class ActionTabBarChange(
            val infoModel: OBGAnalyticsTabBarChangeModel
        ) : Action(OBGAnalyticsAction.OBG_ACTION_HOME_TAB_BAR_CHANGE, {
            defaultFirebaseParams()
            set(OBG_PROP_TAB_BAR_FROM, infoModel.tabBarItemFrom.toString())
            set(OBG_PROP_TAB_BAR_TO, infoModel.tabBarItemTo.toString())
            set(OBG_PROP_TAB_BAR_FROM_KEY, infoModel.tabBarItemFromKey)
            set(OBG_PROP_TAB_BAR_TO_KEY, infoModel.tabBarItemToKey)
            set(OBG_PROP_GTM_TAB_BAR_FROM_KEY, infoModel.tabBarItemFromKey.processTextForGTM())
            set(OBG_PROP_GTM_TAB_BAR_TO_KEY, infoModel.tabBarItemToKey.processTextForGTM())
            set(OBG_PROP_GTM_TAB_BAR_TO_KEY, infoModel.tabBarItemToKey.processTextForGTM())
        })

        data class PushNotificationClickedAction(
            val pushNotificationKeyValue: String,
        ) : Action(OBGAnalyticsAction.OBG_ACTION_NOTIFICATION_CLICKED, {
            defaultFirebaseParams()
            set(OBG_PROP_NOTIFICATION_DEEPLINK, pushNotificationKeyValue)
        })

        data object ShowAllTournamentsClickedAction :
            Action(OBGAnalyticsAction.OBG_ACTION_TOURNAMENT_SHOW_ALL_CLICKED, {
                defaultFirebaseParams()
            })

        data class TournamentClickedAction(
            val tournamentId: String
        ) : Action(OBGAnalyticsAction.OBG_ACTION_TOURNAMENT_CLICKED, {
            defaultFirebaseParams()
            set(OBG_PROP_TOURNAMENT_ID, tournamentId)
        })

        data class NotificationDialogAction(
            val isAllowed: String
        ) : Action(OBG_ACTION_NOTIFICATION, {
            defaultFirebaseParams()
            set(OBG_PROP_NOTIFICATION_CONSENT, isAllowed)
        })

        data object OnboardingPopupShown : Action(OBG_ACTION_ONBOARDING_POPUP_SHOWN, {
            defaultFirebaseParams()
        })

        sealed class LoginAction(
            loginMethod: String? = null,
            propEventAction: String,
            viewName: String,
            screenOrientation: String,
            screenResolution: String
        ) : Action(OBG_ACTION_LOGIN_FUNNEL, {
            loginEventConfiguration(
                loginMethod,
                propEventAction,
                viewName,
                screenOrientation,
                screenResolution
            )
        }) {
            data class LoginSuccess(
                val customerId: String,
                val loginStatus: String,
                val balance: String,
                val currency: String,
                val loginMethod: String,
                val propEventAction: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                loginMethod,
                propEventAction,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    loginEventConfiguration(
                        loginMethod,
                        propEventAction,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_GUID_EVENT, customerId)
                    set(OBG_PROP_LOGIN_STATUS, loginStatus)
                    set(OBG_PROP_LOGIN__BALANCE, balance)
                    set(OBG_PROP_LOGIN_CURRENCY, currency)
                }
            }

            data class LoginFailed(
                val error: String,
                val loginMethod: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                loginMethod,
                OBG_LOGIN_FAILED,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    loginEventConfiguration(
                        loginMethod,
                        OBG_LOGIN_FAILED,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_TECHNICAL_ERROR, "Login - $error")
                }
            }

            data class LoginSubmitted(
                val loginMethod: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                loginMethod,
                OBG_LOGIN_SUBMITTED,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class LoginFieldChange(
                val fieldChanged: String,
                val loginMethod: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                loginMethod,
                OBG_LOGIN_FIELD_CHANGE,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    loginEventConfiguration(
                        loginMethod,
                        OBG_LOGIN_FIELD_CHANGE,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_INTERFACE_COMPONENT, fieldChanged)
                }
            }

            data class LoginFormClose(
                val loginMethod: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                loginMethod,
                OBG_LOGIN_FORM_CLOSE,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class LoginFormOpen(
                val loginMethod: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                loginMethod,
                OBG_LOGIN_FORM_OPEN,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class LoginIntention(
                val loginMethod: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                loginMethod,
                OBG_LOGIN_INTENTION,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class LoginForgottenPasswordClick(
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                null,
                OBG_LOGIN_FORGOTTEN_PASSWORD_CLICK,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class LoginForgottenPasswordSuccess(
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                null,
                OBG_LOGIN_FORGOTTEN_PASSWORD_SUCCESS,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class LoginForgottenPasswordFailed(
                val error: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                null,
                OBG_LOGIN_FORGOTTEN_PASSWORD_FAILED,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    loginEventConfiguration(
                        null,
                        OBG_LOGIN_FORGOTTEN_PASSWORD_FAILED,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_TECHNICAL_ERROR, "Login - $error")
                }
            }

            data class LoginCreateAccountClick(
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                null,
                OBG_CREATE_ACCOUNT_CLICK,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class LoginPasswordFormSubmit(
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                null,
                OBG_LOGIN_PASSWORD_SUBMIT,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class LoginResetPasswordFormOpen(
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                null,
                OBG_RESET_PASSWORD_FORM_OPEN,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class LoginResetPasswordFormSubmit(
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                null,
                OBG_RESET_PASSWORD_FORM_SUBMIT,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class LoginResetPasswordFormSuccess(
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                null,
                OBG_RESET_PASSWORD_SUCCESS,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class LoginResetPasswordFormFailed(
                val error: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                null,
                OBG_RESET_PASSWORD_FAILED,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    loginEventConfiguration(
                        null,
                        OBG_RESET_PASSWORD_FAILED,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_TECHNICAL_ERROR, "Login Error - $error")
                }
            }

            data class LoginTermsAndConditionsAccept(
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                null,
                OBG_TERMS_AND_CONDITIONS_ACCEPTANCE,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class LoginTabClicked(
                val tabSelected: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                null,
                OBG_LOGIN_TAB_CLICKS,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    loginEventConfiguration(
                        null,
                        OBG_LOGIN_TAB_CLICKS,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_INTERFACE_COMPONENT, "Login - $tabSelected")
                }
            }

            data class LoginBankIdOpen(
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                OBG_BANK_ID,
                OBG_BANK_ID_FORM_OPEN,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class LoginOpenMobileBankID(
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                OBG_BANK_ID,
                OBG_OPEN_MOBILE_BANK_ID,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class LoginBankIDStart(
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : LoginAction(
                OBG_BANK_ID,
                OBG_BANK_ID_START,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    loginEventConfiguration(
                        OBG_BANK_ID,
                        OBG_BANK_ID_START,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_TECHNICAL_START_TYPE, OBG_SAME_DEVICE)
                }
            }
        }

        sealed class Login(
            loginEventName: String,
            val isFirstLogin: Boolean,
            val loginMethod: String,
            val hasSavedBiometrics: Boolean,
            val isRememberMeChecked: Boolean,
            val loginEventConfiguration: EventConfiguration
        ) : Action(
            loginEventName,
            {
                defaultFirebaseParams()

                set(OBG_PROP_FIRST_LOGIN, isFirstLogin.toString())
                set(OBG_PROP_LOGIN_METHOD, loginMethod)
                set(OBG_PROP_BIOMETRICS_ENABLED, hasSavedBiometrics.toString())
                set(OBG_PROP_REMEMBER_ME, isRememberMeChecked.toString())

                service(Analytics.APPS_FLYER) { name { "af_$it" } }

                loginEventConfiguration()
            }
        ) {
            /**
             * Forcing the serviceId to null to allow the eventConfiguration
             * implements both services: appflyer & firebase analytics
             */
            override val serviceId = null

            class Success(
                isFirstLogin: Boolean,
                loginMethod: String,
                hasSavedBiometrics: Boolean,
                isRememberMeChecked: Boolean,
                jurisdiction: String?,
                sessionLimitCheckPeriodSeconds: Double,
            ) : Login(
                OBG_ACTION_LOGIN_SUCCESSFUL,
                isFirstLogin,
                loginMethod,
                hasSavedBiometrics,
                isRememberMeChecked,
                {
                    set(OBG_PROP_JURISDICTION, jurisdiction.toString())
                    set(
                        OBG_PROP_SESSION_LIMIT_CHECK_PERIOD,
                        sessionLimitCheckPeriodSeconds.toString()
                    )
                }
            )

            class Failure(
                isFirstLogin: Boolean,
                loginMethod: String,
                hasSavedBiometrics: Boolean,
                isRememberMeChecked: Boolean,
                failureReason: String
            ) : Login(
                OBG_ACTION_LOGIN_FAILED,
                isFirstLogin,
                loginMethod,
                hasSavedBiometrics,
                isRememberMeChecked,
                {
                    set(OBG_PROP_LOGIN_FAILURE_REASON, failureReason)
                }
            )
        }

        sealed class RegistrationAction(
            registrationType: String,
            registrationMethod: String?,
            propEventAction: String,
            viewName: String,
            screenOrientation: String,
            screenResolution: String
        ) : Action(OBG_ACTION_REGISTRATION_FUNNEL, {
            registrationEventConfiguration(
                registrationType,
                registrationMethod,
                propEventAction,
                viewName,
                screenOrientation,
                screenResolution
            )
        }) {

            data class RegistrationFailed(
                val error: String,
                val registrationType: String,
                val registrationMethod: String?,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : RegistrationAction(
                registrationType,
                registrationMethod,
                OBG_PROP_REGISTRATION_FAILED_EVENT_ACTION,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    registrationEventConfiguration(
                        registrationType,
                        registrationMethod,
                        OBG_PROP_REGISTRATION_FAILED_EVENT_ACTION,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_TECHNICAL_ERROR, error)
                }
            }

            data class RegistrationSuccess(
                val guidEvent: String,
                val registrationType: String,
                val registrationMethod: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : RegistrationAction(
                registrationType,
                registrationMethod,
                OBG_PROP_REGISTRATION_NRC_EVENT_ACTION,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    registrationEventConfiguration(
                        registrationType,
                        registrationMethod,
                        OBG_PROP_REGISTRATION_NRC_EVENT_ACTION,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_GUID_EVENT, guidEvent)
                }
            }

            data class RegistrationCompleteVerified(
                val guidEvent: String,
                val guidUser: String,
                val registrationType: String,
                val registrationMethod: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : RegistrationAction(
                registrationType,
                registrationMethod,
                OBG_PROP_REGISTRATION_COMPLETE_VERIFIED_EVENT_ACTION,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    registrationEventConfiguration(
                        registrationType,
                        registrationMethod,
                        OBG_PROP_REGISTRATION_COMPLETE_VERIFIED_EVENT_ACTION,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_GUID_EVENT, guidEvent)
                    set(OBG_PROP_GUID_USER, guidUser)
                }
            }

            // unused from previous implementation, keeping it for reference
            data class RegistrationNotSwedenResident(
                val registrationType: String,
                val registrationMethod: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : RegistrationAction(
                registrationType,
                registrationMethod,
                OBG_PROP_REGISTRATION_FROM_FIELDS,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    registrationEventConfiguration(
                        registrationType,
                        registrationMethod,
                        OBG_PROP_REGISTRATION_FROM_FIELDS,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(
                        OBG_PROP_INTERFACE_COMPONENT,
                        OBG_PROP_REGISTRATION_I_AM_NOT_SWEDISH_RESIDENT
                    )
                }
            }

            data class RegistrationChangeRegistrationType(
                val registrationType: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : RegistrationAction(
                registrationType,
                null,
                OBG_PROP_REGISTRATION_FROM_FIELDS,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    registrationEventConfiguration(
                        registrationType,
                        null,
                        OBG_PROP_REGISTRATION_FROM_FIELDS,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_INTERFACE_COMPONENT, OBG_PROP_REGISTRATION_CHANGE)
                }
            }

            data class RegistrationFormFieldError(
                val interfaceComponent: String,
                val error: String,
                val registrationStep: Int,
                val registrationType: String,
                val registrationMethod: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : RegistrationAction(
                registrationType,
                registrationMethod,
                OBG_PROP_REGISTRATION_FROM_FIELD_ERROR,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    registrationEventConfiguration(
                        registrationType,
                        registrationMethod,
                        OBG_PROP_REGISTRATION_FROM_FIELD_ERROR,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_INTERFACE_COMPONENT, interfaceComponent)
                    set(OBG_PROP_TECHNICAL_ERROR, error)
                    set(OBG_PROP_USER_REG_STEP, "Step $registrationStep")
                }
            }

            data class RegistrationActivitySelection(
                val registrationStep: Int,
                val registrationType: String,
                val registrationMethod: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : RegistrationAction(
                registrationType,
                registrationMethod,
                OBG_PROP_REGISTRATION_ACTIVITY_SELECTION_EVENT_ACTION,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    registrationEventConfiguration(
                        registrationType,
                        registrationMethod,
                        OBG_PROP_REGISTRATION_ACTIVITY_SELECTION_EVENT_ACTION,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_INTERFACE_COMPONENT, OBG_PROP_REGISTRATION_COMPONENT_COUNTRY_CODE)
                    set(OBG_PROP_USER_REG_STEP, "Step $registrationStep")
                }
            }

            data class RegistrationFieldChange(
                val interfaceComponent: String,
                val registrationType: String,
                val registrationMethod: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : RegistrationAction(
                registrationType,
                registrationMethod,
                OBG_PROP_REGISTRATION_FROM_FIELDS,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    registrationEventConfiguration(
                        registrationType,
                        registrationMethod,
                        OBG_PROP_REGISTRATION_FROM_FIELDS,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(
                        OBG_PROP_INTERFACE_COMPONENT,
                        "$OBG_PROP_REGISTRATION - $interfaceComponent"
                    )
                }
            }

            data class RegistrationField(
                val interfaceComponent: String,
                val registrationStep: Int,
                val registrationType: String,
                val registrationMethod: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : RegistrationAction(
                registrationType,
                registrationMethod,
                OBG_PROP_REGISTRATION_EVENT_ACTION,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    registrationEventConfiguration(
                        registrationType,
                        registrationMethod,
                        OBG_PROP_REGISTRATION_EVENT_ACTION,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_INTERFACE_COMPONENT, interfaceComponent)
                    set(OBG_PROP_USER_REG_STEP, "Step $registrationStep")
                }
            }

            data class RegistrationFormOpen(
                val registrationType: String,
                val registrationMethod: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : RegistrationAction(
                registrationType,
                registrationMethod,
                OBG_PROP_REGISTRATION_COMPLETE_REGISTRATION_FROM_OPEN_EVENT_ACTION,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class RegistrationSubmit(
                val registrationType: String,
                val registrationMethod: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : RegistrationAction(
                registrationType,
                registrationMethod,
                OBG_PROP_REGISTRATION_SUBMIT_EVENT_ACTION,
                viewName,
                screenOrientation,
                screenResolution
            )
        }

        data class VerificationAction(
            val interfaceComponent: String?,
            val viewName: String,
            val screenOrientation: String,
            val screenResolution: String
        ) : Action(OBG_ACTION_VERIFICATION, {
            funnelEventConfigurator(
                OBG_CONST_NOTIFICATION_VERIFY_ACCOUNT_OPEN,
                viewName,
                screenOrientation,
                screenResolution
            )
            interfaceComponent?.let {
                set(
                    OBG_PROP_INTERFACE_COMPONENT,
                    "$OBG_CONST_NOTIFICATION_VERIFY_ACCOUNT_CLICK $interfaceComponent"
                )
            }
        })

        data class FeatureAction(
            val propEventAction: String,
            val interfaceComponent: String?,
            val viewName: String,
            val screenOrientation: String,
            val screenResolution: String
        ) : Action(OBG_ACTION_FEATURE, {
            funnelEventConfigurator(
                OBG_CONST_NOTIFICATION_VERIFY_ACCOUNT_OPEN,
                viewName,
                screenOrientation,
                screenResolution
            )
            interfaceComponent?.let {
                set(
                    OBG_PROP_INTERFACE_COMPONENT,
                    "$OBG_CONST_NOTIFICATION - $interfaceComponent"
                )
            }
        })

        data class FormConsentAction(
            val propEventAction: String,
            val registrationType: String?,
            val interfaceComponent: String?,
            val viewName: String,
            val screenOrientation: String,
            val screenResolution: String
        ) : Action(OBG_ACTION_CONSENT, {
            funnelEventConfigurator(
                propEventAction,
                viewName,
                screenOrientation,
                screenResolution
            )
            registrationType?.let { set(OBG_PROP_REGISTRATION_TYPE, registrationType) }
            interfaceComponent?.let { set(OBG_PROP_INTERFACE_COMPONENT, interfaceComponent) }
        })

        data class AccountClickAction(
            val interfaceComponent: String,
            val viewName: String,
            val screenOrientation: String,
            val screenResolution: String
        ) : Action(OBG_ACTION_CLICKS, {
            funnelEventConfigurator(
                OBG_MY_ACCOUNT_CLICK,
                viewName,
                screenOrientation,
                screenResolution
            )
            set(OBG_PROP_INTERFACE_COMPONENT, interfaceComponent)
        })

        data class GameShareAction(
            val viewName: String,
            val screenOrientation: String,
            val screenResolution: String
        ) : Action(OBG_ACTION_GAMING_FUNNEL, {
            funnelEventConfigurator(
                OBG_PROP_SHARE_THE_GAME_EVENT_ACTION,
                viewName,
                screenOrientation,
                screenResolution
            )
        })

        sealed class GamesAction(
            propEventAction: String,
            viewName: String,
            screenOrientation: String,
            screenResolution: String
        ) : Action(OBG_ACTION_GAMES, {
            funnelEventConfigurator(
                propEventAction,
                viewName,
                screenOrientation,
                screenResolution
            )
        }) {

            data class PlayGameAction(
                val propEventAction: String,
                val gameName: String,
                val gameCategory: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : GamesAction(propEventAction, viewName, screenOrientation, screenResolution) {
                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        propEventAction,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_GAME_NAME_CAPS, gameName)
                    set(OBG_PROP_GAME_CATEGORY_CAPS, gameCategory)
                    set(OBG_PROP_INTERFACE_WIDGETNAME, "Casino")
                }
            }

            data class ViewAllGamesAction(
                val propEventAction: String,
                val interfaceComponent: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : GamesAction(propEventAction, viewName, screenOrientation, screenResolution) {
                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        propEventAction,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_INTERFACE_COMPONENT, interfaceComponent)
                }
            }
        }

        sealed class ClickAction(
            viewName: String,
            screenOrientation: String,
            screenResolution: String
        ) : Action(OBG_ACTION_CLICKS, {
            funnelEventConfigurator("", viewName, screenOrientation, screenResolution)
        }) {

            data class GeneralClickAction(
                val propEventAction: String,
                val interfaceComponent: String?,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : ClickAction(viewName, screenOrientation, screenResolution) {
                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        propEventAction,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    interfaceComponent?.let {
                        set(
                            OBG_PROP_INTERFACE_COMPONENT,
                            interfaceComponent
                        )
                    }
                }
            }

            data class GameClickAction(
                val propEventAction: String,
                val interfaceComponent: String,
                val gameName: String,
                val gameCategory: String,
                val widgetName: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : ClickAction(viewName, screenOrientation, screenResolution) {
                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        propEventAction,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_INTERFACE_COMPONENT, interfaceComponent)
                    set(OBG_PROP_GAME_NAME_CAPS, gameName)
                    set(OBG_PROP_GAME_CATEGORY_CAPS, gameCategory)
                    set(OBG_PROP_INTERFACE_WIDGETNAME, widgetName)
                }
            }

            data class CarouselClickAction(
                val slideNumber: String,
                val direction: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : ClickAction(viewName, screenOrientation, screenResolution) {
                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        "$OBG_CONST_CAROUSEL_NAVIGATION_CLICK Home Screen",
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_CONST_SLIDE_NUMBER, slideNumber)
                    set(OBG_CONST_DIRECTION, direction)
                }
            }

            // unused from previous implementation, keeping it for reference
            data class FilterAction(
                val productName: String,
                val filterItem: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : ClickAction(viewName, screenOrientation, screenResolution) {

                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        "$productName - Product Filter Click",
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(
                        OBG_PROP_INTERFACE_COMPONENT,
                        "Interface_Component: $productName - Product Filter Click - $filterItem"
                    )
                }
            }
        }

        sealed class SearchAction(
            viewName: String,
            screenOrientation: String,
            screenResolution: String
        ) : Action(OBG_ACTION_SEARCH, {
            funnelEventConfigurator(
                "",
                viewName,
                screenOrientation,
                screenResolution
            )
        }) {

            data class ClickSearchAction(
                val filter: String,
                val gameName: String,
                val gameCategory: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : SearchAction(viewName, screenOrientation, screenResolution) {
                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        OBG_CONST_GAME_CLICK,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_INTERFACE_SEARCH_TERM, filter)
                    set(OBG_PROP_GAME_NAME, gameName)
                    set(OBG_PROP_GAME_CATEGORY, gameCategory)
                }
            }

            data class GameSearchAction(
                val filter: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : SearchAction(viewName, screenOrientation, screenResolution) {
                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        OBG_CONST_GAME_SEARCH,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_INTERFACE_SEARCH_TERM, filter)
                }
            }

            data class GamesSearchAction(
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : SearchAction(viewName, screenOrientation, screenResolution) {
                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        OBG_ACTION_GAMES,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_INTERFACE_SEARCH_TERM, "Show All Titles")
                }
            }
        }

        sealed class PaymentAction(
            propEventAction: String,
            viewName: String,
            screenOrientation: String,
            screenResolution: String
        ) : Action(OBG_ACTION_PAYMENTS, {
            funnelEventConfigurator(propEventAction, viewName, screenOrientation, screenResolution)
        }) {

            data class GeneralPaymentAction(
                val propEventAction: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : PaymentAction(propEventAction, viewName, screenOrientation, screenResolution)

            sealed class PaymentMethodAction(
                propEventAction: String,
                paymentMethod: String,
                interfaceComponent: String?,
                viewName: String,
                screenOrientation: String,
                screenResolution: String
            ) : PaymentAction(propEventAction, viewName, screenOrientation, screenResolution) {

                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        propEventAction,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_PAYMENT_METHOD, paymentMethod)
                    interfaceComponent?.let {
                        set(
                            OBG_PROP_INTERFACE_COMPONENT,
                            interfaceComponent
                        )
                    }
                }

                data class GeneralPaymentMethodAction(
                    val propEventAction: String,
                    val paymentMethod: String,
                    val interfaceComponent: String?,
                    val viewName: String,
                    val screenOrientation: String,
                    val screenResolution: String
                ) : PaymentMethodAction(
                    propEventAction,
                    paymentMethod,
                    interfaceComponent,
                    viewName,
                    screenOrientation,
                    screenResolution
                )

                data class PaymentMethodAmountAction(
                    val propEventAction: String,
                    val paymentMethod: String,
                    val amount: String,
                    val currency: String,
                    val viewName: String,
                    val screenOrientation: String,
                    val screenResolution: String
                ) : PaymentMethodAction(
                    propEventAction,
                    paymentMethod,
                    null,
                    viewName,
                    screenOrientation,
                    screenResolution
                ) {
                    override val configuration: EventConfiguration = {
                        funnelEventConfigurator(
                            propEventAction,
                            viewName,
                            screenOrientation,
                            screenResolution
                        )
                        set(OBG_PROP_PAYMENT_SEL_CURRENCY, amount)
                        set(OBG_PROP_PAYMENT_SEL_AMOUNT, currency)
                    }
                }
            }

            data class PaymentErrorAction(
                val propEventAction: String,
                val error: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : PaymentAction(propEventAction, viewName, screenOrientation, screenResolution) {

                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        propEventAction,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_TECHNICAL_ERROR, error)
                }
            }

            data class PaymentLimitChangeAction(
                val interfaceComponent: String,
                val amount: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : PaymentAction(
                OBG_CONST_DEPOSIT_LIMITS_CHANGE,
                viewName,
                screenOrientation,
                screenResolution
            ) {

                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        OBG_CONST_DEPOSIT_LIMITS_CHANGE,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(
                        OBG_PROP_INTERFACE_COMPONENT,
                        "$OBG_CONST_DEPOSIT_LIMITS_CHANGE - $interfaceComponent"
                    )
                    set(OBG_PROP_PAYMENT_SEL_AMOUNT, amount)
                }
            }

            data class PaymentTrackAction(
                val propEventAction: String,
                val paymentId: String,
                val bonusType: String?,
                val bonusTitle: String?,
                val paymentMethod: String,
                val currency: String,
                val amount: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : PaymentAction(propEventAction, viewName, screenOrientation, screenResolution) {

                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        propEventAction,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_PAYMENT_DEPOSIT_ID, paymentId)
                    bonusType?.let { set(OBG_PROP_BONUS_TYPE_CAP, bonusType) }
                    bonusTitle?.let { set(OBG_PROP_BONUS_TITLE, bonusTitle) }
                    set(OBG_PROP_PAYMENT_METHOD, paymentMethod)
                    set(OBG_PROP_PAYMENT_CURRENCY, currency)
                    set(OBG_PROP_PAYMENT_AMOUNT, amount)
                }
            }

            data class PaymentSwishAction(
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : PaymentAction(
                OBG_CONST_SWISH_APP_START,
                viewName,
                screenOrientation,
                screenResolution
            ) {

                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        OBG_CONST_SWISH_APP_START,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_TECHNICAL_START_TYPE, OBG_SAME_DEVICE)
                }
            }
        }

        sealed class MessagesAction(
            propEventAction: String,
            viewName: String,
            screenOrientation: String,
            screenResolution: String
        ) : Action(OBG_ACTION_MESSAGES, {
            funnelEventConfigurator(propEventAction, viewName, screenOrientation, screenResolution)
        }) {
            data class TabChangedAction(
                val selectedTabName: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : MessagesAction(
                OBG_ACTION_MESSAGE_TAB_CHANGED,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        OBG_ACTION_MESSAGE_TAB_CHANGED,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_TAB_CLICKED, selectedTabName)
                }
            }

            data class MessageViewedAction(
                val messageId: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : MessagesAction(
                OBG_ACTION_MESSAGE_VIEWED,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        OBG_ACTION_MESSAGE_VIEWED,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_MESSAGE_ID, messageId)
                }
            }

            data class MessageDeletedAction(
                val messageId: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : MessagesAction(
                OBG_ACTION_MESSAGE_DELETED,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        OBG_ACTION_MESSAGE_DELETED,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_MESSAGE_ID, messageId)
                }
            }

            data class MessageClickedAction(
                val messageId: String,
                val ctaType: String?,
                val ctaUrl: String?,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : MessagesAction(
                OBG_ACTION_MESSAGE_CLICKED,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        OBG_ACTION_MESSAGE_CLICKED,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_MESSAGE_ID, messageId)
                    set(OBG_PROP_CTA_TYPE, messageId)
                    set(OBG_PROP_CTA_URL, messageId)
                }
            }
        }

        sealed class SportsBookAction(
            propEventAction: String,
            viewName: String,
            screenOrientation: String,
            screenResolution: String
        ) : Action(OBG_ACTION_SPORTSBOOK, {
            funnelEventConfigurator(propEventAction, viewName, screenOrientation, screenResolution)
        }) {

            data class SportsbookBetConfirmedAction(
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : SportsBookAction(
                OBG_CONST_BET_CONFIRMED,
                viewName,
                screenOrientation,
                screenResolution
            )

            data class SportsbookEventAction(
                val propEventAction: String,
                val interfaceComponent: String?,
                val interfaceSection: String?,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : SportsBookAction(propEventAction, viewName, screenOrientation, screenResolution) {

                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        propEventAction,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    interfaceComponent?.let {
                        set(
                            OBG_PROP_INTERFACE_COMPONENT,
                            interfaceComponent
                        )
                    }
                    interfaceSection?.let { set(OBG_PROP_INTERFACE_SECTION_NAME, interfaceSection) }
                }
            }

            data class SportsbookEventDetailsAction(
                val propEventAction: String,
                val interfaceComponent: String,
                val eventDetails: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : SportsBookAction(propEventAction, viewName, screenOrientation, screenResolution) {

                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        propEventAction,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_INTERFACE_COMPONENT, interfaceComponent)
                    set(SB_PROP_EVENT_DETAILS, eventDetails)
                }
            }

            data class SportsbookCompetitionDetailsAction(
                val propEventAction: String,
                val eventDetails: String,
                val categoryDetails: String,
                val competitionDetails: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : SportsBookAction(propEventAction, viewName, screenOrientation, screenResolution) {

                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        propEventAction,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(SB_PROP_EVENT_DETAILS, eventDetails)
                    set(SB_PROP_CATEGORY_DETAILS, categoryDetails)
                    set(SB_PROP_COMPETITION_DETAILS, competitionDetails)
                }
            }

            data class SportsbookCategoryAction(
                val propEventAction: String,
                val categoryDetails: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : SportsBookAction(propEventAction, viewName, screenOrientation, screenResolution) {

                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        propEventAction,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(SB_PROP_CATEGORY_DETAILS, categoryDetails)
                }
            }

            data class SportsbookPlaceBetAction(
                val propEventAction: String,
                val betType: String,
                val rememberStakeFlag: String,
                val couponType: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : SportsBookAction(propEventAction, viewName, screenOrientation, screenResolution) {

                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        propEventAction,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(SB_PROP_BET_TYPE, betType)
                    set(SB_PROP_REMEMBER_STAKE_FLAG, rememberStakeFlag)
                    set(SB_PROP_COUPON_TYPE, couponType)
                }
            }

            data class SportsbookWidgetClickAction(
                val propEventAction: String,
                val eventPhase: String,
                val eventDetails: String,
                val categoryDetails: String,
                val betType: String,
                val rememberStakeFlag: String,
                val bonusId: String,
                val marketName: String,
                val marketTemplateId: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : SportsBookAction(propEventAction, viewName, screenOrientation, screenResolution) {

                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        propEventAction,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_EVENT_PHASE, eventPhase)
                    set(SB_PROP_EVENT_DETAILS, eventDetails)
                    set(SB_PROP_CATEGORY_DETAILS, categoryDetails)
                    set(SB_PROP_BET_TYPE, betType)
                    set(SB_PROP_REMEMBER_STAKE_FLAG, rememberStakeFlag)
                    set(SB_PROP_BONUS_ID, bonusId)
                    set(SB_PROP_MARKET_NAME, marketName)
                    set(SB_PROP_MARKET_TEMPLATE_ID, marketTemplateId)
                }
            }

            data class BurgerMenuClickAction(
                val interfaceComponent: String,
                val interfaceSection: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : SportsBookAction(
                OBG_CONST_BURGER_MENU_CLICK,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        OBG_CONST_BURGER_MENU_CLICK,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_INTERFACE_COMPONENT, interfaceComponent)
                    set(OBG_PROP_INTERFACE_SECTION_NAME, interfaceSection)
                }
            }

            data class SearchSportsbookAction(
                val searchQuery: String,
                val viewName: String,
                val screenOrientation: String,
                val screenResolution: String
            ) : SportsBookAction(
                OBG_CONST_SPORTSBOOK_SEARCH,
                viewName,
                screenOrientation,
                screenResolution
            ) {
                override val configuration: EventConfiguration = {
                    funnelEventConfigurator(
                        OBG_CONST_SPORTSBOOK_SEARCH,
                        viewName,
                        screenOrientation,
                        screenResolution
                    )
                    set(OBG_PROP_INTERFACE_SEARCH_TERM, searchQuery)
                }
            }
        }

        data class RegistrationConfirmed(
            val method: String = "Regular",
            val customerId: String? = null,
            val currency: String? = null
        ) : Action(OBG_ACTION_REGISTRATION_CONFIRMED, {
            defaultFirebaseParams(customerId = customerId)
            set(AFInAppEventParameterName.REGISTRATION_METHOD, method)

            service(Analytics.APPS_FLYER) {
                name { "af_${AFInAppEventType.COMPLETE_REGISTRATION}" }
                customerId?.also {
                    set(AFInAppEventParameterName.CUSTOMER_USER_ID, it)
                }
                currency?.also {
                    set(AFInAppEventParameterName.CURRENCY, it)
                }
            }
        }) {
            /**
             * Forcing the serviceId to null to allow the eventConfiguration
             * implements both services: appflyer & firebase analytics
             */
            override val serviceId = null
        }

        data class RegistrationFailed(
            val error: String,
        ) : Action(
            OBG_ACTION_REGISTRATION_FAILED,
            {
                defaultFirebaseParams()
                set(OBG_PROP_REGISTRATION_ERROR, error)
            }
        )

        data class RegistrationVerified(
            val signInMethod: String,
            val viewName: String
        ) : Action(OBG_ACTION_REGISTRATION_VERIFIED, {
            defaultFirebaseParams(viewName = viewName)
            set(OBG_PROP_SIGNIN_METHOD, signInMethod)
        })

        sealed class OnboardingModule(
            override val name: String,
            val module: String
        ) : Action(name, {
            defaultFirebaseParams()
            set(OBG_PROP_ONBOARDING_MODULE_TYPE, module)
        }) {
            data class Shown(val moduleType: String) : OnboardingModule(
                OBG_ACTION_ONBOARDING_MODULE_SHOWN,
                moduleType
            )

            data class Clicked(val moduleType: String) : OnboardingModule(
                OBG_ACTION_ONBOARDING_MODULE_CLICKED,
                moduleType
            )
        }

        sealed class HomeDeepLink(
            override val name: String,
            val homeDeepLinkConfigurator: EventConfiguration
        ) : Action(name, { homeDeepLinkConfigurator() }) {

            data class GameUrl(val action: String) : HomeDeepLink(OBG_ACTION_HOME_DEEP_LINK_URL, {
                defaultFirebaseParams()
                set(OBG_ACTION_HOME_OPEN_GAME, action)
            })

            data class Url(val action: String) : HomeDeepLink(
                OBG_ACTION_HOME_DEEP_LINK_URL,
                {
                    defaultFirebaseParams()
                    set(OBG_PROP_DEEPLINK, action)
                    set(OBG_PROP_GTM_DEEPLINK, action.processTextForGTM())
                }
            )

            data class Category(val action: String?) :
                HomeDeepLink(OBG_ACTION_HOME_DEEP_LINK_CATEGORY, {
                    defaultFirebaseParams()
                    set(OBG_PROP_DEEPLINK, action ?: "")
                    set(OBG_PROP_GTM_DEEPLINK, action?.processTextForGTM() ?: "")
                })

            data class More(val action: String) : HomeDeepLink(
                OBG_ACTION_HOME_DEEP_LINK_MORE,
                {
                    defaultFirebaseParams()
                    set(OBG_PROP_DEEPLINK, action)
                    set(OBG_PROP_GTM_DEEPLINK, action.processTextForGTM())
                }
            )
        }

        sealed class CreatePassword(
            override val name: String,
            val actionName: String?,
            val createPasswordConfiguration: EventConfiguration = { defaultFirebaseParams() }
        ) : Action(name, { createPasswordConfiguration() }) {

            override val configuration: EventConfiguration = {
                createPasswordConfiguration()
                actionName?.let {
                    set(OBG_PROP_EVENT_ACTION, it)
                }
                set(OBG_PROP_LOGIN_METHOD_V2, OBG_BANK_ID)
            }

            data object SetNewPassword : CreatePassword(
                name = OBG_ACTION_LOGIN_FUNNEL,
                actionName = OBGAnalyticsProperties.OBG_SET_NEW_PASSWORD
            )

            data object SkipPassword : CreatePassword(
                name = OBG_ACTION_LOGIN_FUNNEL,
                actionName = OBGAnalyticsProperties.OBG_SKIP_PASSWORD
            )

            data object ChangePassword : CreatePassword(
                name = OBGAnalyticsAction.OBG_ACTION_CHANGE_PW,
                actionName = null
            )
        }

        sealed class ChangePassword(
            override val name: String,
            val changePasswordConfiguration: EventConfiguration = { defaultFirebaseParams() }
        ) : Action(name, { changePasswordConfiguration() }) {

            data object Screen : ChangePassword(OBG_VIEW_CHANGE_PW, {
                viewNameParams(null)
            })

            data object Confirmed : ChangePassword(OBG_CONFIRM_CHANGE_PW)

            data object Success : ChangePassword(OBG_CHANGE_PASSWORD_SUCCESS)

            data object Failed : ChangePassword(OBG_CHANGE_PASSWORD_FAILED)

            data object Submit : ChangePassword(OBG_CHANGE_PASSWORD_FORM_SUBMIT)
        }

        data class Promotions(
            val viewName: String? = null
        ) : Action(OBG_ACTION_PROMOTIONS, {
            defaultFirebaseParams(viewName)
        })

        data class SelfAssessment(
            val viewName: String? = null
        ) : Action(OBG_ACTION_SELF_ASSESSMENT, {
            defaultFirebaseParams(viewName)
        })

        data class SelfAssessmentEvaluation(
            val viewName: String? = null
        ) : Action(OBG_ACTION_SELF_ASSESSMENT_EVALUATION, {
            defaultFirebaseParams(viewName)
        })

        data class RemoveSessionLimits(
            val customerId: String
        ) : Action(SGA_PROP_ACTION_CTA_REMOVE_SESSION_LIMITS, {
            defaultFirebaseParams(viewName = OBG_VIEW_SET_LIMIT)
            set(SGA_PROP_CTA_REMOVE_SESSION_LIMITS, customerId)
        })

        sealed class HomeClick(
            val actionName: String,
            sectionId: String,
            ctaAction: String,
            ctaContent: String? = null,
            ctaType: String? = null,
            val homeClickConfiguration: EventConfiguration = { defaultFirebaseParams() }
        ) : Action(OBG_ACTION_CASINO_FUNNEL, { homeClickConfiguration() }) {

            override val configuration: EventConfiguration = {
                homeClickConfiguration()
                set(OBG_PROP_CASINO_EVENT_ACTION, actionName)
                set(OBG_PROP_CASINO_SECTION_ID, sectionId)
                set(OBG_PROP_CASINO_ACTION, ctaAction)
                ctaContent?.let { set(OBG_PROP_CASINO_ITEM_ID, ctaContent) }
                ctaType?.let { set(OBG_PROP_CASINO_ITEM_TYPE, ctaType) }
            }

            data class Item(
                val sectionId: String,
                val ctaAction: String,
                val ctaContent: String,
                val ctaType: String
            ) : HomeClick(
                actionName = OBG_PROP_CASINO_HOME_ITEM_EVENT_ACTION,
                sectionId = sectionId,
                ctaAction = ctaAction,
                ctaContent = ctaContent,
                ctaType = ctaType
            )

            data class Section(
                val sectionId: String,
                val ctaAction: String
            ) : HomeClick(
                actionName = OBG_PROP_CASINO_HOME_SECTION_EVENT_ACTION,
                sectionId = sectionId,
                ctaAction = ctaAction
            )
        }

        data class HomeSelectedItem(
            val viewName: String,
            val cta: String,
            val ctaText: String,
            val caption: String,
            val id: String,
            val sectionId: String,
            val sectionType: String,
            val title: String,
            val type: String,
            val videoUrl: String
        ) : Action(
            name = OBG_ACTION_HOME_SELECT_ITEM,
            {
                defaultFirebaseParams(viewName = viewName)
                set(OBG_PROP_ITEM_CTA, cta)
                set(OBG_PROP_ITEM_CTA_TEXT, ctaText)
                set(OBG_PROP_ITEM_CAPTION, caption)
                set(OBG_PROP_ITEM_ID, id)
                set(OBG_PROP_ITEM_SECTION_ID, sectionId)
                set(OBG_PROP_GTM_ITEM_SECTION_ID, sectionId)
                set(OBG_PROP_ITEM_SECTION_TYPE, sectionType)
                set(OBG_PROP_ITEM_TITLE, title)
                set(OBG_PROP_GTM_ITEM_TITLE, title)
                set(OBG_PROP_ITEM_TYPE, type)
                set(OBG_PROP_ITEM_VIDEO_URL, videoUrl)
            }
        )

        data class ScreenView(
            val contentGroup: String,
            val loginStatus: String,
            val buildType: String,
            val ipCountryCode: String,
            val siteLanguage: String,
            val jurisdiction: String,
            val viewName: String,
            val screenOrientation: String,
            val screenResolution: String
        ) : Action("screen_view", {
            funnelEventConfigurator("", viewName, screenOrientation, screenResolution)
            optional(
                OBG_PROP_USER_ID,
                OBG_PROP_GUID_EVENT,
                OBG_PROP_INTERFACE_SITE_LANGUAGE,
                OBG_PROP_TECHNICAL_RAW_USER_AGENT_INFO
            )
            set(OBG_PROP_CONTENT_GROUP, contentGroup)
            set(OBG_PROP_USER_COUNTRY, ipCountryCode)
            set(OBG_PROP_LOGIN_STATUS, loginStatus)
            set(OBG_PROP_JURISDICTION, jurisdiction)
            set(OBG_PROP_TRAFFIC_TYPE, buildType)
            set(OBG_PROP_INTERFACE_BRAND, buildType)
            set(OBG_PROP_TECHNICAL_EVENT_NAME, "screen_view")
            set(OBG_PROP_TECHNICAL_PLATFORM_ENV, buildType)
            set(OBG_PROP_TECHNICAL_WINDOW_TYPE, "Normal")
            set(OBG_PROP_TECHNICAL_PLATFORM_USED, "Native")
            set(OBG_PROP_TECHNICAL_PLATFORM_DELIVERY, "Android")
            require(OBG_PROP_TECHNICAL_TARGETING_CONSENT)
        })

        data class ProfileImageRemoved(
            val viewName: String
        ) : Action(OBG_ACTION_PROFILE_IMAGE_REMOVE, {
            defaultFirebaseParams(viewName = viewName)
        })

        data class ProfileImageAdded(
            val viewName: String
        ) : Action(OBG_ACTION_PROFILE_IMAGE_ADD, {
            defaultFirebaseParams(viewName = viewName)
        })

        data class ProfileRetrieved(
            val currency: String?,
            val jurisdiction: String?
        ) : Action(OBG_ACTION_PROFILE_RETRIEVE, {
            defaultFirebaseParams()
            set(OBG_PROP_CURRENCY, currency ?: "")
            set(OBG_PROP_JURISDICTION, jurisdiction ?: "")
        })

        open class LogActionEvent(
            action: String,
            viewName: String,
            screenInfo: ScreenInfo
        ) : Action(action, {
            defaultFirebaseParams(viewName)
            set(OBG_PROP_TECHNICAL_SCREEN_ORIENTATION, screenInfo.screenOrientation)
            set(OBG_PROP_TECHNICAL_SCREEN_RESOLUTION, screenInfo.screenResolution)
        }) {

            data class WebPage(
                val action: String,
                val viewName: String,
                val screenInfo: ScreenInfo,
                val title: String
            ) : LogActionEvent(action, viewName, screenInfo) {
                override val configuration: EventConfiguration = {
                    super.configuration(this)
                    set(OBGAnalyticsProperties.OBG_PROP_WEB_PAGE_NAME, title)
                }
            }

            data class GameAction(
                val action: String,
                val viewName: String,
                val gameName: String,
                val gameType: String,
                val screenInfo: ScreenInfo
            ) : LogActionEvent(action, viewName, screenInfo) {
                override val configuration: EventConfiguration = {
                    super.configuration(this)
                    set(OBG_PROP_GAME_NAME, gameName)
                    set(OBG_PROP_GTM_GAME_NAME, gameName.processTextForGTM())
                    set(OBG_PROP_GAME_CATEGORY, gameType)
                }
            }

            data class PaymentAction(
                val action: String,
                val viewName: String,
                val screenInfo: ScreenInfo,
                val paymentType: String,
                val paymentMethod: String,
                val paymentStatus: String,
                val paymentCurrency: String,
                val paymentAmount: String

            ) : LogActionEvent(action, viewName, screenInfo) {
                override val configuration: EventConfiguration = {
                    super.configuration(this)
                    set(OBG_PROP_PAYMENT_TYPE, paymentType)
                    set(OBG_PROP_PAYMENT_METHOD, paymentMethod)
                    set(OBG_PROP_PAYMENT_STATUS, paymentStatus)
                    set(AFInAppEventParameterName.CURRENCY, paymentCurrency)
                    set(AFInAppEventParameterName.REVENUE, paymentAmount)
                }
            }

            data class TournamentEvent(
                val viewName: String,
                val screenInfo: ScreenInfo,
                val event: String,
                val clickedItem: String? = null,
                val tournamentId: String? = null
            ) : LogActionEvent(
                OBGAnalyticsAction.OBG_ACTION_GAME_TOURNAMENTS_EVENT,
                viewName,
                screenInfo
            ) {
                override val configuration: EventConfiguration = {
                    super.configuration(this)
                    set(OBG_PROP_TOURNAMENT_EVENT, event)
                    set(OBG_PROP_TOURNAMENT_EVENT_NAME, "track-ui-interaction")
                    if (clickedItem?.isNotEmpty() == true) {
                        set(OBG_PROP_TOURNAMENT_EVENT_ITEM_CLICKED, clickedItem)
                    }
                    if (tournamentId?.isNotEmpty() == true) {
                        set(OBG_PROP_TOURNAMENT_EVENT_TOURNAMENT_ID, tournamentId)
                    }
                }
            }
        }

        sealed class Web(
            val eventName: String,
            val customerId: String?,
            val currency: String,
            val afEventConfiguration: ServiceSpecificEventConfiguration = {}
        ) : Action(
            eventName,
            {
                defaultFirebaseParams(customerId = customerId)
                service(Analytics.APPS_FLYER) {
                    name { "af_$it" }
                    set(AFInAppEventParameterName.CUSTOMER_USER_ID, customerId.toString())
                    set(AFInAppEventParameterName.CURRENCY, currency)
                    afEventConfiguration()
                }
            }
        ) {
            /**
             * Forcing the serviceId to null to allow the eventConfiguration
             * implements both services: appflyer & firebase analytics
             */
            override val serviceId = null

            class TotalDepositConfirmed(
                customerId: String?,
                currency: String,
                val revenue: String? = null
            ) : Web(
                OBG_ACTION_TOTAL_DEPOSIT_CONFIRMED,
                customerId,
                currency,
                {
                    revenue?.also {
                        set(AFInAppEventParameterName.REVENUE, it)
                    }
                }
            )

            class DepositConfirmed(
                customerId: String?,
                currency: String,
                val revenue: String
            ) : Web(
                OBG_ACTION_DEPOSIT_CONFIRMED,
                customerId,
                currency,
                {
                    set(AFInAppEventParameterName.REVENUE, revenue)
                }
            )

            class FirstDeposit(
                customerId: String?,
                currency: String
            ) : Web(
                OBG_ACTION_FIRST_DEPOSIT,
                customerId,
                currency
            )
        }
    }

    sealed class BonusActionEvent(
        override val name: String,
        val bonusActionConfiguration: EventConfiguration
    ) : Action(name, { bonusActionConfiguration() }) {
        data class NoBonus(
            val viewName: String,
            val numberOfBonuses: String,
            val customerId: String
        ) : BonusActionEvent(
            name = OBG_ACTION_SELECT_BONUS_ONLY_DEPOSIT,
            {
                defaultFirebaseParams(viewName)
                set(OBG_PROP_NUMBER_OF_BONUSES, numberOfBonuses)
                set(OBG_PROP_BONUS_USER_ID, customerId)
            }
        )

        data class BonusSeen(
            val viewName: String,
            val bonusId: String,
            val bonusState: String,
            val bonusType: String,
            val customerId: String
        ) : BonusActionEvent(
            name = OBG_ACTION_BONUS_VIEWED,
            {
                defaultFirebaseParams(viewName)
                set(OBG_PROP_BONUS_ID, bonusId)
                set(OBG_PROP_BONUS_STATE, bonusState)
                set(OBG_PROP_BONUS_TYPE, bonusType)
                set(OBG_PROP_BONUS_USER_ID, customerId)
            }
        )

        data class BonusCtaClick(
            val viewName: String,
            val bonusId: String,
            val bonusState: String,
            val bonusType: String,
            val customerId: String
        ) : BonusActionEvent(
            name = OBG_ACTION_BONUS_CTA_CLICKED,
            {
                defaultFirebaseParams(viewName)
                set(OBG_PROP_BONUS_ID, bonusId)
                set(OBG_PROP_BONUS_STATE, bonusState)
                set(OBG_PROP_BONUS_TYPE, bonusType)
                set(OBG_PROP_BONUS_USER_ID, customerId)
            }
        )

        data class BonusForfeited(
            val viewName: String,
            val bonusId: String,
            val bonusState: String,
            val bonusType: String,
            val bonusWagered: String,
            val bonusExpires: String,
            val customerId: String
        ) : BonusActionEvent(
            name = OBG_ACTION_BONUS_FORFEITED,
            {
                defaultFirebaseParams(viewName)
                set(OBG_PROP_BONUS_ID, bonusId)
                set(OBG_PROP_BONUS_STATE, bonusState)
                set(OBG_PROP_BONUS_TYPE, bonusType)
                set(OBG_PROP_BONUS_WAGERED, bonusWagered)
                set(OBG_PROP_BONUS_EXPIRY_DATE, bonusExpires)
                set(OBG_PROP_BONUS_USER_ID, customerId)
            }
        )

        data class TermsToggleViewed(
            val viewName: String,
            val bonusId: String,
            val bonusState: String,
            val bonusType: String,
            val customerId: String
        ) : BonusActionEvent(
            name = OBG_ACTION_BONUS_TOC_VIEWED,
            {
                defaultFirebaseParams(viewName)
                set(OBG_PROP_BONUS_ID, bonusId)
                set(OBG_PROP_BONUS_STATE, bonusState)
                set(OBG_PROP_BONUS_TYPE, bonusType)
                set(OBG_PROP_BONUS_USER_ID, customerId)
            }
        )

        data class BonusAdditionalOkClick(
            val viewName: String,
            val bonusId: String,
            val bonusState: String,
            val bonusType: String,
            val customerId: String,
            val amount: String
        ) : BonusActionEvent(
            name = OBG_ACTION_BONUS_ADDITIONAL_OK,
            {
                defaultFirebaseParams(viewName)
                set(OBG_PROP_BONUS_ID, bonusId)
                set(OBG_PROP_BONUS_STATE, bonusState)
                set(OBG_PROP_BONUS_TYPE, bonusType)
                set(OBG_PROP_BONUS_USER_ID, customerId)
                set(OBG_PROP_BONUS_SELECTED_AMOUNT, amount)
            }
        )

        data class BonusAdditionalCancelClick(
            val viewName: String,
            val bonusId: String,
            val bonusState: String,
            val bonusType: String,
            val customerId: String,
            val amount: String
        ) : BonusActionEvent(
            name = OBG_ACTION_BONUS_ADDITIONAL_CANCEL,
            {
                defaultFirebaseParams(viewName)
                set(OBG_PROP_BONUS_ID, bonusId)
                set(OBG_PROP_BONUS_STATE, bonusState)
                set(OBG_PROP_BONUS_TYPE, bonusType)
                set(OBG_PROP_BONUS_USER_ID, customerId)
                set(OBG_PROP_BONUS_SELECTED_AMOUNT, amount)
            }
        )

        data class BonusQuickAmountClick(
            val viewName: String,
            val bonusId: String,
            val bonusState: String,
            val bonusType: String,
            val customerId: String,
            val amount: String
        ) : BonusActionEvent(
            name = OBG_ACTION_BONUS_QUICK_AMOUNT,
            {
                defaultFirebaseParams(viewName)
                set(OBG_PROP_BONUS_ID, bonusId)
                set(OBG_PROP_BONUS_STATE, bonusState)
                set(OBG_PROP_BONUS_TYPE, bonusType)
                set(OBG_PROP_BONUS_USER_ID, customerId)
                set(OBG_PROP_BONUS_SELECTED_AMOUNT, amount)
            }
        )

        data class BonusClaimed(
            val viewName: String,
            val bonusId: String,
            val bonusState: String,
            val bonusType: String,
            val customerId: String,
        ) : BonusActionEvent(
            name = OBG_ACTION_BONUS_CLAIMED,
            {
                defaultFirebaseParams(viewName)
                set(OBG_PROP_BONUS_ID, bonusId)
                set(OBG_PROP_BONUS_STATE, bonusState)
                set(OBG_PROP_BONUS_TYPE, bonusType)
                set(OBG_PROP_BONUS_USER_ID, customerId)
            }
        )

        data class LockFoundsClaimed(
            val viewName: String,
            val bonusId: String,
            val bonusState: String,
            val bonusType: String,
            val customerId: String,
            val amount: String,
        ) : BonusActionEvent(
            name = OBG_ACTION_BONUS_CLAIMED,
            {
                defaultFirebaseParams(viewName)
                set(OBG_PROP_BONUS_ID, bonusId)
                set(OBG_PROP_BONUS_STATE, bonusState)
                set(OBG_PROP_BONUS_TYPE, bonusType)
                set(OBG_PROP_BONUS_USER_ID, customerId)
                set(OBG_PROP_BONUS_SELECTED_AMOUNT, amount)
            }
        )
    }

    data class ViewPage(val viewName: String) : Events(FirebaseAnalytics.Event.SCREEN_VIEW) {
        override val serviceId = Analytics.FIREBASE
        override val configuration: EventConfiguration = {
            viewNameParams(viewName)
        }
    }

    data class AccountTypeSimpleViewPage(
        val viewName: String
    ) : Events(FirebaseAnalytics.Event.SCREEN_VIEW) {
        override val serviceId = Analytics.FIREBASE
        override val configuration: EventConfigurator.() -> Unit = {
            viewNameParams(viewName)
            require(OBG_PROP_ACCOUNT_TYPE)
        }
    }

    data class DefaultAnalyticsViewPage(
        val viewName: String
    ) : Events(FirebaseAnalytics.Event.SCREEN_VIEW) {
        override val serviceId = Analytics.FIREBASE
        override val configuration: EventConfigurator.() -> Unit = {
            defaultAnalyticsParams()
        }
    }

    data class LobbyViewPage(
        val lobbyViewPageAnalyticsModel: LobbyViewPageAnalyticsModel,
        val screenParamsAnalyticsModel: DefaultScreenAnalyticsModel
    ) : Events(FirebaseAnalytics.Event.SCREEN_VIEW) {
        override val serviceId = Analytics.FIREBASE

        override val configuration: EventConfigurator.() -> Unit = {
            set(SB_LOBBY_NAME, lobbyViewPageAnalyticsModel.lobbyName)
            set(SB_LOBBY, lobbyViewPageAnalyticsModel.lobby)
            set(SB_VIRTUAL_TITLE, lobbyViewPageAnalyticsModel.virtualTitle)
            set(SB_VIRTUAL_URL, lobbyViewPageAnalyticsModel.virtualUrl)
            set(SB_EVENT_NAME, lobbyViewPageAnalyticsModel.eventName)
            defaultAnalyticsParams()
            // Override basic viewNameParams
            set(OBG_PROP_SCREEN_NAME, screenParamsAnalyticsModel.screenName)
            set(OBG_PROP_SCREEN_CLASS, screenParamsAnalyticsModel.screenClass)
        }
    }

    data object SimpleScreenViewPage : Events(FirebaseAnalytics.Event.SCREEN_VIEW) {
        override val serviceId = Analytics.FIREBASE
        override val configuration: EventConfigurator.() -> Unit = {
            set(SB_EVENT_NAME, SB_SCREEN_VIEW)
        }
    }

    data object SearchViewPage : Events(FirebaseAnalytics.Event.SCREEN_VIEW) {
        override val serviceId = Analytics.FIREBASE
        override val configuration: EventConfigurator.() -> Unit = {
            set(SB_EVENT_NAME, SB_SCREEN_VIEW)
            set(SB_SEARCH, SB_SEARCH_RESULT)
        }
    }

    data object GameViewPage : Events(FirebaseAnalytics.Event.SCREEN_VIEW) {
        override val serviceId = Analytics.FIREBASE
        override val configuration: EventConfigurator.() -> Unit = {
            set(SB_EVENT_NAME, SB_SCREEN_VIEW)
            set(SB_GAME_LIST, SB_EVENT)
            defaultAnalyticsParams()
        }
    }

    companion object {

        fun EventConfigurator.defaultFirebaseParams(
            viewName: String? = null,
            screenId: String = "0000000",
            customerId: String? = null
        ) {
            service(Analytics.FIREBASE) {
                defaultAnalyticsParams(viewName, screenId, customerId)
                require(OBG_PROP_ACCOUNT_TYPE)
            }
        }

        fun PropertyConfigurator.defaultAnalyticsParams(
            viewName: String? = null,
            screenId: String = "0000000",
            customerId: String? = null
        ) {
            if (customerId != null) {
                set(OBG_PROP_USER_ID, customerId)
                set(OBG_PROP_APPSFLYER_CUID, customerId)
            } else {
                optional(
                    OBG_PROP_USER_ID,
                    OBG_PROP_APPSFLYER_CUID,
                    AFInAppEventParameterName.CUSTOMER_USER_ID
                )
            }

            optional(OBG_PROP_DEVICE_TYPE)

            viewNameParams(viewName)
            require(
                OBG_PROP_LOCALE,
                OBG_PROP_BRAND_ID,
                OBG_PROP_APP_PRODUCT,
                OBG_PROP_VERSION_NAME,
                Native_App_Version,
                Native_App_Name,
                OBG_PROP_APPSFLYER_DEV_KEY,
                OBG_PROP_APPSFLYER_ID,
                OBG_PROP_APPSFLYER_AAID
            )
            set(OBG_PROP_SCREEN_IDENTIFIER, screenId)
        }

        fun PropertyConfigurator.viewNameParams(viewName: String?) {
            set(
                OBG_PROP_SCREEN,
                if (viewName.isNullOrEmpty()) "default_screen" else viewName
            )
            set(
                OBG_PROP_SCREEN_NAME,
                if (viewName.isNullOrEmpty()) "default_screen_view" else viewName
            )
            set(
                OBG_PROP_SCREEN_CLASS,
                if (viewName.isNullOrEmpty()) "default_screen_class" else viewName
            )
        }

        fun PropertyConfigurator.require(vararg properties: String) {
            for (propertyName in properties) {
                require(propertyName)
            }
        }

        fun PropertyConfigurator.optional(vararg properties: String) {
            for (propertyName in properties) {
                optional(propertyName)
            }
        }

        fun EventConfigurator.bonusActionEventConfiguration(
            bonusType: String,
            bonusId: String
        ) {
            defaultFirebaseParams()
            set(OBG_PROP_BONUS_ID, bonusId)
            set(OBG_PROP_BONUS_TYPE, bonusType)
        }

        fun EventConfigurator.loginEventConfiguration(
            loginMethod: String?,
            propEventAction: String,
            viewName: String,
            screenOrientation: String,
            screenResolution: String
        ) {
            funnelEventConfigurator(
                propEventAction,
                viewName,
                screenOrientation,
                screenResolution
            )
            loginMethod?.let { set(OBG_PROP_LOGIN_METHOD_V2, loginMethod) }
        }

        fun EventConfigurator.registrationEventConfiguration(
            registrationType: String,
            registrationMethod: String?,
            propEventAction: String,
            viewName: String,
            screenOrientation: String,
            screenResolution: String
        ) {
            funnelEventConfigurator(
                propEventAction,
                viewName,
                screenOrientation,
                screenResolution
            )
            set(OBG_PROP_USER_REGISTRATION_TYPE, registrationType)
            registrationMethod?.let { set(OBG_PROP_USER_REG_METHOD, registrationMethod) }
        }

        private fun EventConfigurator.funnelEventConfigurator(
            propEventAction: String,
            viewName: String,
            screenOrientation: String,
            screenResolution: String
        ) {
            defaultFirebaseParams(viewName = viewName)
            set(OBG_PROP_CONTENT_GROUP, "LOGIN")
            set(OBG_PROP_EVENT_ACTION, propEventAction)
            set(OBG_PROP_TECHNICAL_SCREEN_ORIENTATION, screenOrientation)
            set(OBG_PROP_TECHNICAL_SCREEN_RESOLUTION, screenResolution)
            require(
                OBG_PROP_USER_CUSTOMER_LEVEL,
                OBG_PROP_CUSTOMER_STATES_EVENT,
                OBG_PROP_ALLOW_AD_PERSONALIZATION_SIGNALS,
                OBG_PROP_ALLOW_GOOGLE_SIGNALS
            )
        }
    }
}

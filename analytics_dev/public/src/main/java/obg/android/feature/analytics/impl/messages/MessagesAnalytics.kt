package obg.android.feature.analytics.impl.messages

interface MessagesAnalytics {
    fun logMessageClickAction(
        messageId: String,
        ctaType: String,
        ctaUrl: String
    )
    fun logMessageDeletedAction(
        messageId: String
    )
    fun logMessageTabChangedAction(
        tabClicked: String
    )
    fun logMessageViewedAction(
        messageId: String
    )
}

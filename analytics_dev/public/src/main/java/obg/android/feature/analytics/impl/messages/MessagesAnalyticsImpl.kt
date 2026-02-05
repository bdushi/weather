package obg.android.feature.analytics.impl.messages

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.Events

class MessagesAnalyticsImpl(
    private val analytics: Analytics
) : MessagesAnalytics {
    override fun logMessageClickAction(
        messageId: String,
        ctaType: String,
        ctaUrl: String
    ) = analytics.logEvent(
        event = Events.Action.MessageClickAction(
            messageId = messageId,
            ctaType = ctaType,
            ctaUrl = ctaUrl,
        )
    )

    override fun logMessageDeletedAction(messageId: String) = analytics.logEvent(
        event = Events.Action.MessageDeletedAction(
            messageId = messageId
        )
    )

    override fun logMessageTabChangedAction(tabClicked: String) = analytics.logEvent(
        event = Events.Action.MessageTabChangedAction(
            tabClicked = tabClicked
        )
    )

    override fun logMessageViewedAction(messageId: String) = analytics.logEvent(
        event = Events.Action.MessageViewedAction(
            messageId = messageId
        )
    )
}

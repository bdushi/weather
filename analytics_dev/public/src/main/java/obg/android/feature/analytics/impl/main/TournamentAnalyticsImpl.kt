package obg.android.feature.analytics.impl.main

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.Events

class TournamentAnalyticsImpl(
    private val analytics: Analytics
) : TournamentAnalytics {
    override fun logShowAllTournamentsClicked() = analytics.logEvent(
        event = Events.Action.ShowAllTournamentsClickedAction
    )

    override fun logTournamentClicked(tournamentId: String) = analytics.logEvent(
        event = Events.Action.TournamentClickedAction(
            tournamentId = tournamentId
        )
    )
}

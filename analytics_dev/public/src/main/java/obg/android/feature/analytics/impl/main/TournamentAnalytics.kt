package obg.android.feature.analytics.impl.main

interface TournamentAnalytics {
    fun logShowAllTournamentsClicked()
    fun logTournamentClicked(tournamentId: String)
}

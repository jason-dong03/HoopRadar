package com.main.hoopradar.navigation

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val NEARBY_COURTS = "nearby_courts"
    const val CREATE_RUN = "create_run"
    const val RUN_DETAILS = "run_details/{runId}"
    const val COURT_DETAILS = "court_details"
    const val PROFILE = "profile"
    const val RUN_CHAT = "run_chat/{runId}/{courtName}"

    fun runDetails(runId: String) = "run_details/$runId"
    fun runChat(runId: String, courtName: String) = "run_chat/$runId/${courtName.encodeForNav()}"

    private fun String.encodeForNav() = java.net.URLEncoder.encode(this, "UTF-8")
}

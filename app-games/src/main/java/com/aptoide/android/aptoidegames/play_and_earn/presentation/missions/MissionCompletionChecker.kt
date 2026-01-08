package com.aptoide.android.aptoidegames.play_and_earn.presentation.missions

import cm.aptoide.pt.campaigns.domain.PaEMission
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.SessionContext

interface MissionCompletionChecker {

  fun isLocallyCompleted(mission: PaEMission, sessionContext: SessionContext): Boolean
}

package org.secapcompass.secapinventoryapi.domain.building.core.vo

import org.secapcompass.secapinventoryapi.domain.building.core.model.Report
import java.util.*

data class ReportBatch (
    var report: Report? = null,
    var count: Int = 0,
    var isFirstCalculation:Boolean = true,
    var timer: Timer = Timer(),
    var timerTask: TimerTask? = null
)
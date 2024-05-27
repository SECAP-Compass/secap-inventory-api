package org.secapcompass.secapinventoryapi.domain.building.core.vo

import org.secapcompass.secapinventoryapi.domain.building.core.model.Report
import java.util.*

data class ReportBatch (
    var report: Report? = null,
    var count: Int = 0,
    var timer: Timer = Timer(),
    var timerTask: TimerTask? = null
){
    fun cancelReportBatchTimer(){
        //timerTask?.cancel()
        timer.cancel()
        timer.purge()
    }

    fun setTimerTask(updateFunction: (ReportBatch) -> Unit){
        timerTask = object : TimerTask() {
            override fun run() {
                updateFunction(this@ReportBatch)
            }
        }
    }

    fun startReportBatchTimer(timeout:Long){
        if(timerTask == null){
            throw RuntimeException("timerTask is null, try calling setTimerTask method first")
        }
        timer = Timer()
        timer.schedule(timerTask,0, timeout)
    }
}
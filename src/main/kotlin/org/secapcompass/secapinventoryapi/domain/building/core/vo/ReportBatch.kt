package org.secapcompass.secapinventoryapi.domain.building.core.vo

import org.secapcompass.secapinventoryapi.domain.building.core.model.Report
import java.util.*

data class ReportBatch (
    var report: Report? = null,
    var count: Int = 0,
    var timer: Timer? = null,
    var timerTask: TimerTask? = null
){
    fun cancelReportBatchTimer(){
        //timerTask?.cancel()
        if(timer!=null){
            timer!!.cancel()
            timer!!.purge()
        }
    }

    fun startReportBatchTimer(timeout:Long,updateFunction: (ReportBatch) -> Unit){
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                updateFunction(this@ReportBatch)
            }
        }
        timer!!.schedule(timerTask,timeout)
    }
}
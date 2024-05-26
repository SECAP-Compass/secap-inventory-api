package org.secapcompass.secapinventoryapi.domain.building.core.vo

import org.secapcompass.secapinventoryapi.domain.building.core.model.Report
import java.util.*

data class ReportBatch(
    var report:Report,
    var count: Int = 0,
    var isFirstCalculation:Boolean = true,
    var timer: Timer,
    var timerTask: TimerTask
){
    fun initTimer(){
        timerTask.cancel()
        timer.cancel()
        timer.purge()

        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {

            }
        }
    }

    fun startTimer(timeout:Long){
        initTimer()
        timer.schedule(timerTask,0,timeout)
    }
}

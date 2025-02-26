package com.aimanissa.base.core.workers.data

import androidx.lifecycle.LifecycleOwner
import com.aimanissa.base.core.workers.SyncService

internal class SyncDataService(
    private val workers: List<BaseDataWorker<out Any>>?,
) : SyncService {

    override fun start() {
        workers?.forEach { it.start() }
    }

    override fun stop() {
        workers?.forEach { it.stop() }
    }

    override fun onResume(owner: LifecycleOwner) {
        start()
    }

    override fun onPause(owner: LifecycleOwner) {
        stop()
    }
}

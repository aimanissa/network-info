package com.aimanissa.base.core.workers

import androidx.lifecycle.LifecycleOwner

class SyncLifecycleService(
    private val workers: List<LifecycleSubscriptionWorker>,
) : SyncService {

    override fun start() {
        workers.forEach { it.start() }
    }

    override fun stop() {
        workers.forEach { it.stop() }
    }

    override fun onResume(owner: LifecycleOwner) {
        start()
    }

    override fun onPause(owner: LifecycleOwner) {
        stop()
    }
}
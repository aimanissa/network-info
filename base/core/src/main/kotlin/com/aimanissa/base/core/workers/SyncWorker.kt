package com.aimanissa.base.core.workers

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

interface SyncWorker {
    fun start()
    fun stop()
}

interface SyncService : SyncWorker, DefaultLifecycleObserver

internal class SyncServiceImpl(
    private val workers: List<BaseSyncWorker<out Any>>
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

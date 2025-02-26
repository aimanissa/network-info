package com.aimanissa.base.core.workers.data

import com.aimanissa.base.core.workers.SyncAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseDataWorker<T> : SyncAction {

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var data: T? = null
        private set

    protected abstract suspend fun sync()

    fun putData(data: T, withStartSync: Boolean = true) {
        this.data = data
        if (withStartSync) {
            startSync()
        }
    }

    fun removeData() {
        data = null
        stop()
    }

    private fun canSync(): Boolean = data != null

    override fun start() {
        startSync()
    }

    override fun stop() {
        job?.cancel()
    }

    private fun startSync() {
        if (job?.isActive == true) {
            stop()
        }
        job = scope.launch(Dispatchers.Default) {
            if (canSync()) {
                sync()
            }
        }
    }
}

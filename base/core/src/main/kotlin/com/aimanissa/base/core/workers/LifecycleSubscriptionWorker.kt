package com.aimanissa.base.core.workers

abstract class LifecycleSubscriptionWorker : SyncAction {

    protected abstract fun subscribe()
    protected abstract fun unsubscribe()

    override fun start() {
        subscribe()
    }

    override fun stop() {
        unsubscribe()
    }
}
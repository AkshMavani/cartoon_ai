package com.skylock.ai_cartoon.util


import com.skylock.ai_cartoon.base.BaseActivity
import java.util.*

object ActivityManager {
    private val activities = LinkedList<BaseActivity<*>>()

    fun addActivity(activity: BaseActivity<*>) {
        activities.add(activity)
    }

    fun removeActivity(activity: BaseActivity<*>) {
        activities.remove(activity)
    }

    fun getTopActivity(): BaseActivity<*>? {
        return if (activities.isNotEmpty()) activities.last else null
    }

    fun finishAll() {
        val iterator = activities.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            if (!activity.isFinishing) {
                activity.finish()
            }
            iterator.remove()
        }
    }

    fun isActivityActive(className: String): Boolean {
        return getTopActivity()?.javaClass?.name == className
    }

    fun isEmpty(): Boolean = activities.isEmpty()
}
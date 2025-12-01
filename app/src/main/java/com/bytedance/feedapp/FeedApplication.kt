package com.bytedance.feedapp

import android.app.Application
import com.bytedance.feedapp.data.MockRepo


/**
 * 自定义的 Application 类，作为应用的全局入口和初始化中心。
 *
 * 这个类在应用程序进程创建时被实例化，其生命周期与整个应用相同。
 * 它主要用于执行只需要进行一次的全局初始化操作，例如初始化单例等。
 *
 * @see Application
 */
class FeedApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 在这里初始化 MockRepo，传入 applicationContext 以避免内存泄漏
        MockRepo.init(this)
    }
}

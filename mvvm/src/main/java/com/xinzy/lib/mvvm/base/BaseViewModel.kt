package com.xinzy.lib.mvvm.base

import android.app.Application
import android.content.Context
import androidx.annotation.CallSuper
import androidx.lifecycle.*
import com.xinzy.lib.mvvm.event.SingleLiveEvent
import java.lang.Exception
import java.lang.reflect.ParameterizedType

open class BaseViewModel<M: BaseModel>(application: Application) : AndroidViewModel(application), LifecycleObserver {

    companion object {
        const val ACTION_FINISH = 0
        const val ACTION_SHOW_PROGRESS = 1
        const val ACTION_DISMIS_PROGRESS = 2
    }

    protected var mModel: M? = null

    val actionEvent = SingleLiveEvent<Int>()

    init {
        mModel = createModelInternal()
    }

    protected open fun createModel(): M? = null

    protected fun finish() = actionEvent.postValue(ACTION_FINISH)
    protected fun showProgress() = actionEvent.postValue(ACTION_SHOW_PROGRESS)
    protected fun dismissProgress() = actionEvent.postValue(ACTION_DISMIS_PROGRESS)

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        mModel?.onCleared()
    }

    private fun createModelInternal(): M? {
        val m = createModel()
        if (m != null) return m

        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val clazz = type.actualTypeArguments[0] as Class<M>
            return try {
                clazz.newInstance()
            } catch (e: Exception) {
                return try {
                    clazz.getConstructor(Context::class.java).newInstance(getApplication())
                } catch (e1: Exception) {
                    null
                }
            }
        }

        return null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    open fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {}

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreate() { }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStart() {  }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume() { }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPause() { }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStop() { }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy() { }
}
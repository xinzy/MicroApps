package com.xinzy.lib.mvvm.base

import androidx.databinding.BaseObservable

open class BaseModel : BaseObservable() {

    open fun onCleared() {}
}
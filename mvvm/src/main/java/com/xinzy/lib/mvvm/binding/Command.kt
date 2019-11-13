package com.xinzy.lib.mvvm.binding

interface BindingAction {
    fun call()
}

interface BindingConsumer<T> {
    fun call(t: T)
}

interface BindingFunction<T> {
    fun call(): T
}


package com.xinzy.lib.mvvm.annotation

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention
annotation class HttpConfig(val timeout: Long = 5L, val unsafe: Boolean = false)
package com.xinzy.lib.mvvm.annotation

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseUri(val value: String)
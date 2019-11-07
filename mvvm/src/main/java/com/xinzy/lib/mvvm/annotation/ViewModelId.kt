package com.xinzy.lib.mvvm.annotation

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention
annotation class ViewModelId(val value: Int = -1)
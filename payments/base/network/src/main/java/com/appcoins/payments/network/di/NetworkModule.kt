package com.appcoins.payments.network.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MicroServicesHostUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BackendHostUrl

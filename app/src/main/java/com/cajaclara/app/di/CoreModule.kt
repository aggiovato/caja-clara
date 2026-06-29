package com.cajaclara.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import java.time.ZoneId
import javax.inject.Singleton

/** Provides shared, app-wide collaborators. */
@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    /** System clock; injected into use cases so timestamps are testable. */
    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemDefaultZone()

    /** Device time zone; used to map sales to calendar days. */
    @Provides
    @Singleton
    fun provideZoneId(): ZoneId = ZoneId.systemDefault()
}

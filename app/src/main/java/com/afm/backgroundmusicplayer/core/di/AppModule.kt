package com.afm.backgroundmusicplayer.core.di

import android.content.Context
import com.afm.backgroundmusicplayer.core.data.preferences.PlayerPreferencesImpl
import com.afm.backgroundmusicplayer.core.data.repository.MusicRepositoryImpl
import com.afm.backgroundmusicplayer.core.domain.preferences.PlayerPreferences
import com.afm.backgroundmusicplayer.core.domain.repository.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMusicRepository(
        @ApplicationContext context: Context
    ): MusicRepository {
        return MusicRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun providePlayerPreferences(
        @ApplicationContext context: Context
    ): PlayerPreferences {
        return PlayerPreferencesImpl(context)
    }
}
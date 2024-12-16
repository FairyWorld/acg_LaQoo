package com.laqoome.laqoo.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.laqoome.laqoo.application.laqooApplication
import com.laqoome.laqoo.data.local.database.laqooDatabase
import com.laqoome.laqoo.data.repository.RoomRepositoryImpl
import com.laqoome.laqoo.domain.repository.RoomRepository
import com.laqoome.laqoo.util.laqoo_DATABASE
import com.laqoome.laqoo.util.DownloadManager
import com.laqoome.laqoo.util.preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideslaqooApplication(
        @ApplicationContext app: Context
    ): laqooApplication {
        return app as laqooApplication
    }

    @Singleton
    @Provides
    fun providesContext(
        @ApplicationContext app: Context
    ): Context {
        return app
    }

    @Singleton
    @Provides  // The Application binding is available without qualifiers.
    fun providesDatabase(application: Application): laqooDatabase {
        return Room.databaseBuilder(
            application,
            laqooDatabase::class.java,
            laqoo_DATABASE,
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesRoomRepository(database: laqooDatabase): RoomRepository {
        return RoomRepositoryImpl(database)
    }

    @Singleton
    @Provides
    fun providesPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.preferences
    }

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return DownloadManager.httpClient
    }
}
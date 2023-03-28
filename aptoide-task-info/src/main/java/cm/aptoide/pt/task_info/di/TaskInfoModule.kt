package cm.aptoide.pt.task_info.di

import android.content.Context
import androidx.room.Room
import cm.aptoide.pt.task_info.database.InstallationFileDao
import cm.aptoide.pt.task_info.database.TaskInfoDao
import cm.aptoide.pt.task_info.database.TaskInfoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskInfoModule {

  @Singleton
  @Provides
  fun provideTaskInfoDao(database: TaskInfoDatabase): TaskInfoDao = database.taskInfoDao()

  @Singleton
  @Provides
  fun provideInstallationFileDao(database: TaskInfoDatabase): InstallationFileDao =
    database.installationFileDao()

  @Singleton
  @Provides
  fun provideTaskInfoDatabase(@ApplicationContext appContext: Context): TaskInfoDatabase =
    Room.databaseBuilder(appContext, TaskInfoDatabase::class.java, "aptoide_task_info.db")
      .build()
}

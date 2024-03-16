package com.baris.voltagedropapp.di

import android.app.Application
import androidx.room.Room
import com.baris.voltagedropapp.feature_voltage_drop.data.data_source.ProjectDatabase
import com.baris.voltagedropapp.feature_voltage_drop.data.repository.ProjectRepositoryImpl
import com.baris.voltagedropapp.feature_voltage_drop.domain.repository.ProjectRepository
import com.baris.voltagedropapp.feature_voltage_drop.domain.use_case.AddProject
import com.baris.voltagedropapp.feature_voltage_drop.domain.use_case.DeleteProject
import com.baris.voltagedropapp.feature_voltage_drop.domain.use_case.GetProject
import com.baris.voltagedropapp.feature_voltage_drop.domain.use_case.GetProjects
import com.baris.voltagedropapp.feature_voltage_drop.domain.use_case.ProjectUseCases
import com.baris.voltagedropapp.feature_voltage_drop.domain.use_case.UpdateProject
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideProjectDatabase(app: Application): ProjectDatabase {
        return Room.databaseBuilder(
            app,
            ProjectDatabase::class.java,
            ProjectDatabase.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideProjectRepository(db: ProjectDatabase): ProjectRepository {
        return ProjectRepositoryImpl(db.projectDao())
    }

    @Singleton
    @Provides
    fun provideProjectUseCases(repository: ProjectRepository): ProjectUseCases {
        return ProjectUseCases(
            getProjects = GetProjects(repository),
            deleteProject = DeleteProject(repository),
            addProject = AddProject(repository),
            getProject = GetProject(repository),
            updateProject = UpdateProject(repository)
        )
    }
}
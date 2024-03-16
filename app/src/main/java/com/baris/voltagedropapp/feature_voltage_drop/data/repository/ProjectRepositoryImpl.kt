package com.baris.voltagedropapp.feature_voltage_drop.data.repository

import com.baris.voltagedropapp.feature_voltage_drop.data.data_source.ProjectDao
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Project
import com.baris.voltagedropapp.feature_voltage_drop.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProjectRepositoryImpl@Inject constructor(private val projectDao: ProjectDao):
    ProjectRepository {
    override fun getProjects(): Flow<List<Project>> {
        return projectDao.getProjects()
    }

    override suspend fun getProjectById(projectId: String): Project? {
        return projectDao.getProjectById(projectId)
    }

    override suspend fun insertProject(project: Project) {
        projectDao.insert(project)
    }

    override suspend fun updateProject(project: Project) {
        projectDao.update(project)
    }

    override suspend fun deleteProject(project: Project) {
        projectDao.deleteProject(project)
    }
}
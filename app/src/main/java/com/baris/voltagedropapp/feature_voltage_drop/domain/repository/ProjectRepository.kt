package com.baris.voltagedropapp.feature_voltage_drop.domain.repository

import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getProjects(): Flow<List<Project>>

    suspend fun getProjectById(projectId: String): Project?

    suspend fun insertProject(project: Project)

    suspend fun updateProject(project: Project)

    suspend fun deleteProject(project: Project)
}
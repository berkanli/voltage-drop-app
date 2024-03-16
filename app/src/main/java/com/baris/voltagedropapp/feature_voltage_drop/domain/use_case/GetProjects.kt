package com.baris.voltagedropapp.feature_voltage_drop.domain.use_case

import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Project
import com.baris.voltagedropapp.feature_voltage_drop.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow

class GetProjects(private val repository: ProjectRepository) {
    operator fun invoke(): Flow<List<Project>> {
        return repository.getProjects()
    }
}
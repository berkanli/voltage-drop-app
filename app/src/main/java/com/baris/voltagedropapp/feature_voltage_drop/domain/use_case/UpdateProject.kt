package com.baris.voltagedropapp.feature_voltage_drop.domain.use_case

import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Project
import com.baris.voltagedropapp.feature_voltage_drop.domain.repository.ProjectRepository

class UpdateProject(private val repository: ProjectRepository) {
    suspend operator fun invoke(project: Project){
        repository.updateProject(project)
    }
}
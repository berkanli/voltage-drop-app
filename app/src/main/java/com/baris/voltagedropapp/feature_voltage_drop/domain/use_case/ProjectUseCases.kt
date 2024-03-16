package com.baris.voltagedropapp.feature_voltage_drop.domain.use_case

data class ProjectUseCases(
    val getProjects: GetProjects,
    val deleteProject: DeleteProject,
    val addProject: AddProject,
    val getProject: GetProject,
    val updateProject: UpdateProject,
)
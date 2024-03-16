package com.baris.voltagedropapp.feature_voltage_drop.domain.use_case

import com.baris.voltagedropapp.feature_voltage_drop.domain.model.InvalidNoteException
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Project
import com.baris.voltagedropapp.feature_voltage_drop.domain.repository.ProjectRepository
import kotlin.jvm.Throws

class AddProject (
    private val repository: ProjectRepository
){
    @Throws(InvalidNoteException::class)
    suspend operator fun invoke(project: Project){
        if (project.title.isBlank()){
            throw InvalidNoteException("The title of the note can't be empty.")
        }
        repository.insertProject(project)
    }
}
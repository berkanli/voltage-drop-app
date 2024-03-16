package com.baris.voltagedropapp.feature_voltage_drop.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.baris.voltagedropapp.feature_voltage_drop.domain.model.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM project_table")
    fun getProjects(): Flow<List<Project>>

    @Query("SELECT * FROM project_table where id= :id")
    suspend fun getProjectById(id: String): Project?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: Project)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(project: Project)

    @Query("DELETE FROM project_table")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteProject(project: Project)
}
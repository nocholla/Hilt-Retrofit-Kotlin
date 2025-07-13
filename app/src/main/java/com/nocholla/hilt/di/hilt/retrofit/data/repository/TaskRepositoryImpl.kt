package com.nocholla.hilt.di.hilt.retrofit.data.repository

import com.nocholla.hilt.di.hilt.retrofit.data.api.TaskApiService
import com.nocholla.hilt.di.hilt.retrofit.data.model.TaskDto
import com.nocholla.hilt.di.hilt.retrofit.data.model.toDomain
import com.nocholla.hilt.di.hilt.retrofit.data.model.toDto
import com.nocholla.hilt.di.hilt.retrofit.domain.model.Task
import com.nocholla.hilt.di.hilt.retrofit.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskApiService: TaskApiService
) : TaskRepository {

    override suspend fun getTasks(): Flow<List<Task>> = flow {
        val taskDtos = taskApiService.getTasks()
        emit(taskDtos.map { it.toDomain() })
    }

    override suspend fun addTask(name: String): Task {
        val newTaskDto = taskApiService.addTask(TaskDto(id = 0, title = name, isCompleted = false))
        return newTaskDto.toDomain()
    }

    override suspend fun toggleTaskCompletion(task: Task): Task {
        val updatedTaskDto = taskApiService.updateTask(task.id, task.toDto())
        return updatedTaskDto.toDomain()
    }

    override suspend fun removeTask(taskId: Int) {
        taskApiService.deleteTask(taskId)
    }
}
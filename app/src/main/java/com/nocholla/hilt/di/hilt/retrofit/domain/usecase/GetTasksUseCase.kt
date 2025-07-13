package com.nocholla.hilt.di.hilt.retrofit.domain.usecase

import com.nocholla.hilt.di.hilt.retrofit.domain.model.Task
import com.nocholla.hilt.di.hilt.retrofit.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(): Result<Flow<List<Task>>> {
        return try {
            val tasksFlow = repository.getTasks().map { tasks ->
                if (tasks.isEmpty()) throw Exception("No tasks found")
                tasks
            }.catch { e ->
                throw e
            }
            Result.success(tasksFlow)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
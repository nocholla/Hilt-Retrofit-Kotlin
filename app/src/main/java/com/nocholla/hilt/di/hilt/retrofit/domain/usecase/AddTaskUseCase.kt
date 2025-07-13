package com.nocholla.hilt.di.hilt.retrofit.domain.usecase

import com.nocholla.hilt.di.hilt.retrofit.domain.model.Task
import com.nocholla.hilt.di.hilt.retrofit.domain.repository.TaskRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(name: String): Result<Task> {
        return try {
            val task = repository.addTask(name)
            Result.success(task)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
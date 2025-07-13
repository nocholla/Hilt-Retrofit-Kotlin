package com.nocholla.hilt.di.hilt.retrofit.domain.usecase

import com.nocholla.hilt.di.hilt.retrofit.domain.repository.TaskRepository
import javax.inject.Inject

class RemoveTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(taskId: Int): Result<Unit> {
        return try {
            repository.removeTask(taskId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
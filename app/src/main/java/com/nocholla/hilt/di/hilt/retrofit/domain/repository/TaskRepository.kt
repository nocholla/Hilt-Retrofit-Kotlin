package com.nocholla.hilt.di.hilt.retrofit.domain.repository

import com.nocholla.hilt.di.hilt.retrofit.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun getTasks(): Flow<List<Task>>
    suspend fun addTask(name: String): Task
    suspend fun toggleTaskCompletion(task: Task): Task
    suspend fun removeTask(taskId: Int)
}
package com.nocholla.hilt.di.hilt.retrofit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nocholla.hilt.di.hilt.retrofit.domain.model.Task
import com.nocholla.hilt.di.hilt.retrofit.domain.usecase.AddTaskUseCase
import com.nocholla.hilt.di.hilt.retrofit.domain.usecase.GetTasksUseCase
import com.nocholla.hilt.di.hilt.retrofit.domain.usecase.RemoveTaskUseCase
import com.nocholla.hilt.di.hilt.retrofit.domain.usecase.ToggleTaskCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase,
    private val removeTaskUseCase: RemoveTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState(isLoading = true))
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = getTasksUseCase()
            result.onSuccess { tasksFlow ->
                tasksFlow.collect { tasks ->
                    _uiState.update { it.copy(tasks = tasks, isLoading = false) }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(
                    error = e.localizedMessage ?: "Unknown error", isLoading = false
                ) }
            }
        }
    }

    fun addTask(name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = addTaskUseCase(name)
            result.onSuccess { newTask ->
                _uiState.update { currentState ->
                    currentState.copy(tasks = currentState.tasks + newTask, isLoading = false)
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(error = e.localizedMessage ?: "Error adding task", isLoading = false)
                }
            }
        }
    }

    fun toggleTaskCompletion(taskId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val taskToToggle = _uiState.value.tasks.find { it.id == taskId }
            if (taskToToggle != null) {
                val result = toggleTaskCompletionUseCase(taskToToggle)
                result.onSuccess { returnedTask ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            tasks = currentState.tasks.map { task ->
                                if (task.id == returnedTask.id) returnedTask else task
                            },
                            isLoading = false
                        )
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(
                            error = e.localizedMessage ?: "Error toggling task", isLoading = false
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(error = "Task not found for toggling", isLoading = false)
                }
            }
        }
    }

    fun removeTask(taskId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = removeTaskUseCase(taskId)
            result.onSuccess {
                _uiState.update { currentState ->
                    currentState.copy(
                        tasks = currentState.tasks.filter { task -> task.id != taskId },
                        isLoading = false
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(error = e.localizedMessage ?: "Error removing task", isLoading = false)
                }
            }
        }
    }
}
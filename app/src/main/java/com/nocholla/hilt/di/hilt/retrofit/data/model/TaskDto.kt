package com.nocholla.hilt.di.hilt.retrofit.data.model

import com.google.gson.annotations.SerializedName
import com.nocholla.hilt.di.hilt.retrofit.domain.model.Task

// This DTO maps directly to the JSON structure from the API
data class TaskDto(
    val id: Int,
    val title: String, // Often 'title' in APIs like JSONPlaceholder
    @SerializedName("completed") // Map API's 'completed' field to 'isCompleted'
    val isCompleted: Boolean
)

// Extension functions to convert between DTO and Domain Model
fun TaskDto.toDomain(): Task {
    return Task(
        id = this.id,
        name = this.title,
        isCompleted = this.isCompleted
    )
}

fun Task.toDto(): TaskDto {
    return TaskDto(
        id = this.id,
        title = this.name,
        isCompleted = this.isCompleted
    )
}

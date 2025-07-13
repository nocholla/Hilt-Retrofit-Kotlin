package com.nocholla.hilt.di.hilt.retrofit.data.model

import com.google.gson.annotations.SerializedName

// This DTO maps directly to the JSON structure from the API
data class TaskDto(
    val id: Int,
    val title: String, // Often 'title' in APIs like JSONPlaceholder
    @SerializedName("completed") // Map API's 'completed' field to 'isCompleted'
    val isCompleted: Boolean
)

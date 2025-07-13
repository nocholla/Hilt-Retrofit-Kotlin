package com.nocholla.hilt.di.hilt.retrofit.data.api

import com.nocholla.hilt.di.hilt.retrofit.data.model.TaskDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskApiService {
    @GET("todos")
    suspend fun getTasks(): List<TaskDto>

    @POST("todos")
    suspend fun addTask(@Body taskDto: TaskDto): TaskDto

    @PUT("todos/{id}")
    suspend fun updateTask(@Path("id") id: Int, @Body taskDto: TaskDto): TaskDto

    @DELETE("todos/{id}")
    suspend fun deleteTask(@Path("id") id: Int)
}
package com.nocholla.hilt.di.hilt.retrofit.di

import com.nocholla.hilt.di.hilt.retrofit.data.api.OkHttpClientFactory
import com.nocholla.hilt.di.hilt.retrofit.data.api.TaskApiService
import com.nocholla.hilt.di.hilt.retrofit.data.repository.TaskRepositoryImpl
import com.nocholla.hilt.di.hilt.retrofit.domain.repository.TaskRepository
import com.nocholla.hilt.di.hilt.retrofit.domain.usecase.*
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(okHttpClientFactory: OkHttpClientFactory): OkHttpClient {
        return okHttpClientFactory.createOkHttpClient()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideTaskApiService(retrofit: Retrofit): TaskApiService {
        return retrofit.create(TaskApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(taskApiService: TaskApiService): TaskRepository {
        return TaskRepositoryImpl(taskApiService)
    }

    @Provides
    fun provideAddTaskUseCase(repository: TaskRepository): AddTaskUseCase {
        return AddTaskUseCase(repository)
    }

    @Provides
    fun provideGetTasksUseCase(repository: TaskRepository): GetTasksUseCase {
        return GetTasksUseCase(repository)
    }

    @Provides
    fun provideToggleTaskCompletionUseCase(repository: TaskRepository): ToggleTaskCompletionUseCase {
        return ToggleTaskCompletionUseCase(repository)
    }

    @Provides
    fun provideRemoveTaskUseCase(repository: TaskRepository): RemoveTaskUseCase {
        return RemoveTaskUseCase(repository)
    }
}
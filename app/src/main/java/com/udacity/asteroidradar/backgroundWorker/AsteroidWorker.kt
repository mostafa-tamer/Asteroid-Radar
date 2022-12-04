package com.udacity.asteroidradar.backgroundWorker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.Repository

class AsteroidWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val dataSource: AsteroidDao = AsteroidDatabase
        .getInstance(context)
        .dataSource

    companion object {
        const val WORK_NAME = "Worker"
    }

    override suspend fun doWork(): Result {


        val repository = Repository(dataSource)

        return try {

            //refresh the database with new data
            repository.getApiData()
            Result.success()
        } catch (e: Exception) {

            println(e.message)
            Result.failure()
        }
    }
}
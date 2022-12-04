package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.ApiKey
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.database.AsteroidPropertyDatabase
import com.udacity.asteroidradar.database.toDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class Repository(val dataSource: AsteroidDao) {

    fun getDatabaseAsteroids(): LiveData<List<AsteroidPropertyDatabase>> {

        return dataSource.retrieveAsteroidProperties()
    }

    suspend fun getApiData(): List<Asteroid> {

        var list: List<Asteroid> = mutableListOf()

        withContext(Dispatchers.IO) {

            try {

                val apiString = AsteroidApi.retrofitService
                    .getProperties(ApiKey.KEY.key)

                list = parseAsteroidsJsonResult(JSONObject(apiString))

                dataSource.insertAsteroidProperties(*list.toDatabase())

            } catch (e: Exception) {
                println("Error Message " + e.message)
            }
        }

        return list
    }

    suspend fun setupPictureOfTheDay(): PictureOfDay? {

        var apiPictureOfDay = PictureOfDay(
            "Image",
            "Waiting For The Image Of The Day",
            "https://apod.nasa.gov/apod/image/2001/STSCI-H-p2006a-h-1024x614.jpg"
        )

        withContext(Dispatchers.IO) {

            try {
                apiPictureOfDay = AsteroidApi.retrofitService.getImage(ApiKey.KEY.key)

            } catch (e: Exception) {
                println("Error on finding the image of the day")
            }
        }

        if (apiPictureOfDay.mediaType == "video") {
            return null
        }

        return apiPictureOfDay
    }
}
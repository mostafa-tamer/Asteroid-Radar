package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid


@Entity
data class AsteroidPropertyDatabase(
    @PrimaryKey
    val id: Long, val codename: String, val closeApproachDate: String,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

fun List<AsteroidPropertyDatabase>.toDomain(): List<Asteroid> {
    return map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

fun List<Asteroid>.toDatabase(): Array<AsteroidPropertyDatabase> {
    return map {
        AsteroidPropertyDatabase(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}


@Dao
interface AsteroidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsteroidProperties(vararg asteroidProperties: AsteroidPropertyDatabase)

    @Query("select * from asteroidpropertydatabase order by closeApproachDate asc")
    fun retrieveAsteroidProperties(): LiveData<List<AsteroidPropertyDatabase>>
}

@Database(entities = [AsteroidPropertyDatabase::class], version = 1, exportSchema = false)
abstract class AsteroidDatabase : RoomDatabase() {

    abstract val dataSource: AsteroidDao

    companion object {

        @Volatile
        private var instance: AsteroidDatabase? = null

        fun getInstance(context: Context): AsteroidDatabase {

            synchronized(this) {
                if (instance == null) {

                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AsteroidDatabase::class.java,
                        "asteroid_database"
                    ).build()
                }
            }

            return instance!!
        }
    }
}



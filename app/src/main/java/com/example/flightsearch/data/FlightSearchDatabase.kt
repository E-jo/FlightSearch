package com.example.flightsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flightsearch.models.Airport
import com.example.flightsearch.models.Flight

@Database(
    entities = [Airport::class, Flight::class],
    version = 1, exportSchema = false
)
abstract class FlightSearchDatabase : RoomDatabase() {
    abstract fun airportDao(): AirportDao
    abstract fun flightDao(): FlightDao

    companion object {
        @Volatile
        private var Instance: FlightSearchDatabase? = null

        fun getDatabase(context: Context): FlightSearchDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    FlightSearchDatabase::class.java,
                    "flight_search"
                )
                    .createFromAsset("database/flight_search.db")
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}



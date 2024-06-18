package com.example.flightsearch.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "airport")
data class Airport(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "iata_code")
    val iataCode: String,
    val name: String,
    val passengers: Int
) {
}

/*
Column

Data type

Description

id

INTEGER

Unique identifier (primary key)

iata_code

VARCHAR

3 letter IATA code

name

VARCHAR

Full airport name

passengers

INTEGER

Number of passengers per year
 */
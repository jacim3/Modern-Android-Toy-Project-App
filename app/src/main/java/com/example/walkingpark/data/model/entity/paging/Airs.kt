package com.example.walkingpark.data.model.entity.paging

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Airs(
    val total: Int = 0,
    val page:Int = 0,
    val Airs: List<Air>
) : Parcelable {

    @IgnoredOnParcel
    val endOfPage = total  == page

    @Parcelize
    @Entity(tableName = "weather")
    data class Air(
        @PrimaryKey(autoGenerate = true) val id:Long = 0,
        val baseDate: String,
        val baseTime:String,
        val category:String,
        val showDate:String,
        val showTime:String,
        val nx:String,
        val ny:String
    ) : Parcelable

    @Parcelize
    @Entity(tableName = "movie_remote_keys")
    data class WeatherRemoteKeys(
        @PrimaryKey val weatherId:Long,
        val prevKey:Int?,
        val nextKey:Int?
    ): Parcelable
}
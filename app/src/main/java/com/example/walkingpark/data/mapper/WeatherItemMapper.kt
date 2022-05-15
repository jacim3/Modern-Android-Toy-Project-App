package com.example.walkingpark.data.mapper

import com.example.walkingpark.constants.WEATHER
import com.example.walkingpark.data.model.dto.WeatherDTO
import java.util.*

object WeatherItemMapper {
/*

        <items>
            <item>
                <baseDate>20220418</baseDate>
                <baseTime>1100</baseTime>
                <category>TMP</category>
                <fcstDate>20220418</fcstDate>
                <fcstTime>1200</fcstTime>
                <fcstValue>17</fcstValue>
                <nx>55</nx>
                <ny>127</ny>
            </item>
            <item>
                <baseDate>20220418</baseDate>
                <baseTime>1100</baseTime>
                <category>UUU</category>
                <fcstDate>20220418</fcstDate>
                <fcstTime>1200</fcstTime>
                <fcstValue>1.6</fcstValue>
                <nx>55</nx>
                <ny>127</ny>
            </item>
        </item>
*/

    // TODO 현재 시각을 기준으로 1차 필터링 이후, 해당 Mapper 를 호출해야 함.
    fun responseToMapper(response: List<WeatherDTO.Response.Body.Items.Item>): HashMap<String, String?> {
        val result = HashMap<String, String?>().apply {
            for (i in WEATHER.values()) {
                this[i.code] = "none"
            }
        }
        response.forEach {
            for (i in WEATHER.values()) {
                if (it.category == i.code) {
                    result[it.category] = it.fcstValue
                }
            }
        }
        return result
    }
}
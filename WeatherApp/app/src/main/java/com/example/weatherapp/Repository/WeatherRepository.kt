package com.example.weatherapp.Repository

import com.example.weatherapp.Server.ApiServices

class WeatherRepository(val api:ApiServices) {

    fun getCurrentWeather(lat : Double,lng : Double, unit:String)=
        api.getCurrentWeather(lat,lng,unit,"c98072f4b726b0d61aa540238dc8c89a")

    fun getForecastWeather(lat : Double,lng : Double, unit:String)=
        api.getForecastWeather(lat,lng,unit,"c98072f4b726b0d61aa540238dc8c89a")
}
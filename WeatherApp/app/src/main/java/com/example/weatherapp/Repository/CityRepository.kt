package com.example.weatherapp.Repository

import com.example.weatherapp.Server.ApiServices

class CityRepository(val api:ApiServices) {
    fun getCities(q:String,limit:Int)=
        api.getCitiesList(q,limit,"c98072f4b726b0d61aa540238dc8c89a")
}
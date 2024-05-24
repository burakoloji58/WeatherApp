package com.example.weatherapp.ViewModel

import androidx.lifecycle.ViewModel
import com.example.weatherapp.Repository.CityRepository
import com.example.weatherapp.Server.ApiClient
import com.example.weatherapp.Server.ApiServices

class CitiyViewModel(val repository: CityRepository) : ViewModel() {

    constructor() : this(CityRepository(ApiClient().getClient().create(ApiServices::class.java)))

    fun loadCity(q:String,limit:Int) =
        repository.getCities(q,limit)
}
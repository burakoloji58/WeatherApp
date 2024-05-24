package com.example.weatherapp.Activity
                                                                                        // Burak Karagöz
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Adapter.ForecastAdapter
import com.example.weatherapp.Model.CurrentResponseApi
import com.example.weatherapp.Model.ForecastResponseApi
import com.example.weatherapp.R
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.example.weatherapp.databinding.ActivityMainBinding
import com.github.matteobattilana.weather.PrecipType
import eightbitlab.com.blurview.RenderScriptBlur
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.util.Calendar
import javax.security.auth.callback.Callback

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val calendar by lazy { Calendar.getInstance() }
    private val forecastAdapter by lazy { ForecastAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        binding.apply {

            var lat=intent.getDoubleExtra("lat",0.0)
            var lon=intent.getDoubleExtra("lon",0.0)
            var name=intent.getStringExtra("name")

            if(lat == 0.0){
                 lat = 37.1798
                //51.50
                 lon = 33.2246
                //-0.12
                 name = "Karaman"
            }

            addCity.setOnClickListener {
                startActivity(Intent(this@MainActivity,CityListActivity::class.java))
            }

            //current Temp

            cityTxt.text=name
            progressBar.visibility=View.VISIBLE

            weatherViewModel.loadCurrentWeather(lat,lon,"metric").enqueue(object :
                retrofit2.Callback<CurrentResponseApi> {
                override fun onResponse(
                    call: Call<CurrentResponseApi>,
                    response: Response<CurrentResponseApi>
                ) {

                    if (response.isSuccessful){
                        val data=response.body()
                        progressBar.visibility=View.GONE
                        detaillayout.visibility=View.VISIBLE
                        //
                        data?.let {
                            statusTxt.text=it.weather?.get(0)?.main ?: "."
                            windTxt.text=it.wind?.speed?.let { Math.round(it).toString() }+ "Km"
                            humidityTxt.text=it.main?.humidity?.toString()+"%"
                            currentTempTxt.text=it.main?.temp?.let { Math.round(it).toString() }+ "°"
                            maxTempTxt.text=it.main?.tempMax?.let { Math.round(it).toString() }+ "°"
                            minTempTxt.text=it.main?.tempMin?.let { Math.round(it).toString() }+ "°"


                            val drawble=if (isNightNow()) R.drawable.night_bg
                            else{
                                setDynamicallyWallpaper(it.weather?.get(0)?.icon?:"-")
                            }
                            bgImage.setImageResource(drawble)
                            setEffectRainSnow(it.weather?.get(0)?.icon?:"-")
                        }
                    }else{
                        Log.e("BURDAAAAA","başarısız")
                        Log.e("BURDAAAAA",response.toString())
                    }

                }

                override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                    Log.e("Retrofit Error", t.message.toString())
                    Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
                }

            })


            //bluer view settings

            var radius = 10f
            val decorView = window.decorView
            val rootView = (decorView.findViewById(android.R.id.content) as ViewGroup?)
            val windowBackground = decorView.background

            rootView?.let {
                blurView.setupWith(it,RenderScriptBlur(this@MainActivity))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
                blurView.clipToOutline = true
            }

            //forecast Temp

            weatherViewModel.loadForecastWeather(lat,lon,"metric").enqueue(object : retrofit2.Callback<ForecastResponseApi>{
                override fun onResponse(
                    call: Call<ForecastResponseApi>,
                    response: Response<ForecastResponseApi>
                ) {
                    if (response.isSuccessful){
                        val data = response.body()
                        blurView.visibility= View.VISIBLE

                        data?.let {

                            forecastAdapter.differ.submitList(it.list)

                            forecastView.apply {
                                layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
                                adapter=forecastAdapter
                            }
                        }

                    }
                }

                override fun onFailure(call: Call<ForecastResponseApi>, t: Throwable) {
                    Log.e("Retrofit Error", t.message.toString())
                    Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
                }

            })

        }

    }
    private fun isNightNow():Boolean{
        Log.e("BURDAAAAA", (calendar.get(Calendar.HOUR_OF_DAY)+3).toString())
        return calendar.get(Calendar.HOUR_OF_DAY)+3 >= 19
    }

    private fun setDynamicallyWallpaper(icon:String) : Int{
        return when(icon.dropLast(1)){
            "01"->{
                    initWeatherView(PrecipType.CLEAR)
                R.drawable.snow_bg
            }"02","03","04"->{
                    initWeatherView(PrecipType.CLEAR)
                R.drawable.cloudy_bg
            }"09","10","11"->{
                    initWeatherView(PrecipType.RAIN)
                R.drawable.rainy_bg
            }"13"->{
                    initWeatherView(PrecipType.SNOW)
                R.drawable.snow_bg
            }"50"->{
                    initWeatherView(PrecipType.CLEAR)
                R.drawable.haze_bg
            }
            else->0
        }
    }
    private fun initWeatherView(type: PrecipType){
        binding.weatherView.apply {
            setWeatherData(type)
            angle=20
            emissionRate=100.0f
        }
    }
    private fun setEffectRainSnow(icon:String) {
        when(icon.dropLast(1)){
            "01"->{
                initWeatherView(PrecipType.CLEAR)

            }"02","03","04"->{
                initWeatherView(PrecipType.CLEAR)

            }"09","10","11"->{
                initWeatherView(PrecipType.RAIN)

            }"13"->{
                initWeatherView(PrecipType.SNOW)

            }"50"->{
                initWeatherView(PrecipType.CLEAR)

            }
        }
    }

}
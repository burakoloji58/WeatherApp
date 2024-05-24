package com.example.weatherapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.Model.ForecastResponseApi
import com.example.weatherapp.databinding.ForecastViewholderBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ViewHolder>(){
    private lateinit var binding: ForecastViewholderBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ForecastViewholderBinding.inflate(inflater,parent,false)
        return ViewHolder()
    }

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ForecastAdapter.ViewHolder, position: Int) {
        val binding = ForecastViewholderBinding.bind(holder.itemView)
        val date=SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(differ.currentList[position].dtTxt.toString())
        val calendar = Calendar.getInstance()
        calendar.time=date

        val dayOfWeekName =when(calendar.get(Calendar.DAY_OF_WEEK)){
            1->"Sun"
            2->"Mon"
            3->"Tue"
            4->"Wed"
            5->"Thu"
            6->"Fri"
            7->"Sat"
            else->"-"
        }
        binding.nameDayTxt.text=dayOfWeekName
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val amPm = if (hour < 12) "am" else "pm"
        val hour12 = calendar.get(Calendar.HOUR)
        binding.hourTxt.text=hour12.toString()+amPm
        binding.tempTxt.text=differ.currentList[position].main?.temp?.let { Math.round(it) }.toString()+"°"


        val icon = when(differ.currentList[position].weather?.get(0)?.icon.toString()){
            "0d", "0"-> "sunny"
            "1d", "1n"-> "cloudy_sunny"
            "2d", "2n"-> "cloudy_sunny"
            "3d", "3n"-> "cloudy"
            "9d", "9n"-> "rainy"
            "10d", "10n"-> "rainy"
            "11d", "11n"-> "storm"
            "13d", "13n"-> "snow"
            "50d", "50n"-> "windy"
            else-> "sunny"
        }

        val drawableResourceId : Int =binding.root.resources.getIdentifier(icon,"drawable",binding.root.context.packageName)

        Glide.with(binding.root.context)
            .load(drawableResourceId)
            .into(binding.pic)

    }

    override fun getItemCount()=differ.currentList.size

    private val differCallBack = object :DiffUtil.ItemCallback<ForecastResponseApi.data>(){
        override fun areItemsTheSame(
            oldItem: ForecastResponseApi.data,
            newItem: ForecastResponseApi.data
        ): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(
            oldItem: ForecastResponseApi.data,
            newItem: ForecastResponseApi.data
        ): Boolean {
            return  oldItem==newItem
        }

    }
    val differ = AsyncListDiffer(this,differCallBack)


}
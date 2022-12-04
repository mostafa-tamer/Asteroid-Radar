package com.udacity.asteroidradar.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.repository.Repository
import kotlinx.coroutines.launch

class MainViewModel(
    val repository: Repository
) : ViewModel() {


    private val _status = MutableLiveData<String>()
    val status get() = _status.value

    private val _data = MutableLiveData<List<Asteroid>>()
    val data get() = _data

    private var _imageUrl =
        MutableLiveData("https://apod.nasa.gov/apod/image/2001/STSCI-H-p2006a-h-1024x614.jpg")
    val imageUrl get() = _imageUrl

    private var _imageTitle =
        MutableLiveData("Waiting For The Image Of The Day")
    val imageTitle get() = _imageTitle

    init {
        apiData()
        setupPictureOfTheDay()
    }

    fun getDatabaseAsteroids() = repository.getDatabaseAsteroids()

    private fun setupPictureOfTheDay() {

        viewModelScope.launch {

            val imageOfTheDay = repository.setupPictureOfTheDay()

            if (imageOfTheDay != null) {
                _imageUrl.value = imageOfTheDay.url
                _imageTitle.value = imageOfTheDay.title
            }
        }
    }

    private fun apiData() {

        viewModelScope.launch {
            _data.value = repository.getApiData()

            if (data.value!!.isEmpty()) {
                _status.value = "Failed"
            } else {
                _status.value = "Success"
            }

            println(status)
        }
    }

    class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Can not create viewModel")
        }
    }

}

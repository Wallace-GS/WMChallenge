package com.ngmatt.weedmapsandroidcodechallenge.ui.businesses

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngmatt.weedmapsandroidcodechallenge.BuildConfig
import com.ngmatt.weedmapsandroidcodechallenge.data.Business
import com.ngmatt.weedmapsandroidcodechallenge.data.YelpApi
import kotlinx.coroutines.*
import java.lang.Exception

class BusinessesViewModel : ViewModel() {

    private val _businesses = MutableLiveData<List<Business>>()
    val businesses: LiveData<List<Business>>
        get() = _businesses

    private var _searchTerm = MutableLiveData("food")
    val searchTerm: LiveData<String>
        get() = _searchTerm

    private var searchJob: Job? = null

    private var _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private var _resultVisible = MutableLiveData(View.GONE)
    val resultVisible: LiveData<Int>
        get() = _resultVisible

    private var _listVisible = MutableLiveData(View.GONE)
    val listVisible: LiveData<Int>
        get() = _listVisible

    private var _progressVisible = MutableLiveData(View.GONE)
    val progressVisible: LiveData<Int>
        get() = _progressVisible

    private var _noResultVisible = MutableLiveData(View.GONE)
    val noResultVisible: LiveData<Int>
        get() = _noResultVisible

    private var _errorVisible = MutableLiveData(View.GONE)
    val errorVisible: LiveData<Int>
        get() = _errorVisible

    init {
        searchTerm.value?.let { getBusinesses(it) }
    }

    fun getBusinesses(searchTerm: String) {
        searchJob?.cancel()
        setProgressBar()
        this._searchTerm.value = searchTerm
        searchJob = viewModelScope.launch {
            delay(500)
            try {
                val listResult = YelpApi.retrofitService
                    .searchBusinesses("Bearer ${BuildConfig.API_KEY}", searchTerm, "irvine")
                    .body()
                    ?.businesses
                if (listResult != null && listResult.isNotEmpty()) {
                    val job = ArrayList<Job>()
                    if (listResult.size >= 3) {
                        for (i in 0..2) {
                            job.add(launch {
                                getSortedReviews(listResult, i)
                            })
                        }
                        job.joinAll()

                        _businesses.value = listResult.subList(0, 3)
                        onSearchSuccessful()
                        for (i in 3 until listResult.size) {
                            delay(200)
                            getSortedReviews(listResult, i)
                            if (listResult[i].reviews.isEmpty()) continue
                            _businesses.value = listResult.subList(0, i + 1)
                        }
                    } else {
                        for (i in listResult.indices) {
                            job.add(launch {
                                getSortedReviews(listResult, i)
                            })
                        }
                        job.joinAll()

                        _businesses.value = listResult
                        onSearchSuccessful()
                    }
                } else if (listResult?.isEmpty() == true) {
                    onSearchUnsuccessful()
                }
            } catch (e: Exception) {
                setNetworkError(e.localizedMessage)
            }
        }
    }

    private suspend fun getSortedReviews(listResult: List<Business>, i: Int) {
        val reviewsResponse = YelpApi.retrofitService.getReviews(
            "Bearer ${BuildConfig.API_KEY}",
            listResult[i].id
        )
        reviewsResponse.body()?.reviews?.let {
            val sortedList = it.sortedByDescending { review ->
                review.rating
            }
            listResult[i].reviews.addAll(sortedList)
        }
    }

    private fun setNetworkError(error: String?) {
        _resultVisible.value = View.GONE
        _listVisible.value = View.GONE
        _progressVisible.value = View.GONE
        _errorMessage.value = error
        _errorVisible.value = View.VISIBLE
    }

    private fun setProgressBar() {
        _errorVisible.value = View.GONE
        _errorMessage.value = ""
        _noResultVisible.value = View.GONE
        _resultVisible.value = View.GONE
        _progressVisible.value = View.VISIBLE
        _businesses.value = mutableListOf()
    }

    private fun onSearchSuccessful() {
        _progressVisible.value = View.GONE
        _resultVisible.value = View.VISIBLE
        _listVisible.value = View.VISIBLE
    }

    private fun onSearchUnsuccessful() {
        _listVisible.value = View.GONE
        _progressVisible.value = View.GONE
        _noResultVisible.value = View.VISIBLE
    }

}
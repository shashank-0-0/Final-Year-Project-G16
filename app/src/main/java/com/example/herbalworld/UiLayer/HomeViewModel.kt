package com.example.herbalworld.UiLayer

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.herbalworld.Model.network.TrefleApi
import com.example.herbalworld.Model.network.model.Herb
import com.example.herbalworld.Model.network.model.Herbs
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

const val PAGE_SIZE = 20

class HomeViewModel : ViewModel() {

    val herbs: MutableLiveData<List<Herb>> = MutableLiveData(mutableListOf())
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val page = mutableStateOf(1)


    init {
        viewModelScope.launch() {
            herbs.value = getHerbs()
        }
    }

    private suspend fun getHerbs(): List<Herb> {
        return try {
            api.getHerbs(true, api_key, page.value).data
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun nextPage(){
        viewModelScope.launch {
            //prevent duplicate events due to recomposing
            if(loading.value==false){
                Log.i("shetty","yo ${loading}")
                loading.value=true
                appendHerbs(getHerbs())
            }
        }
    }
    /**
     * Append new herbs to currrent list of herbs
     **/
    private fun appendHerbs(newHerbs:List<Herb>){
        Log.i("shetty","yo ${newHerbs.size}")
        val currentHerbs = ArrayList(herbs.value!!)
        currentHerbs.addAll(newHerbs)
        herbs.value= currentHerbs
        loading.value=false
        incrementPage()
    }


    private fun incrementPage() {
        page.value = page.value + 1
    }



    companion object {
        val api = Retrofit.Builder().baseUrl("https://trefle.io/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(TrefleApi::class.java)
        val api_key = "dvnUIeT0VpY4zhhNvIZNdizh3VLlCh9Ckh6HbwTEdW4"
    }
}

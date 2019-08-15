// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.data

import android.app.Application
import android.database.ContentObserver
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Wilberforce on 19/04/2019 at 16:45.
 */
abstract class BaseViewModel<T>(application: Application) : AndroidViewModel(application) {

    val data = MutableLiveData<List<T>>()
    abstract var repository: BaseRepository<T>
    open var projection: Array<String>? = null
    open var selection: String? = null
    open var selectionArgs: Array<String>? = null
    open var sortOrder: String? = null
    abstract var uri: Uri


    private val observer: ContentObserver = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {
            loadData()
        }
    }

    fun init() {
        repository.uri = uri
        observer.onChange(false)
        getApplication<Application>().contentResolver.registerContentObserver(uri, true, observer)
    }


    fun loadData() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.loadData(projection, selection, selectionArgs, sortOrder)
            }
            deliverResult(result)

        }
    }

    override fun onCleared() {
        super.onCleared()
        getApplication<Application>().contentResolver.unregisterContentObserver(observer)
    }


    // Give child classes the opportunity to intercept and modify result
    open fun deliverResult(items: List<T>) {
        if (data.value != items) data.value = items
    }
}


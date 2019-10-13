// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.view

import android.app.Application
import android.database.ContentObserver
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.CallSuper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jadebyte.jadeplayer.main.common.data.MediaStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *  Created by Wilberforce on 19/04/2019 at 16:45.
 *  Base class for all ViewHolders that relies on [MediaStore] for data
 *
 *  All Activities or Fragments using this ViewModel must call the [init] function to initialize
 *  fetching of data from [MediaStore] and watching it for subsequent changes.
 *
 */
abstract class BaseMediaStoreViewModel<T>(application: Application) : AndroidViewModel(application) {

    protected val data = MutableLiveData<List<T>>()
    val items: LiveData<List<T>> get() = data
    abstract var repository: MediaStoreRepository<T>
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

    /**
     *  Fetch data from the [MediaStore] and watch it for changes to the data at [uri]]
     */
    @CallSuper
    open fun init(vararg params: Any?) {
        observer.onChange(false)
        getApplication<Application>().contentResolver.registerContentObserver(uri, true, observer)
    }


    private fun loadData() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.loadData(uri, projection, selection, selectionArgs, sortOrder)
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

    fun overrideCurrentItems(items: List<T>) {
        data.value = items
    }
}


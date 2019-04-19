// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.data

import android.content.Context
import androidx.lifecycle.ComputableLiveData
import androidx.lifecycle.LiveData

/**
 * Created by Wilberforce on 17/04/2019 at 04:12.
 */
abstract class ProviderLiveData<T> (private val context: Context): LiveData<T>() {
}
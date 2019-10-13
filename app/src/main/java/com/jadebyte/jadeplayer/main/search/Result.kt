// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.search

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.android.parcel.Parcelize


/**
 * Created by Wilberforce on 2019-10-13 at 03:58.
 */
@Parcelize
data class Result(@StringRes val title: Int, val type: Type, var hasResults: Boolean = false) : Parcelable

enum class Type {
    Songs, Albums, Artists, Genres, Playlists
}
// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.genres

import android.os.Parcelable
import com.jadebyte.jadeplayer.main.common.data.Data
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Genre(override val id: Long) : Data(), Parcelable {
}
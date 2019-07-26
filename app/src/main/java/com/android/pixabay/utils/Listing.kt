package com.android.pixabay.utils

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

data class Listing<T>(val pagedList: LiveData<PagedList<T>>,
                      val networkState: LiveData<NetworkState>,
                      val retry: () -> Unit)
package com.nochino.support.networking.vo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel which can be subclassed to return [LiveData] of type [LoadingResource] for a specified
 * data object [D]
 */
abstract class LoadingResourceViewModel<D>: ViewModel() {
    abstract fun fetchLiveData(ignoreCache: Boolean = false): LiveData<LoadingResource<D>>
}
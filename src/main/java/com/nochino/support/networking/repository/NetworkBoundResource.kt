package com.nochino.support.networking.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.nochino.support.networking.api.ApiEmptyResponse
import com.nochino.support.networking.api.ApiErrorResponse
import com.nochino.support.networking.api.ApiResponse
import com.nochino.support.networking.api.ApiSuccessResponse
import com.nochino.support.networking.execution.AppExecutors
import com.nochino.support.networking.vo.LoadingResource

/**
 * A generic class that can provide a resource backed by both the network
 * and a cache (eg...local data cache or an sqLite database).
 *
 * Implementations should provide a reference to the [LiveData] [ResultType] object
 * being requested, and return this reference in implementations of [loadFromStorage].
 *
 * Borrowed from Google Architecture sample project
 * [android-architecture-components](https://github.com/googlesamples/android-architecture-components/blob/88747993139224a4bb6dbe985adf652d557de621/GithubBrowserSample/app/src/main/java/com/android/example/github/repository/NetworkBoundResource.kt)
 *
 * You can read more about it in the
 * [Architecture Guide](https://developer.android.com/arch).
 */
abstract class NetworkBoundResource<ResultType, RequestType>
@MainThread constructor(private val appExecutors: AppExecutors) {

    /**
     * LiveData that notifies observers of [LoadingResource] for the [ResultType].
     * This special LiveData object mediates on changes to both the storage live data
     * and the network live data. When changes are made to either, listeners of the
     * [LoadingResource] of the [ResultType] are notified.
     */
    private val result = MediatorLiveData<LoadingResource<ResultType>>()

    init {
        result.value = LoadingResource.loading(null)
        @Suppress("LeakingThis")
        val storageDataSource = loadFromStorage()
        result.addSource(storageDataSource) { data ->
            result.removeSource(storageDataSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(storageDataSource)
            } else {
                result.addSource(storageDataSource) { newData ->
                    setValue(LoadingResource.success(newData))
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: LoadingResource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(storageDataSource: LiveData<ResultType>) {

        // Re-attach dbSource as a mediator LiveData source, it will dispatch its latest value quickly
        result.addSource(storageDataSource) { newData ->
            setValue(LoadingResource.loading(newData))
        }

        // Create the LiveData ApiResponse for the RequestType. When this is added
        // to the MediatorLiveData result object the ApiResponse call is created
        // and executed(enqueued) by Retrofit
        val apiResponse = createCall()

        // Attach the ApiResponse as a mediator LiveData source.
        // Attaching this LiveData object to the mediator kicks-off the API
        // request call via MutableLiveDataCallAdapter by making the LiveData
        // object "active".
        // See "onActive" method override in MutableLiveDataCallAdapter.adapt() override.
        result.addSource(apiResponse) { response ->

            // Allow implementations to perform work with the
            // api response before observers of the mediator LiveData
            // are alerted.
            appExecutors.diskIO().execute {
                preProcessRawResponse(apiResponse)
            }

            // Remove the storageDataSource from the mediator LiveData
            // This is done so further logic can re-query the storageDataSource
            // for a fresh copy before setting the new value on the
            // mediator LiveData to alert observers.
            result.removeSource(storageDataSource)

            when (response) {

                is ApiSuccessResponse -> {

                    // Only when the API response is successful should the
                    // apiResponse be removed as a mediator source!
                    // This way, if the response returns as failed, implementations
                    // can chain on future calls to the API, and if the future call is
                    // successful, alert previous observers of failed implementations with
                    // the successful result response!
                    result.removeSource(apiResponse)

                    appExecutors.diskIO().execute {

                        val apiSuccessResponse = processSuccessResponse(response)
                        saveCallResult(apiSuccessResponse)

                        appExecutors.mainThread().execute {
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromStorage()) { newData ->
                                setValue(LoadingResource.success(newData))
                            }
                        }
                    }
                }

                is ApiEmptyResponse -> {
                    appExecutors.mainThread().execute {
                        // reload from disk whatever we had
                        result.addSource(loadFromStorage()) { newData ->
                            setValue(LoadingResource.success(newData))
                        }
                    }
                }

                is ApiErrorResponse -> {
                    onFetchFailed(apiResponse)
                    result.addSource(storageDataSource) { newData ->
                        setValue(LoadingResource.error(response.errorMessage, newData))
                    }
                }
            }
        }
    }

    /**
     * Invoked after an [ApiResponse] has returned and failed.
     */
    protected open fun onFetchFailed(apiResponse: MutableLiveData<ApiResponse<RequestType>>) {
        /* Currently no default op*/
    }

    /**
     * Returns the internal [MediatorLiveData] [result] as a [LiveData] object. Creates abstraction and allows
     * implementations the ability to not worry about requiring the use of [MediatorLiveData]
     */
    fun asLiveData() = result as LiveData<LoadingResource<ResultType>>

    /**
     * Called on the DiskIO thread immediately after the api response is returned.
     * Allows implementing classes the ability to perform some action with the
     * response before the response is processed by the LiveData observer in
     * [fetchFromNetwork]
     */
    @WorkerThread
    protected abstract fun preProcessRawResponse(liveDataRequestResponse: MutableLiveData<ApiResponse<RequestType>>)

    /**
     * Called on the DiskIO thread after an [ApiResponse] has returned as an [ApiSuccessResponse]
     */
    @WorkerThread
    protected open fun processSuccessResponse(apiSuccessResponse: ApiSuccessResponse<RequestType>): RequestType {
        return apiSuccessResponse.body
    }

    /**
     * Allows implementations to save the result of the [RequestType]. This is currently only
     * called for successful responses.
     */
    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    /**
     * Allows implementations to define rules that determine if the [ResultType] should be fetched
     * from the network (api). If implementations return false then the [ResultType] will attempt to
     * be derived from the the implementations storage (or cached) value via [loadFromStorage].
     * See initialization block for more details.
     */
    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    /**
     * Retrieves any current value of the [ResultType] from implementations. This could be
     * a value from a database or a cached value in the implementation.
     */
    @MainThread
    protected abstract fun loadFromStorage(): LiveData<ResultType>

    /**
     * Retrieves the API call (which provides the [ApiResponse] object) of the desired
     * [RequestType] from the implementation
     */
    @MainThread
    protected abstract fun createCall(): MutableLiveData<ApiResponse<RequestType>>
}
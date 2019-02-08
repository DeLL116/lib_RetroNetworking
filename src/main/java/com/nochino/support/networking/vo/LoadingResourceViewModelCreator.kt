package com.nochino.support.networking.vo

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer

/**
 * Bundle arg key pointing to the canonical class name of the [LoadingResourceViewModel]
 * class to be observed.
 */
const val ARG_VIEW_MODEL_CLASS_NAME = "viewModelClassName"

/**
 * Defines contract between an object which creates and uses [LoadingResourceViewModel]
 */
interface LoadingResourceViewModelCreator<D, VM : LoadingResourceViewModel<D>> {

    /**
     * Represents the Class object of the [VM] type. This Class object can be used to create the
     * [ViewModel] instance of [loadingResourceViewModel] via [ViewModelProviders.of].
     * Allows implementations the ability to override and provide the [LoadingResourceViewModel]
     * [Class] which contains the [LiveData] being fetched and observed.
     */
    val loadingResourceViewModelClass: Class<VM>?

    /**
     * [LoadingResourceViewModel] class responsible for fetching, caching, and returning [LoadingResource] [LiveData].
     * Changes to the [LoadingResource] [LiveData] are dispatched to the associated [Observer.onChanged] callback.
     */
    var loadingResourceViewModel: LoadingResourceViewModel<D>?

    /**
     * Creates the observed [LoadingResourceViewModel] from bundle argument. Implementations
     * should call the Companion's [createFromBundle] method to get a class instance
     * of the [LoadingResourceViewModel] via java reflection.
     */
    @Suppress("UNCHECKED_CAST")
    fun createViewModelClass(arguments: Bundle?): Class<VM>?

    /**
     * Creates the [loadingResourceViewModel] instance containing the [LoadingResource] [LiveData]
     * that will be fetched and observed by this fragment.
     */
    fun initLoadingResourceViewModel(loadingResourceViewModelClass: Class<VM>?)

    companion object {
        /**
         * Uses java reflection to create a Class object of the [VM] type
         *
         * @return A Class object of the [LoadingResourceViewModel] parsed from the [Bundle] [arguments]
         */
        fun <VM> createFromBundle(arguments: Bundle?) : Class<VM>? {
            return arguments?.getString(ARG_VIEW_MODEL_CLASS_NAME, null)?.let {
                // Suppressed because cast is ensured in class declaration
                // via "where VM: LoadingResourceViewModel<D>" ;-P
                @Suppress("UNCHECKED_CAST")
                return Class.forName(it) as Class<VM>
            }
        }
    }
}
package com.nochino.support.networking.presenter

import com.nochino.support.networking.vo.LoadingResource
import com.nochino.support.networking.vo.LoadingResourceViewModel

interface LoadingResourceContract {
    /**
     * Specifies the contract between the view (what handles displaying the data) and the presenter.
     * This is implemented by a fragment, view...etc.
     *
     * The View layer handles the user interface and these are the only functions that
     * we expose to other layers.
     */
    interface View<D> {
        fun showLoading(loadingResource: LoadingResource<D>)
        fun showSuccess(loadingResource: LoadingResource<D>)
        fun showError(loadingResource: LoadingResource<D>)
    }

    /**
     * Describes the actions that can be started from the View
     */
    interface LoadingResourceActionsListener<D> {
        fun fetchLoadingResource(
            loadingResource: LoadingResource<D>,
            loadingResourceViewModel: LoadingResourceViewModel<D>
        )

        fun displaySuccess(loadingResource: LoadingResource<D>)
        fun displayError(loadingResource: LoadingResource<D>)
    }
}
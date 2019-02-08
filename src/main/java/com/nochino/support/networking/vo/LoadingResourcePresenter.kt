package com.nochino.support.networking.vo

import com.nochino.support.networking.presenter.LoadingResourceContract

/**
 * Presenter of a [LoadingResource]. User actions are forwarded from the
 * associated LoadingResourceContract view to the presenter.
 */
class LoadingResourcePresenter<D>(

    private val presenterView: LoadingResourceContract.View<D>

) : LoadingResourceContract.LoadingResourceActionsListener<D> {

    override fun fetchLoadingResource(
        loadingResource: LoadingResource<D>,
        loadingResourceViewModel: LoadingResourceViewModel<D>) {
        presenterView.showLoading(loadingResource)
    }

    override fun displaySuccess(loadingResource: LoadingResource<D>) {
        presenterView.showSuccess(loadingResource)
    }

    override fun displayError(loadingResource: LoadingResource<D>) {
        presenterView.showError(loadingResource)
    }
}
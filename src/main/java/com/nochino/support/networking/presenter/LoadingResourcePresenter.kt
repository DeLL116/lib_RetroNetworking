package com.nochino.support.networking.presenter

import com.nochino.support.networking.vo.LoadingResource

class LoadingResourcePresenter<D>(private val presenterView: LoadingResourcePresenterView<D>) {

    fun onLoading(loadingResource: LoadingResource<D>) {
        presenterView.showLoading(loadingResource)
    }

    fun onError(loadingResource: LoadingResource<D>) {
        presenterView.showError(loadingResource)
    }

    fun onSuccess(loadingResource: LoadingResource<D>) {
        presenterView.showSuccess(loadingResource)
    }
}

interface LoadingResourcePresenterView<D> {
    fun showError(loadingResource: LoadingResource<D>)
    fun showSuccess(loadingResource: LoadingResource<D>)
    fun showLoading(loadingResource: LoadingResource<D>)
}
package com.nochino.support.networking.vo

import com.nochino.support.networking.presenter.LoadingResourceContract
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class LoadingResourcePresenterTest {

    @Mock
    lateinit var loadingResourceView: LoadingResourceContract.View<Any>

    @Mock
    lateinit var loadingResourceViewModel: LoadingResourceViewModel<Any>

    lateinit var loadingResourcePresenter: LoadingResourcePresenter<Any>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        loadingResourcePresenter = LoadingResourcePresenter(loadingResourceView)
    }

    @After
    fun tearDown() {
        /* No current default op */
    }

    @Test
    fun fetchLoadingResource() {
        val loadingResourceLoading = LoadingResource.loading(null)
        loadingResourcePresenter.fetchLoadingResource(loadingResourceLoading, loadingResourceViewModel)
        verify(loadingResourceView).showLoading(loadingResourceLoading)
    }

    @Test
    fun displaySuccess() {
        val loadingResourceSuccess = LoadingResource.success(null)
        loadingResourcePresenter.displaySuccess(loadingResourceSuccess)
        verify(loadingResourceView).showSuccess(loadingResourceSuccess)
    }

    @Test
    fun displayError() {
        val loadingResourceError = LoadingResource.error("You've crossed the streams!", null)
        loadingResourcePresenter.displayError(loadingResourceError)
        verify(loadingResourceView).showError(loadingResourceError)
    }
}
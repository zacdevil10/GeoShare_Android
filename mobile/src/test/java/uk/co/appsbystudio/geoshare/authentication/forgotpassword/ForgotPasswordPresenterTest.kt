package uk.co.appsbystudio.geoshare.authentication.forgotpassword

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class ForgotPasswordPresenterTest {

    private var presenter: ForgotPasswordPresenter? = null
    private val interactor = ForgotPasswordInteractorImpl()
    @Mock
    val view: ForgotPasswordView = mock(ForgotPasswordView::class.java)
    @Mock
    val listener: ForgotPasswordInteractor.OnRecoverFinishedListener = mock(ForgotPasswordInteractor.OnRecoverFinishedListener::class.java)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        presenter = ForgotPasswordPresenterImpl(view, interactor)
    }

    @Test
    fun showProgress_whenValidateEmailStarts() {
        presenter?.validate("")

        verify(view).showProgress()
    }

    @Test
    fun showEmailError_whenEmailIsEmpty() {
        interactor.recover("", listener)

        verify(listener).onEmailError()
    }
}
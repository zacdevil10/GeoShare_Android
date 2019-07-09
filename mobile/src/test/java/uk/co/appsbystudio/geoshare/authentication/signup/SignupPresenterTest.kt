package uk.co.appsbystudio.geoshare.authentication.signup

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class SignupPresenterTest {

    private var presenter: SignupPresenter? = null
    private var interactor: SignupInteractor = SignupInteractorImpl()
    @Mock
    val view: SignupView = mock(SignupView::class.java)
    @Mock
    val listener: SignupInteractor.OnSignupFinishedListener = mock(SignupInteractor.OnSignupFinishedListener::class.java)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        presenter = SignupPresenterImpl(view, interactor)
    }

    @Test
    fun showProgress_whenValidateEmailAndPasswordStarts() {
        presenter?.validate("", "", "", false)

        verify(view).showProgress()
    }

    @Test
    fun showNameError_whenNameIsEmpty() {
        interactor.signup("", "test@test.com", "SecretThing", true, listener)

        verify(listener).onNameError()
    }

    @Test
    fun showEmailError_whenEmailIsEmpty() {
        interactor.signup("Test", "", "SecretThing", true, listener)

        verify(listener).onEmailError()
    }

    @Test
    fun showPasswordError_whenPasswordIsEmpty() {
        interactor.signup("Test", "test@test.com", "", true, listener)

        verify(listener).onPasswordError()
    }

    @Test
    fun showTermsError_whenTermsIsNotAccepted() {
        interactor.signup("Test", "test@test.com", "SecretThing", false, listener)

        verify(listener).onTermsError()
    }
}
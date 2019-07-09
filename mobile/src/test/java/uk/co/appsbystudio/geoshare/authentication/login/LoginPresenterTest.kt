package uk.co.appsbystudio.geoshare.authentication.login

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class LoginPresenterTest {

    private var presenter: LoginPresenter? = null
    private var interactor: LoginInteractor = LoginInteractorImpl()
    @Mock
    val view: LoginView = mock(LoginView::class.java)
    @Mock
    val listener: LoginInteractor.OnLoginFinishedListener = mock(LoginInteractor.OnLoginFinishedListener::class.java)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        presenter = LoginPresenterImpl(view, interactor)
    }

    @Test
    fun showProgress_whenValidateEmailAndPasswordStarts() {
        presenter?.validate("", "")

        verify(view).showProgress()
    }

    @Test
    fun showEmailError_whenEmailIsEmpty() {
        interactor.login("", "SecretThing", listener)

        verify(listener).onEmailError()
    }

    @Test
    fun showPasswordError_whenPasswordIsEmpty() {
        interactor.login("test@test.com", "", listener)

        verify(listener).onPasswordError()
    }

}
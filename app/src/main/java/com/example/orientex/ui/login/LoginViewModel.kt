package com.example.orientex.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.orientex.data.LoginRepository
import com.example.orientex.data.Result

import com.example.orientex.R
import kotlin.math.log

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _isSignedIn = MutableLiveData<Boolean>(false)
    var isSignedIn: LiveData<Boolean> = _isSignedIn

    fun setSignedIn(newValue : Boolean) {
        // use postvalue() to make the assignation on the main (UI) thread
        _isSignedIn.postValue(newValue)
    }

    private var passwordLengthBool = false
    private fun setPasswordLengthBool (tempBool: Boolean) {passwordLengthBool = tempBool}

    private var upperCaseBool = false
    private fun setUpperCaseBool (tempBool: Boolean) {upperCaseBool = tempBool}

    private var lowerCaseBool = false
    private fun setLowerCaseBool (tempBool: Boolean) {lowerCaseBool = tempBool}

    private var containsDigitBool = false
    private fun setContainsDigitBool (tempBool: Boolean) {containsDigitBool = tempBool}

    private var containsSpecialCharacterBool = false
    private fun setContainsSpecialCharacterBool (tempBool: Boolean) {containsSpecialCharacterBool = tempBool}

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)

        } else if (!isPasswordValid(password) && passwordLengthBool) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password_length)
            setPasswordLengthBool(false)

        } else if (!isPasswordValid(password) && upperCaseBool) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password_capitalization)
            setUpperCaseBool(false)

        } else if (!isPasswordValid(password) && lowerCaseBool) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password_capitalization)
            setLowerCaseBool(false)

        } else if (!isPasswordValid(password) && containsDigitBool) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password_digit)
            setContainsDigitBool(false)

        } else if (!isPasswordValid(password) && containsSpecialCharacterBool) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password_character)
            setContainsSpecialCharacterBool(false)

        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')
            && (username.endsWith("uwf.edu"))
            && username.isNotBlank()) {
                Patterns.EMAIL_ADDRESS.matcher(username).matches()
        }
        else { false }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        if (password.length < 8) {
            setPasswordLengthBool(true)
            return false }

        if (password.filter { it.isDigit() }.firstOrNull() == null) {
            setContainsDigitBool(true)
            return false }

        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) {
            setUpperCaseBool(true)
            return false }

        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) {
            setLowerCaseBool(true)
            return false }

        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) {
            setContainsSpecialCharacterBool(true)
            return false }

        return true
    }
}
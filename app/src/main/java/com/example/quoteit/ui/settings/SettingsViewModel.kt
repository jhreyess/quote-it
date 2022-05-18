package com.example.quoteit.ui.settings

import androidx.lifecycle.*
import com.example.quoteit.data.UsersRepository
import com.example.quoteit.data.network.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsViewModel(private val usersRepo: UsersRepository): ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _success = MutableLiveData(false)
    val success: LiveData<Boolean> get() = _success

    private val _error = MutableLiveData("")
    val error: LiveData<String> get() = _error

    fun updatePassword(pass: String, confirm: String){
        when(validatePassword(pass, confirm)){
            Validation.NO_MATCH -> {_error.value = "Las contraseñas no coinciden"}
            Validation.WEAK_PASSWORD -> {_error.value = "La contraseña debe contener 6 o más caracteres"}
            Validation.IS_EMPTY -> {_error.value = "Llena los campos correspondientes"}
            else -> {
                viewModelScope.launch {
                    usersRepo.updateUserPassword(pass).collect { result ->
                        when(result){
                            is Result.Success -> {_success.value = result.data.success}
                            is Result.Error -> {_error.value = result.exception.message}
                            is Result.Loading -> {_isLoading.value = result.isLoading}
                        }
                    }
                }
            }
        }
    }

    private fun validatePassword(password: String, confirm: String): Validation{
        return when {
            confirm.isBlank() -> { Validation.IS_EMPTY }
            password.isBlank() -> { Validation.IS_EMPTY }
            (password.length < 6) -> { Validation.WEAK_PASSWORD }
            !password.equals(confirm, false) -> { Validation.NO_MATCH }
            else -> { Validation.IS_VALID }
        }
    }

    private enum class Validation{
        IS_VALID, WEAK_PASSWORD, NO_MATCH, IS_EMPTY
    }
}

class SettingsViewModelFactory(
    private val repo: UsersRepository,
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}
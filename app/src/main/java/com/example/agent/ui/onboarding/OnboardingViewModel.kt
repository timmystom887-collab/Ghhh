package com.example.agent.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agent.data.local.entity.ProfileEntity
import com.example.agent.data.repository.AgentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AgentRepository(application)

    val profile: StateFlow<ProfileEntity?> = repository.profile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveProfile(fullName: String, email: String, phone: String) {
        viewModelScope.launch {
            val current = repository.getProfileSync() ?: ProfileEntity()
            repository.updateProfile(
                current.copy(
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    termsAccepted = true,
                    onboardingCompleted = true
                )
            )
        }
    }
}

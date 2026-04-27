package com.simpe.bridge.appmovil.ui.screens.messages

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simpe.bridge.appmovil.data.local.AppDatabase
import com.simpe.bridge.appmovil.data.repository.MessageRepositoryImpl
import com.simpe.bridge.appmovil.domain.usecases.GetMessagesUseCase
import com.simpe.bridge.appmovil.domain.usecases.SaveMessageUseCase
import com.simpe.bridge.appmovil.domain.usecases.SmsMessage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MessagesViewModel(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val saveMessageUseCase: SaveMessageUseCase,
) : ViewModel() {

    val messages: StateFlow<List<SmsMessage>> = getMessagesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun saveMessage(message: SmsMessage) {
        viewModelScope.launch {
            saveMessageUseCase(message)
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val appContext = context.applicationContext
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val dao = AppDatabase.getInstance(appContext).messageDao()
                    val repository = MessageRepositoryImpl(dao)
                    val getMessagesUseCase = GetMessagesUseCase(repository)
                    val saveMessageUseCase = SaveMessageUseCase(repository)

                    @Suppress("UNCHECKED_CAST")
                    return MessagesViewModel(
                        getMessagesUseCase = getMessagesUseCase,
                        saveMessageUseCase = saveMessageUseCase,
                    ) as T
                }
            }
        }
    }
}

package com.hyunakim.gunsiya.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hyunakim.gunsiya.GunsiyaApplication
import com.hyunakim.gunsiya.ui.home.HomeViewModel
import com.hyunakim.gunsiya.ui.qna.QnaViewModel
import com.hyunakim.gunsiya.ui.user.UserEntryViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for ItemEditViewModel
//        initializer {
//            ItemEditViewModel(
//                this.createSavedStateHandle()
//            )
//        }
//        // Initializer for ItemEntryViewModel
        initializer {
            HomeViewModel(gunsiyaApplication().container.usersRepository, gunsiyaApplication().container.recordsRepository)
        }
        initializer {
            UserEntryViewModel(gunsiyaApplication().container.usersRepository)
        }
        initializer {
            QnaViewModel(gunsiyaApplication().container.qnasRepository)
        }
//
//        // Initializer for ItemDetailsViewModel
//        initializer {
//            ItemDetailsViewModel(
//                this.createSavedStateHandle()
//            )
//        }
//
//        // Initializer for HomeViewModel
//        initializer {
//            HomeViewModel(inventoryApplication().container.itemsRepository)
//        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.gunsiyaApplication(): GunsiyaApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GunsiyaApplication)
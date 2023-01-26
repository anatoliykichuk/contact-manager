package com.example.contactmanager

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ContactsViewModelFactory(
    private val context: Context?
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContactsViewModel(context) as T
    }
}
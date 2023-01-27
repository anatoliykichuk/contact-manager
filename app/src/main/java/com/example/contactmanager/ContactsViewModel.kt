package com.example.contactmanager

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactsViewModel(private val context: Context?) : ViewModel() {

    private val liveData: MutableLiveData<List<String>> = MutableLiveData()
    private var contacts = mutableListOf<String>()

    fun getLiveData(): LiveData<List<String>> = liveData

    fun getContacts() {
        Thread {
            context?.let {
                val contentResolver: ContentResolver = it.contentResolver
                val cursorWithContacts: Cursor? = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    null,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME + " ASC"
                )

                cursorWithContacts?.let { cursor ->
                    val columnIndex = cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )

                    for (rowIndex in 0..cursor.count) {
                        if (cursor.moveToPosition(rowIndex)) {

                            val name = cursor.getString(columnIndex)
                            contacts.add(name)
                        }
                    }
                }

                cursorWithContacts?.close()
            }

            liveData.postValue(contacts)

        }.start()
    }
}
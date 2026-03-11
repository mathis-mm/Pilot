package com.example.pilot.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pilot.data.PilotRepository
import com.example.pilot.data.model.Contact
import kotlinx.coroutines.flow.*

class ContactViewModel : ViewModel() {
    private val repo = PilotRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val contacts: StateFlow<List<Contact>> = repo.contacts

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getFilteredContacts(): List<Contact> {
        val query = _searchQuery.value
        val all = repo.contacts.value
        return if (query.isBlank()) {
            all.sortedWith(compareByDescending<Contact> { it.isFavorite }.thenBy { it.name })
        } else {
            all.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.phone.contains(query, ignoreCase = true) ||
                it.email.contains(query, ignoreCase = true)
            }.sortedWith(compareByDescending<Contact> { it.isFavorite }.thenBy { it.name })
        }
    }

    fun addContact(name: String, phone: String = "", email: String = "", note: String = "") {
        repo.addContact(Contact(name = name, phone = phone, email = email, note = note))
    }

    fun toggleFavorite(contact: Contact) {
        repo.updateContact(contact.copy(isFavorite = !contact.isFavorite))
    }

    fun updateContact(contact: Contact) {
        repo.updateContact(contact)
    }

    fun deleteContact(contact: Contact) {
        repo.deleteContact(contact)
    }
}

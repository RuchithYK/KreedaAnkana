package com.example.kreedaankana.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kreedaankana.data.Booking
import com.example.kreedaankana.repository.FirebaseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    // live list of all bookings
    val bookings = repository.getBookings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // input state lives in ViewModel
    var teamName by mutableStateOf("")
        private set
    var sport by mutableStateOf("Cricket")
        private set
    var date by mutableStateOf("")
        private set
    var startTime by mutableStateOf("")
        private set
    var endTime by mutableStateOf("")
        private set
    // which booking is being edited
    var editingBooking by mutableStateOf<Booking?>(null)
        private set

    fun onTeamNameChange(v: String) { teamName = v }
    fun onSportChange(v: String) { sport = v }
    fun onDateChange(v: String) { date = v }
    fun onStartTimeChange(v: String) { startTime = v }
    fun onEndTimeChange(v: String) { endTime = v }

    fun startEditing(booking: Booking) {
        editingBooking = booking
        teamName = booking.teamName
        sport = booking.sport
        date = booking.date
        startTime = booking.startTime
        endTime = booking.endTime
    }
    fun cancelEditing() {
        editingBooking = null
        clearInputs()
    }

    fun saveBooking(userId: String) {
        if (teamName.isBlank() || date.isBlank()) return
        viewModelScope.launch {
            if (editingBooking != null) {
                // UPDATE existing booking
                repository.updateBooking(
                    editingBooking!!.copy(
                        teamName = teamName,
                        sport = sport,
                        date = date,
                        startTime = startTime,
                        endTime = endTime
                    )
                )
                editingBooking = null
            } else {
                // CREATE new booking
                repository.addBooking(
                    Booking(
                        userId = userId,
                        teamName = teamName,
                        sport = sport,
                        date = date,
                        startTime = startTime,
                        endTime = endTime
                    )
                )
            }
            clearInputs()
        }
    }

    private fun clearInputs() {
        teamName = ""
        date = ""
        startTime = ""
        endTime = ""
    }
    fun addBooking(userId: String) {
        if (teamName.isBlank() || date.isBlank()) return
        viewModelScope.launch {
            repository.addBooking(
                Booking(
                    userId = userId,
                    teamName = teamName,
                    sport = sport,
                    date = date,
                    startTime = startTime,
                    endTime = endTime
                )
            )
            // clear inputs after saving
            teamName = ""
            date = ""
            startTime = ""
            endTime = ""
        }
    }

    fun deleteBooking(bookingId: String) {
        viewModelScope.launch {
            repository.deleteBooking(bookingId)
        }
    }
}
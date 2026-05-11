package com.example.kreedaankana.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kreedaankana.data.Booking
import com.example.kreedaankana.repository.FirebaseRepository
import com.example.kreedaankana.ui.theme.components.timesOverlap
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    val bookings = repository.getBookings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly, // loads immediately so conflict check works
        initialValue = emptyList()
    )

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
    var editingBooking by mutableStateOf<Booking?>(null)
        private set
    var conflictError by mutableStateOf("")
        private set
    var isSaving by mutableStateOf(false)
        private set
    var saveSuccess by mutableStateOf(false)
        private set

    fun onTeamNameChange(v: String) { teamName = v }
    fun onSportChange(v: String) { sport = v }
    fun onDateChange(v: String) { date = v }
    fun onStartTimeChange(v: String) { startTime = v }
    fun onEndTimeChange(v: String) { endTime = v }
    fun clearConflictError() { conflictError = "" }
    fun clearSaveSuccess() { saveSuccess = false }

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

    fun saveBooking(userId: String, onComplete: (Boolean) -> Unit) {
        if (teamName.isBlank() || date.isBlank() ||
            startTime.isBlank() || endTime.isBlank()
        ) {
            conflictError = "Please fill all fields!"
            onComplete(false)
            return
        }

        val currentBookings = bookings.value

        val conflict = currentBookings.find { existing ->
            if (editingBooking != null && existing.id == editingBooking!!.id) return@find false
            if (existing.date != date) return@find false
            timesOverlap(
                newStart = startTime,
                newEnd = endTime,
                existingStart = existing.startTime,
                existingEnd = existing.endTime
            )
        }

        if (conflict != null) {
            conflictError = "Slot already booked by \"${conflict.teamName}\" " +
                    "(${conflict.startTime} – ${conflict.endTime}). Choose a different time."
            onComplete(false)
            return
        }

        isSaving = true
        viewModelScope.launch {
            try {
                if (editingBooking != null) {
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
                saveSuccess = true
                onComplete(true)
            } catch (e: Exception) {
                conflictError = "Failed to save: ${e.message}"
                onComplete(false)
            } finally {
                isSaving = false
            }
        }
    }

    fun deleteBooking(bookingId: String) {
        viewModelScope.launch {
            repository.deleteBooking(bookingId)
        }
    }

    private fun clearInputs() {
        teamName = ""
        date = ""
        startTime = ""
        endTime = ""
        sport = "Cricket"   // ✅ reset sport
        conflictError = ""
    }
}
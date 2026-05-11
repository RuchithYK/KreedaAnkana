package com.example.kreedaankana.ui.theme.components

// Converts "10:00 AM", "4:30 PM", "04:00 PM" → total minutes from midnight
// Handles both single-digit and double-digit hours
fun timeToMinutes(timeStr: String): Int {
    return try {
        val trimmed = timeStr.trim()
        // split "10:00 AM" or "4:30 PM" into parts
        val parts = trimmed.split(" ")
        if (parts.size < 2) return -1

        val timePart = parts[0]   // "10:00" or "4:30"
        val amPm = parts[1].uppercase()  // "AM" or "PM"

        val hourMin = timePart.split(":")
        if (hourMin.size < 2) return -1

        var hour = hourMin[0].toInt()
        val minute = hourMin[1].toInt()

        // convert to 24-hour format
        if (amPm == "PM" && hour != 12) hour += 12
        if (amPm == "AM" && hour == 12) hour = 0

        // return total minutes
        (hour * 60) + minute

    } catch (e: Exception) {
        -1
    }
}

// ✅ CONFLICT DETECTION:
// Returns true if the two time ranges overlap AT ALL.
// This prevents:
//   - Same start/end bookings
//   - Bookings within another booking's window (e.g. 4:15–4:45 inside 4:00–5:00)
//   - Bookings that partially overlap (e.g. 4:30–5:30 overlaps with 4:00–5:00)
//
// Two ranges [A,B] and [C,D] overlap if: A < D AND B > C
fun timesOverlap(
    newStart: String,
    newEnd: String,
    existingStart: String,
    existingEnd: String
): Boolean {
    val newStartMin = timeToMinutes(newStart)
    val newEndMin = timeToMinutes(newEnd)
    val existingStartMin = timeToMinutes(existingStart)
    val existingEndMin = timeToMinutes(existingEnd)

    // if any conversion failed → skip this check
    if (newStartMin == -1 || newEndMin == -1 ||
        existingStartMin == -1 || existingEndMin == -1
    ) return false

    // Standard interval overlap: new starts before existing ends AND new ends after existing starts
    return newStartMin < existingEndMin && newEndMin > existingStartMin
}
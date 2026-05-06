package com.example.kreedaankana.repository

import com.example.kreedaankana.data.Booking
import com.example.kreedaankana.data.Challenge
import com.example.kreedaankana.data.Score
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    // auth = Firebase Authentication
    // like the login manager
    private val auth = FirebaseAuth.getInstance()

    // db = Firestore database
    // like your MongoDB connection
    private val db = FirebaseFirestore.getInstance()

    // get current logged in user
    fun getCurrentUser() = auth.currentUser

    // sign out
    fun signOut() = auth.signOut()

    // ─────────────────────────────────────────
    // BOOKINGS
    // ─────────────────────────────────────────

    // get all bookings as live Flow
    // callbackFlow converts Firestore listener to Flow
    // so whenever data changes in Firebase → UI updates automatically
    // exactly like Room's Flow but for cloud database!
    fun getBookings(): Flow<List<Booking>> = callbackFlow {
        val listener = db.collection("bookings")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val bookings = snapshot?.documents?.map { doc ->
                    doc.toObject(Booking::class.java)!!.copy(id = doc.id)
                } ?: emptyList()
                trySend(bookings)
            }
        // when Flow is canceled → remove the listener
        awaitClose { listener.remove() }
    }

    // add new booking
    suspend fun addBooking(booking: Booking) {
        db.collection("bookings")
            .add(booking)
            .await()
        // .await() = wait for Firebase to confirm saved
        // like await in JS async/await
    }

    // delete booking — only owner can delete their own
    suspend fun deleteBooking(bookingId: String) {
        db.collection("bookings")
            .document(bookingId)
            .delete()
            .await()
    }

    // ─────────────────────────────────────────
    // CHALLENGES
    // ─────────────────────────────────────────

    fun getChallenges(): Flow<List<Challenge>> = callbackFlow {
        val listener = db.collection("challenges")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val challenges = snapshot?.documents?.map { doc ->
                    doc.toObject(Challenge::class.java)!!.copy(id = doc.id)
                } ?: emptyList()
                trySend(challenges)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addChallenge(challenge: Challenge) {
        db.collection("challenges")
            .add(challenge)
            .await()
    }

    suspend fun deleteChallenge(challengeId: String) {
        db.collection("challenges")
            .document(challengeId)
            .delete()
            .await()
    }

    // ─────────────────────────────────────────
    // SCORES
    // ─────────────────────────────────────────

    fun getScores(): Flow<List<Score>> = callbackFlow {
        val listener = db.collection("scores")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val scores = snapshot?.documents?.map { doc ->
                    doc.toObject(Score::class.java)!!.copy(id = doc.id)
                } ?: emptyList()
                trySend(scores)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addScore(score: Score) {
        db.collection("scores")
            .add(score)
            .await()
    }

    suspend fun deleteScore(scoreId: String) {
        db.collection("scores")
            .document(scoreId)
            .delete()
            .await()
    }
}
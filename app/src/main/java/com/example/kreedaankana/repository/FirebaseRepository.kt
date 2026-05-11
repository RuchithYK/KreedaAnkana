package com.example.kreedaankana.repository

import com.example.kreedaankana.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getCurrentUser() = auth.currentUser
    fun signOut() = auth.signOut()

    // ─────────────────────────────────────────
    // USER PROFILE
    // ─────────────────────────────────────────

    fun getUserProfile(userId: String): Flow<UserProfile?> = callbackFlow {
        // FIXED — guard against empty userId
        if (userId.isEmpty()) {
            trySend(null)
            awaitClose {}
            return@callbackFlow
        }
        val listener = db.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val profile = snapshot?.toObject(UserProfile::class.java)
                trySend(profile)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        if (profile.userId.isEmpty()) return  // guard
        db.collection("users")
            .document(profile.userId)
            .set(profile)
            .await()
    }

    suspend fun updateTeamName(userId: String, teamName: String) {
        if (userId.isEmpty()) return
        db.collection("users")
            .document(userId)
            .update("teamName", teamName)
            .await()
    }

    // FIXED — now also deletes team if user is captain or removes from members
    // FIXED — re-authenticate before deleting, then sign out properly
    suspend fun deleteAllUserData(userId: String) {
        if (userId.isEmpty()) return

        println("=== STARTING DELETE FOR USER: $userId ===")

        try {
            // delete bookings
            println("Deleting bookings...")
            val bookings = db.collection("bookings")
                .whereEqualTo("userId", userId).get().await()
            println("Found ${bookings.documents.size} bookings")
            bookings.documents.forEach {
                it.reference.delete().await()
                println("Deleted booking: ${it.id}")
            }

            // delete challenges
            println("Deleting challenges...")
            val challenges = db.collection("challenges")
                .whereEqualTo("userId", userId).get().await()
            println("Found ${challenges.documents.size} challenges")
            challenges.documents.forEach {
                it.reference.delete().await()
                println("Deleted challenge: ${it.id}")
            }

            // delete scores
            println("Deleting scores...")
            val scores = db.collection("scores")
                .whereEqualTo("userId", userId).get().await()
            println("Found ${scores.documents.size} scores")
            scores.documents.forEach {
                it.reference.delete().await()
                println("Deleted score: ${it.id}")
            }

            // delete team if captain
            println("Deleting captain teams...")
            val captainTeams = db.collection("teams")
                .whereEqualTo("captainId", userId).get().await()
            println("Found ${captainTeams.documents.size} captain teams")
            captainTeams.documents.forEach {
                it.reference.delete().await()
                println("Deleted team: ${it.id}")
            }

            // remove from team members
            println("Removing from team members...")
            val allTeams = db.collection("teams").get().await()
            allTeams.documents.forEach { doc ->
                val members = doc.get("members") as? List<Map<String, Any>> ?: emptyList()
                val userMember = members.find { it["userId"] == userId }
                if (userMember != null) {
                    println("Removing member from team: ${doc.id}")
                    doc.reference.update(
                        "members", FieldValue.arrayRemove(userMember)
                    ).await()
                }
            }

            // delete user profile
            println("Deleting user profile...")
            db.collection("users").document(userId).delete().await()
            println("User profile deleted!")

            // (Sign out happens later in ProfileViewModel after user auth deletion)
            println("=== DELETE COMPLETE ===")

        } catch (e: Exception) {
            println("=== DELETE FAILED: ${e.message} ===")
            e.printStackTrace()
            throw e  // rethrow so ProfileViewModel catches it
        }
    }

    // ─────────────────────────────────────────
    // TEAMS — FIXED joinTeam crash
    // ─────────────────────────────────────────

    fun getAllTeams(): Flow<List<Team>> = callbackFlow {
        val listener = db.collection("teams")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val teams = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Team::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(teams)
            }
        awaitClose { listener.remove() }
    }

    fun getTeam(teamId: String): Flow<Team?> = callbackFlow {
        if (teamId.isEmpty()) {
            trySend(null)
            awaitClose {}
            return@callbackFlow
        }
        val listener = db.collection("teams")
            .document(teamId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val team = snapshot?.toObject(Team::class.java)?.copy(id = snapshot.id)
                trySend(team)
            }
        awaitClose { listener.remove() }
    }

    suspend fun createTeam(
        teamName: String,
        sport: String,
        captainId: String,
        captainName: String
    ): String {
        if (captainId.isEmpty()) return ""
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val team = Team(
            teamName = teamName,
            sport = sport,
            captainId = captainId,
            captainName = captainName,
            members = listOf(
                TeamMember(
                    userId = captainId,
                    displayName = captainName,
                    joinedAt = today
                )
            ),
            createdAt = today
        )
        val docRef = db.collection("teams").add(team).await()
        val teamId = docRef.id
        db.collection("users").document(captainId).update(
            mapOf("teamId" to teamId, "teamName" to teamName)
        ).await()
        return teamId
    }

    // FIXED — joinTeam now safely handles Firestore map format
    suspend fun joinTeam(
        teamId: String,
        userId: String,
        displayName: String,
        teamName: String
    ) {
        if (teamId.isEmpty() || userId.isEmpty()) return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // FIXED — use HashMap explicitly so Firestore reads it correctly
        val newMember = hashMapOf(
            "userId" to userId,
            "displayName" to displayName,
            "joinedAt" to today
        )

        db.collection("teams").document(teamId)
            .update("members", FieldValue.arrayUnion(newMember))
            .await()

        db.collection("users").document(userId).update(
            mapOf("teamId" to teamId, "teamName" to teamName)
        ).await()
    }

    suspend fun leaveTeam(teamId: String, userId: String, displayName: String) {
        if (teamId.isEmpty() || userId.isEmpty()) return

        // get current members and find exact match to remove
        val teamDoc = db.collection("teams").document(teamId).get().await()
        val members = teamDoc.get("members") as? List<Map<String, Any>> ?: emptyList()
        val memberToRemove = members.find { it["userId"] == userId }

        if (memberToRemove != null) {
            db.collection("teams").document(teamId)
                .update("members", FieldValue.arrayRemove(memberToRemove))
                .await()
        }

        db.collection("users").document(userId).update(
            mapOf("teamId" to "", "teamName" to "")
        ).await()
    }

    fun getTeamScores(teamName: String): Flow<List<Score>> = callbackFlow {
        val listener = db.collection("scores")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val scores = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val score = doc.toObject(Score::class.java)?.copy(id = doc.id)
                        if (score?.team1 == teamName || score?.team2 == teamName) score
                        else null
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(scores)
            }
        awaitClose { listener.remove() }
    }

    // ─────────────────────────────────────────
    // BOOKINGS
    // ─────────────────────────────────────────

    fun getBookings(): Flow<List<Booking>> = callbackFlow {
        val listener = db.collection("bookings")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val bookings = snapshot?.documents?.mapNotNull { doc ->
                    try { doc.toObject(Booking::class.java)?.copy(id = doc.id) }
                    catch (e: Exception) { null }
                } ?: emptyList()
                trySend(bookings)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addBooking(booking: Booking) {
        db.collection("bookings").add(booking).await()
    }

    suspend fun deleteBooking(bookingId: String) {
        db.collection("bookings").document(bookingId).delete().await()
    }

    suspend fun updateBooking(booking: Booking) {
        db.collection("bookings").document(booking.id).set(booking).await()
    }

    // ─────────────────────────────────────────
    // CHALLENGES
    // ─────────────────────────────────────────

    fun getChallenges(): Flow<List<Challenge>> = callbackFlow {
        val listener = db.collection("challenges")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val challenges = snapshot?.documents?.mapNotNull { doc ->
                    try { doc.toObject(Challenge::class.java)?.copy(id = doc.id) }
                    catch (e: Exception) { null }
                } ?: emptyList()
                trySend(challenges)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addChallenge(challenge: Challenge) {
        db.collection("challenges").add(challenge).await()
    }

    suspend fun updateChallenge(challenge: Challenge) {
        db.collection("challenges").document(challenge.id).set(challenge).await()
    }

    suspend fun deleteChallenge(challengeId: String) {
        db.collection("challenges").document(challengeId).delete().await()
    }

    // ─────────────────────────────────────────
    // SCORES
    // ─────────────────────────────────────────

    fun getScores(): Flow<List<Score>> = callbackFlow {
        val listener = db.collection("scores")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val scores = snapshot?.documents?.mapNotNull { doc ->
                    try { doc.toObject(Score::class.java)?.copy(id = doc.id) }
                    catch (e: Exception) { null }
                } ?: emptyList()
                trySend(scores)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addScore(score: Score) {
        db.collection("scores").add(score).await()
    }

    suspend fun updateScore(score: Score) {
        db.collection("scores").document(score.id).set(score).await()
    }

    suspend fun deleteScore(scoreId: String) {
        db.collection("scores").document(scoreId).delete().await()
    }

    // ─────────────────────────────────────────
    // HOME
    // ─────────────────────────────────────────

    fun getRecentBookings(): Flow<List<Booking>> = callbackFlow {
        val listener = db.collection("bookings")
            .limit(3)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val bookings = snapshot?.documents?.mapNotNull { doc ->
                    try { doc.toObject(Booking::class.java)?.copy(id = doc.id) }
                    catch (e: Exception) { null }
                } ?: emptyList()
                trySend(bookings)
            }
        awaitClose { listener.remove() }
    }

    fun getRecentChallenges(): Flow<List<Challenge>> = callbackFlow {
        val listener = db.collection("challenges")
            .limit(2)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val challenges = snapshot?.documents?.mapNotNull { doc ->
                    try { doc.toObject(Challenge::class.java)?.copy(id = doc.id) }
                    catch (e: Exception) { null }
                } ?: emptyList()
                trySend(challenges)
            }
        awaitClose { listener.remove() }
    }
}
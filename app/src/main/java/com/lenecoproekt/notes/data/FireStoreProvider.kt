package com.lenecoproekt.notes.data


import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.NoteResult
import com.lenecoproekt.notes.model.User
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val NOTES_COLLECTION = "notes"
private const val USERS_COLLECTION = "users"

class FireStoreProvider(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : RemoteDataProvider {

    companion object {
        private val TAG = "${FireStoreProvider::class.java.simpleName} :"
    }

    override suspend fun subscribeToAllNotes(): ReceiveChannel<NoteResult> =
        Channel<NoteResult>(Channel.CONFLATED).apply {
            var registration: ListenerRegistration? = null
            try {
                registration = getUserNotesCollection()
                    .addSnapshotListener { snapshot, e ->
                        val value = e?.let {
                            NoteResult.Error(it)
                        } ?: snapshot?.let { querySnapshot ->
                            val notes = querySnapshot.documents.map { documentSnapshot ->
                                documentSnapshot.toObject(Note::class.java)
                            }
                            notes.sortedByDescending { note -> note?.lastChanged }
                            NoteResult.Success(notes)
                        }

                        value?.let { offer(it) }
                    }

            } catch (e: Throwable) {
                offer(NoteResult.Error(e))
            }
            invokeOnClose { registration?.remove() }
        }

    override suspend fun getNoteById(id: String): Note =
        suspendCoroutine { continuation ->
            try {
                getUserNotesCollection().document(id)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        continuation.resume(snapshot.toObject(Note::class.java)!!)
                    }
                    .addOnFailureListener { exception ->
                        throw exception
                    }
            } catch (error: Throwable) {
                continuation.resumeWithException(error)
            }
        }

    override suspend fun saveNote(note: Note): Note =
        suspendCoroutine { continuation ->
            try {
                getUserNotesCollection().document(note.id)
                    .set(note).addOnSuccessListener {
                        continuation.resume(note)
                    }
                    .addOnFailureListener {
                        OnFailureListener { exception ->
                            throw exception
                        }
                    }
            } catch (error: Throwable) {
                continuation.resumeWithException(error)
            }

        }

    override suspend fun removeNote(id: String): Note? =
        suspendCoroutine { continuation ->
            try {
                getUserNotesCollection().document(id)
                    .delete()
                    .addOnSuccessListener {
                        continuation.resume(null)
                    }
                    .addOnFailureListener { throw it }
            } catch (error: Exception) {
                continuation.resumeWithException(error)
            }
        }

    private val currentUser
        get() = firebaseAuth.currentUser

    private fun getUserNotesCollection() = currentUser?.let {
        db.collection(USERS_COLLECTION).document(it.uid).collection(NOTES_COLLECTION)
    } ?: throw NoAuthException()

    override suspend fun getCurrentUser(): User? =
        suspendCoroutine { continuation ->
            currentUser?.let { firebaseUser ->
                continuation.resume(User(firebaseUser.displayName ?: "", firebaseUser.email ?: ""))
            } ?: continuation.resume(null)
        }
}
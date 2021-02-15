package com.lenecoproekt.notes.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.NoteResult
import com.lenecoproekt.notes.model.User
import java.lang.Exception

private const val NOTES_COLLECTION = "notes"
private const val USERS_COLLECTION = "users"

class FireStoreProvider(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : RemoteDataProvider {

    companion object {
        private val TAG = "${FireStoreProvider::class.java.simpleName} :"
    }

    override fun subscribeToAllNotes(): LiveData<NoteResult> {
        return MutableLiveData<NoteResult>().apply {
            try {
                getUserNotesCollection().addSnapshotListener { snapshot, error ->
                    value = error?.let { NoteResult.Error(it) }
                        ?: snapshot?.let { quarrySnapshot ->
                            val notes = quarrySnapshot.documents.map { doc ->
                                doc.toObject(Note::class.java)
                            }
                            NoteResult.Success(notes.sortedByDescending { it?.lastChanged })
                        }
                }
            } catch (error: Throwable) {
                value = NoteResult.Error(error)
            }
        }
    }

    override fun getNoteById(id: String): LiveData<NoteResult> {
        return MutableLiveData<NoteResult>().apply {
            try {
                getUserNotesCollection().document(id)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        value = NoteResult.Success(snapshot.toObject(Note::class.java))
                    }
                    .addOnFailureListener { exception ->
                        throw exception
                    }
            } catch (error: Throwable) {
                value = NoteResult.Error(error)
            }

        }
    }

    override fun saveNote(note: Note): LiveData<NoteResult> {
        return MutableLiveData<NoteResult>().apply {
            try {
                getUserNotesCollection().document(note.id)
                    .set(note).addOnSuccessListener {
                        Log.d(TAG, "Note $note is saved")
                        value = NoteResult.Success(note)
                    }
                    .addOnFailureListener {
                        OnFailureListener { exception ->
                            Log.d(TAG, "Error saving note $note, message: ${exception.message}")
                            throw exception
                        }
                    }
            } catch (error: Throwable) {
                value = NoteResult.Error(error)
            }

        }
    }

    override fun removeNote(id: String): LiveData<NoteResult> =
        MutableLiveData<NoteResult>().apply {
            try {
                getUserNotesCollection().document(id)
                    .delete()
                    .addOnSuccessListener {
                        value = NoteResult.Success(null)
                    }
                    .addOnFailureListener { throw it }
            } catch (error: Exception) {
                value = NoteResult.Error(error)
            }
        }

    private val currentUser
        get() = firebaseAuth.currentUser

    private fun getUserNotesCollection() = currentUser?.let {
        db.collection(USERS_COLLECTION).document(it.uid).collection(NOTES_COLLECTION)
    } ?: throw NoAuthException()

    override fun getCurrentUser(): LiveData<User?> =
        MutableLiveData<User?>().apply {
            value = currentUser?.let {
                User(
                    it.displayName ?: "",
                    it.email ?: ""
                )
            }
        }
}
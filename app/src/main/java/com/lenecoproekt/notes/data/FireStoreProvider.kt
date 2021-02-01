package com.lenecoproekt.notes.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.*
import com.lenecoproekt.notes.model.Note
import com.lenecoproekt.notes.model.NoteResult

private const val NOTES_COLLECTION = "notes"

class FireStoreProvider : RemoteDataProvider {

    companion object {
        private val TAG = "${FireStoreProvider::class.java.simpleName} :"
    }

    private val db = FirebaseFirestore.getInstance()
    private val notesReferences = db.collection(NOTES_COLLECTION)

    override fun subscribeToAllNotes(): LiveData<NoteResult> {
        return MutableLiveData<NoteResult>().apply {
            notesReferences.addSnapshotListener { snapshot, error ->
                value = error?.let { NoteResult.Error(it) }
                    ?: snapshot?.let { quarrySnapshot ->
                        val notes = quarrySnapshot.documents.map { doc ->
                            doc.toObject(Note::class.java)
                        }
                        NoteResult.Success(notes.sortedByDescending { it?.lastChanged })
                    }
            }
        }
    }

    override fun getNoteById(id: String): LiveData<NoteResult> {
        return MutableLiveData<NoteResult>().apply {
            notesReferences.document(id)
                .get()
                .addOnSuccessListener { snapshot ->
                    value = NoteResult.Success(snapshot.toObject(Note::class.java))
                }
                .addOnFailureListener { exception ->
                    value = NoteResult.Error(exception)
                }
        }
    }

    override fun saveNote(note: Note): LiveData<NoteResult> {
        return MutableLiveData<NoteResult>().apply {
            notesReferences.document(note.id)
                .set(note).addOnSuccessListener {
                    Log.d(TAG, "Note $note is saved")
                    value = NoteResult.Success(note)
                }
                .addOnFailureListener {
                    OnFailureListener { exception ->
                        Log.d(TAG, "Error saving note $note, message: ${exception.message}")
                        value = NoteResult.Error(exception)
                    }
                }
        }
    }

    override fun removeNote(id: String): LiveData<NoteResult> {
        notesReferences.document(id)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

        return subscribeToAllNotes()
    }

    override fun deleteAllNotes(notes: List<Note>): LiveData<NoteResult> {
        for (i in notes.indices) {
            notesReferences.document(notes[i].id)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
        return subscribeToAllNotes()
    }
}
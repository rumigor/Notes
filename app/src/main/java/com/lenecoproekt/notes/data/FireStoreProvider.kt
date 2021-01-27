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
        val result = MutableLiveData<NoteResult>()
        notesReferences.addSnapshotListener { value, error ->
            if (error != null) {
                result.value = NoteResult.Error(error)
            } else if (value != null) {
                val notes = mutableListOf<Note>()

                for (doc: QueryDocumentSnapshot in value) {
                    notes.add(doc.toObject(Note::class.java))
                }
                notes.sortByDescending {
                    it.lastChanged
                }
                result.value = NoteResult.Success(notes)
            }
        }
        return result
    }

    override fun getNoteById(id: String): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()
        notesReferences.document(id)
            .get()
            .addOnSuccessListener { snapshot ->
                result.value = NoteResult.Success(snapshot.toObject(Note::class.java))
            }
            .addOnFailureListener { exception ->
                result.value = NoteResult.Error(exception)
            }
        return result
    }

    override fun saveNote(note: Note): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()
        notesReferences.document(note.id)
            .set(note).addOnSuccessListener {
                Log.d(TAG, "Note $note is saved")
                result.value = NoteResult.Success(note)
            }.addOnFailureListener {
                OnFailureListener { exception ->
                    Log.d(TAG, "Error saving note $note, message: ${exception.message}")
                    result.value = NoteResult.Error(exception)
                }
            }
        return result
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
package com.keepsafe.notes.services.impl;

import com.keepsafe.notes.models.Note;
import com.keepsafe.notes.repositories.NoteRepository;
import com.keepsafe.notes.services.AuditLogService;
import com.keepsafe.notes.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    //implements all the methods declared in NoteService interface

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public Note createNoteForUser(String username, String content) {
        //This method helps a user to create a note and store the same in the database, the contents of notes would be coming from the specified controller including the details of the user that is creating the note.
        Note note = new Note(); //creating an instance of new note.
        note.setContent(content);
        note.setOwnerUsername(username);
        Note savedNote = noteRepository.save(note); //saving this newly created note in the database (with set details)
        auditLogService.logNoteCreation(username, note);
        return savedNote;
    }

    @Override
    public Note updateNoteForUser(Long noteId, String content, String username) {
        //This method helps the user in updating his/her note, first the user searches if the note is available or note with the provided ID, It also has content with which the original content needs to be updated along with user information.
        Note note = noteRepository.findById(noteId).orElseThrow(()
                -> new RuntimeException("Note not found")); //finding the note by ID that needs to be updated.
        note.setContent(content);
        Note updatedNote = noteRepository.save(note); //updating the content and saving the same in the database
        auditLogService.logNoteUpdate(username, note);
        return updatedNote;
    }

    @Override
    public void deleteNoteForUser(Long noteId, String username) {
        //Deleting a note by searching for it through the provided ID
        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new RuntimeException("Note not found")
        );
        auditLogService.logNoteDeletion(username, noteId);
        noteRepository.delete(note);
    }

    @Override
    public List<Note> getNotesForUser(String username) {
        //helps in retrieving all the notes created by a specific user.
        List<Note> personalNotes = noteRepository
                .findByOwnerUsername(username);
        return personalNotes;
    }
}

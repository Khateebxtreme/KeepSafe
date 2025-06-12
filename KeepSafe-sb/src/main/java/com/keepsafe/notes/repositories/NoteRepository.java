package com.keepsafe.notes.repositories;

import com.keepsafe.notes.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    //get list of all notes that is created by a specific user (helps in displaying all the notes on a user's dashboard)
    List<Note> findByOwnerUsername(String ownerUsername);
}

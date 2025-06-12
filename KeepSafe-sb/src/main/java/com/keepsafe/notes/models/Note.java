package com.keepsafe.notes.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //represents unique ID for each created note

    @Lob
    private String content;

    //(Large Object) is used to map large objects, such as binary data or large text, to database columns. It’s particularly useful for handling fields that contain large amounts of data, like images, documents, or large text fields. For large text data, we use String type otherwise Binary Type is used. Here, It represents the content inside our notes.

    private String ownerUsername;

    //username of the user that has created the note
}

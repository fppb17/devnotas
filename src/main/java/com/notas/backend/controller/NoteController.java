package com.notas.backend.controller;

import com.notas.backend.dto.CreateNoteRequest;
import com.notas.backend.dto.NoteResponse;
import com.notas.backend.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {

    private final NoteService noteService;

    // Inyección de dependencias por constructor (Buena práctica)
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // Endpoint para crear una nota
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Devuelve código 201
    public Mono<NoteResponse> createNote(@Valid @RequestBody CreateNoteRequest request) {
        return noteService.createNote(request);
    }

    // Endpoint para listar todas las notas
    @GetMapping
    public Flux<NoteResponse> getAllNotes() {
        return noteService.getAllNotes();
    }
}

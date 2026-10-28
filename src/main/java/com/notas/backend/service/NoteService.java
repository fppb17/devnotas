package com.notas.backend.service;

import com.notas.backend.dto.CreateNoteRequest;
import com.notas.backend.dto.NoteResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NoteService {

    // Simulación de base de datos en memoria (Thread-safe)
    private final Map<UUID, NoteResponse> mockDatabase = new ConcurrentHashMap<>();

    public Mono<NoteResponse> createNote(CreateNoteRequest request) {
        return Mono.fromCallable(() -> {
            // Creamos la respuesta simulando el guardado
            NoteResponse note = new NoteResponse(
                    UUID.randomUUID(),
                    request.title(),
                    request.body(),
                    "PUBLISHED",
                    LocalDateTime.now()
            );
            // "Guardamos" en nuestro mapa en memoria
            mockDatabase.put(note.id(), note);
            return note;
        });
    }

    public Flux<NoteResponse> getAllNotes() {
        // Devolvemos todos los elementos del mapa como un flujo reactivo
        return Flux.fromIterable(mockDatabase.values());
    }
}

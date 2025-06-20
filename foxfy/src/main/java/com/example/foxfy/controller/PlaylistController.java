package com.example.foxfy.controller;

import com.example.foxfy.dto.PlaylistDTO;
import com.example.foxfy.model.Playlist;
import com.example.foxfy.model.Usuario;
import com.example.foxfy.repository.PlaylistRepository;
import com.example.foxfy.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
@RequestMapping("/playlists")
public class PlaylistController {

    PlaylistRepository playlistRepository;
    UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Playlist> getAll() {
        return playlistRepository.findAll();
    }

    @GetMapping("/{id}")
    public Playlist getById(@PathVariable Long id) {
        return playlistRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Playlist save(@RequestBody PlaylistDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Playlist playlist = new Playlist();
        playlist.setNomePlaylist(dto.getNomePlaylist());
        playlist.setFoto(dto.getFoto());
        playlist.setUsuario(usuario);

        return playlistRepository.save(playlist);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Playlist> update(@PathVariable Long id, @RequestBody PlaylistDTO dto) {
        return playlistRepository.findById(id).map(existingPlaylist -> {
            existingPlaylist.setNomePlaylist(dto.getNomePlaylist());
            existingPlaylist.setFoto(dto.getFoto());

            if (dto.getUsuarioId() != null) {
                Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                        .orElse(null);
                if (usuario == null) {
                    return new ResponseEntity<Playlist>(HttpStatus.BAD_REQUEST);
                }
                existingPlaylist.setUsuario(usuario);
            }

            Playlist updatedPlaylist = playlistRepository.save(existingPlaylist);
            return ResponseEntity.ok(updatedPlaylist);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        playlistRepository.deleteById(id);
    }
}

package com.elotech.desafio.controller;

import com.elotech.desafio.dto.UsuarioRequestDTO;
import com.elotech.desafio.dto.UsuarioResponseDTO;
import com.elotech.desafio.entity.Usuario;
import com.elotech.desafio.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criaUsuario(@Valid @RequestBody UsuarioRequestDTO requestDTO){

           Usuario usuarioNovo = usuarioService.criarUsuario(requestDTO);

           return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioResponseDTO(usuarioNovo.getId(), usuarioNovo.getNome(),
                   usuarioNovo.getEmail(), usuarioNovo.getDataCadastro(), usuarioNovo.getTelefone()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscaUsuario(@PathVariable Long id){

            Usuario usuarioEncontrado = usuarioService.retornaUsuario(id);

            return ResponseEntity.ok(new UsuarioResponseDTO(usuarioEncontrado.getId(), usuarioEncontrado.getNome(),
                    usuarioEncontrado.getEmail(), usuarioEncontrado.getDataCadastro(), usuarioEncontrado.getTelefone()));

    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizaUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO requestDTO){


            Usuario usuarioAtt = usuarioService.editaUsuario(id, requestDTO);

            return ResponseEntity.ok(new UsuarioResponseDTO(usuarioAtt.getId(), usuarioAtt.getNome(),
                    usuarioAtt.getEmail(), usuarioAtt.getDataCadastro(), usuarioAtt.getTelefone()));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletaUsuario(@PathVariable Long id){

            usuarioService.deletaUsuario(id);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();


    }
}


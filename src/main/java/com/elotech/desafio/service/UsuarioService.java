package com.elotech.desafio.service;

import com.elotech.desafio.dto.UsuarioRequestDTO;
import com.elotech.desafio.entity.Usuario;
import com.elotech.desafio.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public Usuario criarUsuario(UsuarioRequestDTO requestDTO) {

        if(usuarioRepository.existsByEmail(requestDTO.email())){
            throw new RuntimeException("E-mail ja cadastrado no sistema");
        }

        Usuario usuarioNovo =  Usuario.builder().nome(requestDTO.nome()).email(requestDTO.email())
                .dataCadastro(LocalDate.now()).telefone(requestDTO.telefone()).build();;

        return usuarioRepository.save(usuarioNovo);

    }


    public Usuario retornaUsuario(Long id){

        return usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario não encontrado"));

    }

    public Page<Usuario> listaUsuarios(Pageable pageable){

        return usuarioRepository.findAll(pageable);
    }

    public Usuario editaUsuario(Long id, UsuarioRequestDTO requestDTO){

        Usuario usuarioPraEditar = retornaUsuario(id);

        if(!Objects.equals(requestDTO.email(), usuarioPraEditar.getEmail())){
            if(usuarioRepository.existsByEmail(requestDTO.email())){
                throw new RuntimeException("E-mail ja cadastrado no sistema");
            }

        }

        usuarioPraEditar.setEmail(requestDTO.email());
        usuarioPraEditar.setNome(requestDTO.nome());
        usuarioPraEditar.setTelefone(requestDTO.telefone());

        return usuarioRepository.save(usuarioPraEditar);
    }

    public void deletaUsuario(Long id){

        Usuario usuarioPraDeletar = retornaUsuario(id);

        usuarioRepository.delete(usuarioPraDeletar);
    }


}

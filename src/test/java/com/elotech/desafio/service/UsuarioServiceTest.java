package com.elotech.desafio.service;

import com.elotech.desafio.dto.UsuarioRequestDTO;
import com.elotech.desafio.entity.Usuario;
import com.elotech.desafio.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCriarUsuarioComSucesso() {

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("joao", "joao@outlook.com", "449912213");

        Usuario usuarioRetornoBD = Usuario.builder().id(1L).nome("joao").email("joao@outlook.com")
                .dataCadastro(LocalDate.now()).telefone("449912213").build();



        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioRetornoBD);

        Usuario usuarioNovo = usuarioService.criarUsuario(requestDTO);

        assertEquals(usuarioRetornoBD.getEmail(), usuarioNovo.getEmail());

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoAoCriarUsuarioComEmailDuplicado() {

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("joao", "joao@outlook.com", "449912213");


        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> { usuarioService.criarUsuario(requestDTO);});

        assertEquals("E-mail ja cadastrado no sistema", exception.getMessage());

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveRetornarUsuarioQuandoEncontrado() {

        Usuario usuarioRetornoBD = Usuario.builder().id(1L).nome("joao").email("joao@outlook.com")
                .dataCadastro(LocalDate.now()).telefone("449912213").build();

        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuarioRetornoBD));

        Usuario usuarioAlvo = usuarioService.retornaUsuario(usuarioRetornoBD.getId());

        assertEquals(usuarioRetornoBD.getEmail(), usuarioAlvo.getEmail());

        verify(usuarioRepository, times(1)).findById(anyLong());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {

        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> { usuarioService.retornaUsuario(1L);});

        assertEquals("Usuario não encontrado", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(anyLong());
    }


    @Test
    void deveEditarUsuarioMantendoEmailComSucesso() {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("joao", "joao@outlook.com", "123123123");
        Usuario usuarioRetornoBd = Usuario.builder().id(1L).nome("joao").email("joao@outlook.com")
                .dataCadastro(LocalDate.now()).telefone("449912213").build();

        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuarioRetornoBd));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioRetornoBd);


        Usuario usuarioEditado = usuarioService.editaUsuario(1L, requestDTO);

        assertEquals(requestDTO.telefone(), usuarioEditado.getTelefone());

        verify(usuarioRepository, never()).existsByEmail(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));

    }

    @Test
    void deveEditarUsuarioAlterandoEmailComSucesso() {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("joao", "joao123@outlook.com", "123123");

        Usuario usuarioRetornoBdAntigo = Usuario.builder().id(1L).nome("joao").email("joao@outlook.com")
                .dataCadastro(LocalDate.now()).telefone("449912213").build();
        Usuario usuarioRetornoBdAtual = Usuario.builder().id(1L).nome("joao").email("joao123@outlook.com")
                .dataCadastro(LocalDate.now()).telefone("123123").build();

        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuarioRetornoBdAntigo));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioRetornoBdAtual);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);

        Usuario usuarioEditado = usuarioService.editaUsuario(1L, requestDTO);

        assertEquals(requestDTO.email(), usuarioEditado.getEmail());


        verify(usuarioRepository, times(1)).existsByEmail(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcessaoAoEditarComEmailDeOutroUsuario() {

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("joao", "joaozik4@outlook.com", "123123123");
        Usuario usuarioRetornoBd = Usuario.builder().id(1L).nome("joao").email("joao@outlook.com")
                .dataCadastro(LocalDate.now()).telefone("449912213").build();

        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuarioRetornoBd));
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> { usuarioService.editaUsuario(1L, requestDTO);});

        assertEquals("E-mail ja cadastrado no sistema", exception.getMessage());

        verify(usuarioRepository, times(1)).existsByEmail(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));

    }

    @Test
    void deveDeletaUsuarioComSucesso() {

        Usuario usuarioRetornoBd = Usuario.builder().id(1L).nome("joao").email("joao@outlook.com")
                .dataCadastro(LocalDate.now()).telefone("449912213").build();

        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuarioRetornoBd));

        usuarioService.deletaUsuario(1L);

        verify(usuarioRepository, times(1)).delete(any(Usuario.class));

    }
}
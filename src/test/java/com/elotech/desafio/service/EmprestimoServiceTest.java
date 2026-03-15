package com.elotech.desafio.service;

import com.elotech.desafio.dto.EmprestimoAttRequestDTO;
import com.elotech.desafio.dto.EmprestimoRequestDTO;
import com.elotech.desafio.entity.Emprestimo;
import com.elotech.desafio.entity.Livro;
import com.elotech.desafio.entity.Usuario;
import com.elotech.desafio.repository.EmprestimoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private LivroService livroService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private EmprestimoService emprestimoService;

    @Test
    void deveCriarEmprestimoComSucesso(){

        Usuario usuarioMock = Usuario.builder().id(1L).nome("joao").email("joao@outlook.com")
                .dataCadastro(LocalDate.now()).telefone("449912213").build();

        Livro livroMock = Livro.builder().id(1L).titulo("O Senhor dos Anéis").autor("J.R.R. Tolkien")
                .isbn("9788533613379").dataPublicacao(LocalDate.of(1954,07,29)).categoria("Fantasia").build();

        EmprestimoRequestDTO requestDTO = new EmprestimoRequestDTO(1L, 1L);
        Emprestimo emprestimoBD = Emprestimo.builder().id(1L).usuario(usuarioMock).livro(livroMock)
                .dataEmprestimo(LocalDate.now())
                .dataDevolucao(LocalDate.now().plusDays(14))
                .status("EMPRESTADO").build();

        when(emprestimoRepository.existsByLivroIdAndStatus(anyLong(), anyString())).thenReturn(false);
        when(usuarioService.retornaUsuario(anyLong())).thenReturn(usuarioMock);
        when(livroService.retornaLivro(anyLong())).thenReturn(livroMock);
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimoBD);

        Emprestimo emprestimoNovo = emprestimoService.criaEmprestimo(requestDTO);

        assertEquals(emprestimoBD.getId(), emprestimoNovo.getId());

        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));
    }

    @Test
    void deveRetornarExcecaoQuandoLivroPossuiEmprestimoAtivo(){

        EmprestimoRequestDTO requestDTO = new EmprestimoRequestDTO(1L, 1L);

        when(emprestimoRepository.existsByLivroIdAndStatus(anyLong(), anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> emprestimoService.criaEmprestimo(requestDTO));

        assertEquals("O livro possui um empréstimo ativo", exception.getMessage());

        verify(emprestimoRepository, times(1)).existsByLivroIdAndStatus(anyLong(), anyString());
        verify(usuarioService, never()).retornaUsuario(anyLong());
        verify(livroService, never()).retornaLivro(anyLong());
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));

    }

    @Test
    void deveAtualizarEmprestimoComSucesso(){

        EmprestimoAttRequestDTO requestDTO = new EmprestimoAttRequestDTO(LocalDate.now().plusDays(14), "Emprestado");

        Usuario usuarioMock = Usuario.builder().id(1L).nome("joao").email("joao@outlook.com")
                .dataCadastro(LocalDate.now()).telefone("449912213").build();

        Livro livroMock = Livro.builder().id(1L).titulo("O Senhor dos Anéis").autor("J.R.R. Tolkien")
                .isbn("9788533613379").dataPublicacao(LocalDate.of(1954,07,29)).categoria("Fantasia").build();

        Emprestimo emprestimoBD = Emprestimo.builder().id(1L).usuario(usuarioMock).livro(livroMock)
                .dataEmprestimo(LocalDate.now())
                .dataDevolucao(LocalDate.now().plusDays(14))
                .status("EMPRESTADO").build();

        when(emprestimoRepository.findById(anyLong())).thenReturn(Optional.of(emprestimoBD));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimoBD);

        Emprestimo emprestimoPraAtualizar = emprestimoService.atualizaEmprestimo(1L, requestDTO);

        assertEquals(requestDTO.status(), emprestimoPraAtualizar.getStatus());
        assertEquals(requestDTO.dataDevolucao(), emprestimoPraAtualizar.getDataDevolucao());

        verify(emprestimoRepository, times(1)).findById(anyLong());
        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));

    }

    @Test
    void deveRetornarExcecaoQuandoEmprestimoNãoExiste(){

        EmprestimoAttRequestDTO requestDTO = new EmprestimoAttRequestDTO(LocalDate.now().plusDays(14), "Emprestado");

        when(emprestimoRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> emprestimoService.atualizaEmprestimo(1L, requestDTO));

        assertEquals("Empréstimo não encontrado", exception.getMessage());

        verify(emprestimoRepository, times(1)).findById(anyLong());
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));


    }
}
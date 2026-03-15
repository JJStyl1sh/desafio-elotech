package com.elotech.desafio.service;

import com.elotech.desafio.dto.EmprestimoAttRequestDTO;
import com.elotech.desafio.dto.EmprestimoRequestDTO;
import com.elotech.desafio.entity.Emprestimo;
import com.elotech.desafio.entity.Livro;
import com.elotech.desafio.entity.Usuario;
import com.elotech.desafio.repository.EmprestimoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;

    private final UsuarioService usuarioService;

    private final LivroService livroService;


    public Emprestimo criaEmprestimo(EmprestimoRequestDTO requestDTO){

        if(emprestimoRepository.existsByLivroIdAndStatus(requestDTO.livroId(), "EMPRESTADO")){
            throw new RuntimeException("O livro possui um empréstimo ativo");
        }

        Usuario usuarioEmprestimo = usuarioService.retornaUsuario(requestDTO.usuarioId());

        Livro livroEmprestimo = livroService.retornaLivro(requestDTO.livroId());

        Emprestimo emprestimoNovo = Emprestimo.builder().usuario(usuarioEmprestimo).livro(livroEmprestimo)
                .dataEmprestimo(LocalDate.now()).dataDevolucao(LocalDate.now().plusDays(14)).status("EMPRESTADO").build();

        return emprestimoRepository.save(emprestimoNovo);
    }

    public Emprestimo atualizaEmprestimo(Long id, EmprestimoAttRequestDTO requestDTO){


        Emprestimo emprestimoPraAtualizar = emprestimoRepository.findById(id).orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));


        emprestimoPraAtualizar.setStatus(requestDTO.status());
        emprestimoPraAtualizar.setDataDevolucao(requestDTO.dataDevolucao());

        return emprestimoRepository.save(emprestimoPraAtualizar);
    }

    public Page<Emprestimo> listaEmprestimos(Pageable pageable){
        return emprestimoRepository.findAll(pageable);
    }
}

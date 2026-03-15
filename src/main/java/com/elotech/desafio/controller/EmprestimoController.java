package com.elotech.desafio.controller;

import com.elotech.desafio.dto.EmprestimoAttRequestDTO;
import com.elotech.desafio.dto.EmprestimoRequestDTO;
import com.elotech.desafio.dto.EmprestimoResponseDTO;
import com.elotech.desafio.entity.Emprestimo;
import com.elotech.desafio.service.EmprestimoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> criaEmprestimo(@Valid @RequestBody EmprestimoRequestDTO requestDTO){

        Emprestimo emprestimo = emprestimoService.criaEmprestimo(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(new EmprestimoResponseDTO(emprestimo.getId(),  emprestimo.getUsuario().getId(),
                emprestimo.getLivro().getId(), emprestimo.getDataEmprestimo(), emprestimo.getDataDevolucao(), emprestimo.getStatus()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> atualizaEmprestimo(@PathVariable Long id, @Valid @RequestBody EmprestimoAttRequestDTO requestDTO){

        Emprestimo emprestimo = emprestimoService.atualizaEmprestimo(id, requestDTO);

        return ResponseEntity.ok(new EmprestimoResponseDTO(emprestimo.getId(),  emprestimo.getUsuario().getId(),
                emprestimo.getLivro().getId(), emprestimo.getDataEmprestimo(), emprestimo.getDataDevolucao(), emprestimo.getStatus()));
    }

    @GetMapping
    public ResponseEntity<Page<EmprestimoResponseDTO>> listaEmprestimos(@PageableDefault(size = 10, sort = "id")Pageable pageable){

        Page<Emprestimo> emprestimos = emprestimoService.listaEmprestimos(pageable);

        Page<EmprestimoResponseDTO> responseDTO = emprestimos.map(emprestimo -> new EmprestimoResponseDTO(emprestimo.getId(),  emprestimo.getUsuario().getId(),
                emprestimo.getLivro().getId(), emprestimo.getDataEmprestimo(), emprestimo.getDataDevolucao(), emprestimo.getStatus()));

        return ResponseEntity.ok(responseDTO);
    }
}

package com.elotech.desafio.dto;

import com.elotech.desafio.entity.Livro;
import com.elotech.desafio.entity.Usuario;

import java.time.LocalDate;

public record EmprestimoResponseDTO(Long id, Long userId, Long livroId, LocalDate dataEmprestimo, LocalDate dataDevolucao, String status) {
}

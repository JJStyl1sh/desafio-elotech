package com.elotech.desafio.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EmprestimoAttRequestDTO(
                                      LocalDate dataDevolucao,
                                      String status
                                      ) {
}

package com.elotech.desafio.dto;

import java.time.LocalDate;

public record LivroRequestDTO(String titulo, String autor, String isbn, LocalDate dataPublicacao, String categoria) {
}

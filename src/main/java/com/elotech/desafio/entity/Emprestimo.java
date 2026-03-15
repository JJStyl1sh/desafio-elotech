package com.elotech.desafio.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_emprestimos")
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O ID do usuário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;


    @NotNull(message = "O ID do livro é obrigatório")
    @ManyToOne
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @Column(name = "data_emprestimo", nullable = false)
    @NotNull(message = "Data do emprestimo é obrigatória")
    @PastOrPresent(message = "Data em que o empréstimo foi realizado não pode ser no futuro")
    private LocalDate dataEmprestimo;

    @Column(name = "data_devolucao", nullable = false)
    @NotNull(message = "Data da devolução é obrigatória")
    private LocalDate dataDevolucao;

    @Column(name = "status", nullable = false)
    @NotBlank(message = "Status é obrigatório")
    private String status;



}

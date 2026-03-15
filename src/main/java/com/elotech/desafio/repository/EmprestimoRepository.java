package com.elotech.desafio.repository;

import com.elotech.desafio.entity.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    boolean existsByLivroIdAndStatus(Long livroId, String status);

}

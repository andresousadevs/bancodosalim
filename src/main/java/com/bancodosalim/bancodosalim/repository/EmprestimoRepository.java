package com.bancodosalim.bancodosalim.repository;

import com.bancodosalim.bancodosalim.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
	List<Emprestimo> findByClienteCpf(String cpfCliente);

	List<Emprestimo> findByClienteId(Long clienteId);
}
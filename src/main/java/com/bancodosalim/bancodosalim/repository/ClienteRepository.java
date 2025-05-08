package com.bancodosalim.bancodosalim.repository;

import com.bancodosalim.bancodosalim.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
	Optional<Cliente> findByCpf(String cpf);

	Optional<Cliente> findByEmail(String email);

	boolean existsByCpf(String cpf);

	boolean existsByEmail(String email);
}
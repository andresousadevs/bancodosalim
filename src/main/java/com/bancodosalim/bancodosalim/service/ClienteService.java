package com.bancodosalim.bancodosalim.service;

import com.bancodosalim.bancodosalim.model.Cliente;
import com.bancodosalim.bancodosalim.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal; // Importar BigDecimal

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	// Método de salvar existente, ajustado para incluir senha (se necessário)
	// Certifique-se que o objeto Cliente vindo do controller já tem a senha
	public Cliente cadastrarCliente(Cliente cliente) throws Exception {
		if (clienteRepository.existsByCpf(cliente.getCpf())) {
			throw new Exception("CPF já cadastrado.");
		}
		if (clienteRepository.existsByEmail(cliente.getEmail())) {
			throw new Exception("Email já cadastrado.");
		}
		if (cliente.getSenha() == null || cliente.getSenha().isEmpty()) {
			throw new Exception("Senha é obrigatória."); // Tornando a senha obrigatória no cadastro
		}
		// ATENÇÃO: Aqui seria o local ideal para HASHING da senha antes de salvar
		// Ex: cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
		cliente.setSaldo(BigDecimal.ZERO); // Garante saldo inicial zero no cadastro
		return clienteRepository.save(cliente);
	}

	// Método para salvar atualizações (reutiliza o save do repository)
	public Cliente salvarAtualizacaoCliente(Cliente cliente) {
		return clienteRepository.save(cliente);
	}

	// Método para buscar cliente por CPF (usado no login e outras partes)
	public Optional<Cliente> buscarPorCpf(String cpf) {
		return clienteRepository.findByCpf(cpf);
	}

	// Método de autenticação SIMPLIFICADO (ATENÇÃO: SENHA EM TEXTO PLANO)
	public Optional<Cliente> autenticar(String cpf, String senha) {
		Optional<Cliente> clienteOpt = clienteRepository.findByCpf(cpf);
		if (clienteOpt.isPresent()) {
			Cliente cliente = clienteOpt.get();
			// Comparação direta da senha (INSEGURO!)
			if (senha != null && senha.equals(cliente.getSenha())) {
				return Optional.of(cliente);
			}
		}
		return Optional.empty(); // Retorna vazio se CPF não encontrado ou senha incorreta
	}

	public Optional<Cliente> buscarPorId(Long id) {
		return clienteRepository.findById(id);
	}

	public List<Cliente> listarTodos() {
		return clienteRepository.findAll();
	}
}
package com.bancodosalim.bancodosalim.model;

import jakarta.persistence.*;
import java.util.List;
import java.math.BigDecimal; // Usar BigDecimal para valores monetários é mais preciso
import java.math.RoundingMode;

@Entity
public class Cliente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nomeCompleto;

	@Column(unique = true, nullable = false)
	private String cpf;

	@Column(unique = true, nullable = false)
	private String email;

	private String telefone;
	// Não armazene renda se não for essencial, pode ser sensível
	// private Double rendaMensal;

	// --- NOVOS CAMPOS ---
	@Column(nullable = false)
	private String senha; // ATENÇÃO: Texto plano apenas para este exercício!

	@Column(precision = 19, scale = 2) // Precisão para dinheiro
	private BigDecimal saldo;
	// --- FIM NOVOS CAMPOS ---

	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Emprestimo> emprestimos;

	// Construtores
	public Cliente() {
		this.saldo = BigDecimal.ZERO.setScale(2); // Inicializa saldo com 0.00
	}

	public Cliente(String nomeCompleto, String cpf, String email, String telefone, String senha) {
		this(); // Chama o construtor padrão para inicializar o saldo
		this.nomeCompleto = nomeCompleto;
		this.cpf = cpf;
		this.email = email;
		this.telefone = telefone;
		this.senha = senha; // ATENÇÃO: Texto plano
		// Removido rendaMensal do construtor
	}

	// --- Getters e Setters (incluir para senha e saldo) ---

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeCompleto() {
		return nomeCompleto;
	}

	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	// --- NOVOS GETTERS/SETTERS ---
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	} // Adicione validação/hash aqui em um projeto real

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = (saldo == null) ? BigDecimal.ZERO.setScale(2) : saldo.setScale(2, RoundingMode.HALF_UP);
	}
	// --- FIM NOVOS GETTERS/SETTERS ---

	public List<Emprestimo> getEmprestimos() {
		return emprestimos;
	}

	public void setEmprestimos(List<Emprestimo> emprestimos) {
		this.emprestimos = emprestimos;
	}

	// Métodos utilitários para saldo (boa prática)
	public void depositar(BigDecimal valor) {
		if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) {
			this.saldo = this.saldo.add(valor).setScale(2, RoundingMode.HALF_UP);
		}
	}

	public boolean sacar(BigDecimal valor) {
		if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0 && this.saldo.compareTo(valor) >= 0) {
			this.saldo = this.saldo.subtract(valor).setScale(2, RoundingMode.HALF_UP);
			return true; // Saque bem-sucedido
		}
		return false; // Saldo insuficiente ou valor inválido
	}

	public int getRendaMensal() {
		// TODO Auto-generated method stub
		return 0;
	}
}
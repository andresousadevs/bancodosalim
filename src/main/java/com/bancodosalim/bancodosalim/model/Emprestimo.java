package com.bancodosalim.bancodosalim.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Emprestimo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "cliente_id", nullable = false)
	private Cliente cliente;

	private Double valorSolicitado;
	private Integer numeroParcelas;
	private Double taxaJurosAoMes;
	private Double valorParcela;
	private Double valorTotalPagar;
	private LocalDate dataSolicitacao;
	private String status;

	public Emprestimo() {
		this.dataSolicitacao = LocalDate.now();
		this.status = "SOLICITADO";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Double getValorSolicitado() {
		return valorSolicitado;
	}

	public void setValorSolicitado(Double valorSolicitado) {
		this.valorSolicitado = valorSolicitado;
	}

	public Integer getNumeroParcelas() {
		return numeroParcelas;
	}

	public void setNumeroParcelas(Integer numeroParcelas) {
		this.numeroParcelas = numeroParcelas;
	}

	public Double getTaxaJurosAoMes() {
		return taxaJurosAoMes;
	}

	public void setTaxaJurosAoMes(Double taxaJurosAoMes) {
		this.taxaJurosAoMes = taxaJurosAoMes;
	}

	public Double getValorParcela() {
		return valorParcela;
	}

	public void setValorParcela(Double valorParcela) {
		this.valorParcela = valorParcela;
	}

	public Double getValorTotalPagar() {
		return valorTotalPagar;
	}

	public void setValorTotalPagar(Double valorTotalPagar) {
		this.valorTotalPagar = valorTotalPagar;
	}

	public LocalDate getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(LocalDate dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
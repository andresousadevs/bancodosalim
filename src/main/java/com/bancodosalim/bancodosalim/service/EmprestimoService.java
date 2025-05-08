package com.bancodosalim.bancodosalim.service;

import com.bancodosalim.bancodosalim.model.Cliente;
import com.bancodosalim.bancodosalim.model.Emprestimo;
import com.bancodosalim.bancodosalim.repository.EmprestimoRepository;
import com.bancodosalim.bancodosalim.repository.ClienteRepository; // Import ClienteRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmprestimoService {

	@Autowired
	private EmprestimoRepository emprestimoRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	public Emprestimo solicitarEmprestimo(String cpfCliente, Double valorSolicitado, Integer numeroParcelas)
			throws Exception {
		Optional<Cliente> clienteOpt = clienteRepository.findByCpf(cpfCliente);
		if (!clienteOpt.isPresent()) {
			throw new Exception("Cliente com CPF " + cpfCliente + " não encontrado.");
		}
		Cliente cliente = clienteOpt.get();

		// REGRA DE NEGÓCIO (Exemplo simples - adaptar conforme seu README.md)
		if (valorSolicitado > cliente.getRendaMensal() * 5) {
			throw new Exception("Valor solicitado excede o limite para a renda do cliente.");
		}
		if (numeroParcelas > 36) {
			throw new Exception("Número máximo de parcelas é 36.");
		}

		Emprestimo emprestimo = new Emprestimo();
		emprestimo.setCliente(cliente);
		emprestimo.setValorSolicitado(valorSolicitado);
		emprestimo.setNumeroParcelas(numeroParcelas);
		emprestimo.setTaxaJurosAoMes(0.02); // Exemplo: 2% ao mês

		// Cálculo simples de juros composites (apenas um exemplo, use a fórmula
		// correta)
		// M = C * (1+i)^n
		double valorTotal = valorSolicitado * Math.pow((1 + emprestimo.getTaxaJurosAoMes()), numeroParcelas);
		emprestimo.setValorTotalPagar(arredondar(valorTotal, 2));
		emprestimo.setValorParcela(arredondar(valorTotal / numeroParcelas, 2));
		emprestimo.setStatus("AGUARDANDO_APROVACAO"); // Ou já "APROVADO" se a regra for simples

		return emprestimoRepository.save(emprestimo);
	}

	public List<Emprestimo> listarPorCpfCliente(String cpf) {
		return emprestimoRepository.findByClienteCpf(cpf);
	}

	public List<Emprestimo> listarTodos() {
		return emprestimoRepository.findAll();
	}

	private static double arredondar(double valor, int casasDecimais) {
		double fator = Math.pow(10, casasDecimais);
		return Math.round(valor * fator) / fator;
	}

	// Outros métodos: aprovarEmprestimo, rejeitarEmprestimo, etc.
}
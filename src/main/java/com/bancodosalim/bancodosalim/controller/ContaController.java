package com.bancodosalim.bancodosalim.controller;

import com.bancodosalim.bancodosalim.model.Cliente;
import com.bancodosalim.bancodosalim.service.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
public class ContaController {

	@Autowired
	private ClienteService clienteService;

	@GetMapping("/minha-conta")
	public String exibirMinhaConta(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		HttpSession session = request.getSession(false); // Não cria sessão se não existir

		if (session == null || session.getAttribute("clienteLogado") == null) {
			redirectAttributes.addFlashAttribute("loginError", "Sessão expirada ou inválida. Faça login novamente.");
			return "redirect:/"; // Volta para home/login se não está logado
		}

		Cliente clienteDaSessao = (Cliente) session.getAttribute("clienteLogado");

		// Boa prática: Recarregar dados do banco para garantir que estão atualizados
		Optional<Cliente> clienteAtualizadoOpt = clienteService.buscarPorId(clienteDaSessao.getId());

		if (clienteAtualizadoOpt.isPresent()) {
			model.addAttribute("cliente", clienteAtualizadoOpt.get());
			return "minha-conta"; // Nome do arquivo HTML
		} else {
			// Cliente não encontrado no banco (improvável se a sessão é válida, mas trata
			// erro)
			session.invalidate(); // Limpa a sessão inválida
			redirectAttributes.addFlashAttribute("loginError",
					"Erro ao carregar dados da conta. Faça login novamente.");
			return "redirect:/";
		}
	}

	@PostMapping("/depositar")
	public String processarDeposito(@RequestParam BigDecimal valor, // Spring pode converter String para BigDecimal
			HttpServletRequest request, RedirectAttributes redirectAttributes) {

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("clienteLogado") == null) {
			redirectAttributes.addFlashAttribute("loginError", "Sessão inválida para depósito.");
			return "redirect:/";
		}

		if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
			redirectAttributes.addFlashAttribute("operacaoErro", "Valor de depósito inválido.");
			return "redirect:/minha-conta";
		}

		Cliente clienteDaSessao = (Cliente) session.getAttribute("clienteLogado");
		// Recarregar cliente do banco para operar sobre o saldo atualizado
		Optional<Cliente> clienteAtualizadoOpt = clienteService.buscarPorId(clienteDaSessao.getId());

		if (clienteAtualizadoOpt.isPresent()) {
			Cliente cliente = clienteAtualizadoOpt.get();
			cliente.depositar(valor);
			clienteService.salvarAtualizacaoCliente(cliente); // Salva a atualização
			session.setAttribute("clienteLogado", cliente); // Atualiza o cliente na sessão também
			redirectAttributes.addFlashAttribute("operacaoSucesso",
					"Depósito de R$ " + valor.setScale(2) + " realizado com sucesso!");
		} else {
			redirectAttributes.addFlashAttribute("operacaoErro", "Erro ao processar depósito: Conta não encontrada.");
			session.invalidate();
			return "redirect:/";
		}

		return "redirect:/minha-conta";
	}

	@PostMapping("/sacar")
	public String processarSaque(@RequestParam BigDecimal valor, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("clienteLogado") == null) {
			redirectAttributes.addFlashAttribute("loginError", "Sessão inválida para saque.");
			return "redirect:/";
		}

		if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
			redirectAttributes.addFlashAttribute("operacaoErro", "Valor de saque inválido.");
			return "redirect:/minha-conta";
		}

		Cliente clienteDaSessao = (Cliente) session.getAttribute("clienteLogado");
		// Recarregar cliente do banco
		Optional<Cliente> clienteAtualizadoOpt = clienteService.buscarPorId(clienteDaSessao.getId());

		if (clienteAtualizadoOpt.isPresent()) {
			Cliente cliente = clienteAtualizadoOpt.get();
			boolean sucesso = cliente.sacar(valor); // Tenta sacar usando o método do modelo

			if (sucesso) {
				clienteService.salvarAtualizacaoCliente(cliente); // Salva se o saque foi bem-sucedido
				session.setAttribute("clienteLogado", cliente); // Atualiza a sessão
				redirectAttributes.addFlashAttribute("operacaoSucesso",
						"Saque de R$ " + valor.setScale(2) + " realizado com sucesso!");
			} else {
				redirectAttributes.addFlashAttribute("operacaoErro",
						"Saldo insuficiente para saque de R$ " + valor.setScale(2) + ".");
			}
		} else {
			redirectAttributes.addFlashAttribute("operacaoErro", "Erro ao processar saque: Conta não encontrada.");
			session.invalidate();
			return "redirect:/";
		}

		return "redirect:/minha-conta";
	}
}
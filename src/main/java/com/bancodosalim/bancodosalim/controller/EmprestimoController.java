package com.bancodosalim.bancodosalim.controller;

import com.bancodosalim.bancodosalim.model.Emprestimo;
import com.bancodosalim.bancodosalim.service.ClienteService;
import com.bancodosalim.bancodosalim.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class EmprestimoController {

	@Autowired
	private EmprestimoService emprestimoService;

	@Autowired
	private ClienteService clienteService; // Pode ser útil para buscar dados do cliente

	@GetMapping("/emprestimos")
	public String paginaEmprestimos(Model model, @RequestParam(required = false) String cpfBusca) {
		// Objeto para o formulário de solicitação
		// Você pode passar um objeto EmprestimoDTO aqui se quiser campos separados para
		// cpfCliente e demais
		model.addAttribute("solicitacaoEmprestimo", new Emprestimo()); // Simples para agora
		List<Emprestimo> emprestimosListados = new ArrayList<>();

		if (cpfBusca != null && !cpfBusca.isEmpty()) {
			try {
				emprestimosListados = emprestimoService.listarPorCpfCliente(cpfBusca);
				if (emprestimosListados.isEmpty()) {
					model.addAttribute("infoMessage", "Nenhum empréstimo encontrado para o CPF: " + cpfBusca);
				}
			} catch (Exception e) {
				model.addAttribute("errorMessageList", "Erro ao buscar empréstimos: " + e.getMessage());
			}
		}
		model.addAttribute("emprestimosListados", emprestimosListados);
		model.addAttribute("cpfBuscaAtual", cpfBusca);
		return "emprestimos"; // Nome do HTML: emprestimos.html
	}

	@PostMapping("/emprestimos/solicitar")
	public String solicitarEmprestimo(@RequestParam String cpfCliente, // Pego diretamente dos campos
			@RequestParam Double valorSolicitado, @RequestParam Integer numeroParcelas,
			RedirectAttributes redirectAttributes, Model model) {
		try {
			emprestimoService.solicitarEmprestimo(cpfCliente, valorSolicitado, numeroParcelas);
			redirectAttributes.addFlashAttribute("successMessage",
					"Empréstimo solicitado com sucesso! Aguardando análise.");
			return "redirect:/emprestimos?cpfBusca=" + cpfCliente; // Redireciona para ver a lista atualizada
		} catch (Exception e) {
			// Para manter o formulário preenchido em caso de erro, o ideal seria usar
			// @ModelAttribute com um DTO
			// Mas para simplificar, vamos apenas mostrar o erro e redirecionar
			redirectAttributes.addFlashAttribute("errorMessage", "Erro ao solicitar empréstimo: " + e.getMessage());
			return "redirect:/emprestimos";
		}
	}
}
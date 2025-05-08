package com.bancodosalim.bancodosalim.controller;

import com.bancodosalim.bancodosalim.model.Cliente;
import com.bancodosalim.bancodosalim.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	@GetMapping("/seja-cliente")
	public String formNovoCliente(Model model) {
		model.addAttribute("cliente", new Cliente()); // Objeto para o form th:object
		return "seja-cliente"; // Nome do HTML: seja-cliente.html
	}

	@PostMapping("/clientes/salvar")
	public String salvarCliente(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes, Model model) {
		try {
			clienteService.cadastrarCliente(cliente);
			redirectAttributes.addFlashAttribute("successMessage", "Cliente cadastrado com sucesso!");
			return "redirect:/"; // Redireciona para a home ou página de sucesso
		} catch (Exception e) {
			model.addAttribute("cliente", cliente); // Devolve o objeto para preencher o form novamente
			model.addAttribute("errorMessage", "Erro ao cadastrar cliente: " + e.getMessage());
			return "seja-cliente"; // Volta para a página de cadastro com a mensagem de erro
		}
	}

}
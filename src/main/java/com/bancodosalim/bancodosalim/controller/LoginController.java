package com.bancodosalim.bancodosalim.controller;

import com.bancodosalim.bancodosalim.model.Cliente;
import com.bancodosalim.bancodosalim.service.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping; // Import GetMapping

import java.util.Optional;

@Controller
public class LoginController {

	@Autowired
	private ClienteService clienteService;

	@PostMapping("/login")
	public String processarLogin(@RequestParam String cpf, @RequestParam String senha, HttpServletRequest request, // Para
																													// obter
																													// a
																													// sessão
			RedirectAttributes redirectAttributes) {

		Optional<Cliente> clienteAutenticado = clienteService.autenticar(cpf, senha);

		if (clienteAutenticado.isPresent()) {
			// Login bem-sucedido!
			HttpSession session = request.getSession(); // Obtém ou cria a sessão
			session.setAttribute("clienteLogado", clienteAutenticado.get()); // Guarda o cliente na sessão
			session.setMaxInactiveInterval(30 * 60); // Define timeout da sessão (ex: 30 minutos)
			return "redirect:/minha-conta"; // Redireciona para a página da conta
		} else {
			// Login falhou
			redirectAttributes.addFlashAttribute("loginError", "CPF ou senha inválidos.");
			return "redirect:/"; // Redireciona de volta para a home com a mensagem de erro
		}
	}

	@GetMapping("/logout")
	public String processarLogout(HttpServletRequest request) {
		HttpSession session = request.getSession(false); // Obtém a sessão SEM criar uma nova
		if (session != null) {
			session.invalidate(); // Invalida a sessão
		}
		return "redirect:/"; // Redireciona para a home
	}
}
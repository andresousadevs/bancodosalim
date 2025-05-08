package com.bancodosalim.bancodosalim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartaoController {

	// Mapeamento para exibir a página do cartão
	@GetMapping("/cartao")
	public String exibirPaginaCartao() {
		return "cartao"; // Retorna o nome do arquivo HTML (cartao.html)
	}

	// Mapeamento para receber a solicitação do formulário
	@PostMapping("/cartoes/solicitar")
	public String solicitarCartao(@RequestParam String cpfCartao, @RequestParam(required = false) String nomeCartao, // Tornando
																														// opcionais
																														// para
																														// simplificar
			@RequestParam(required = false) String emailCartao, RedirectAttributes redirectAttributes) {

		System.out.println("Recebida solicitação de cartão para CPF: " + cpfCartao); // Log simples no console

		if (cpfCartao == null || cpfCartao.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "CPF é obrigatório para solicitar o cartão.");
			return "redirect:/cartao";
		}
		// Adicione outras validações se desejar (formato CPF, etc.)

		// Define a mensagem de sucesso para ser exibida na página após o
		// redirecionamento
		redirectAttributes.addFlashAttribute("successMessage",
				"Sua solicitação para o Cartão Banco do Salim foi recebida! Analisaremos seus dados e entraremos em contato em breve.");

		// Redireciona de volta para a página do cartão (evita reenvio do formulário ao
		// recarregar)
		return "redirect:/cartao";
	}
}
// Garante que o código só execute quando o DOM estiver pronto.
$(document).ready(function() {
    console.log("jQuery e script.js carregados! DOM pronto.");

    // --- Constantes Globais ---
    const CPF_REGEX = /^\d{3}\.\d{3}\.\d{3}-\d{2}$/; // Formato com pontos e traço
    const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    // --- Funções Auxiliares ---

    // Função para mostrar erro perto de um campo
    function mostrarErro(inputElement, mensagem) {
        // Procura por spans de erro com classe 'error-message' ou 'js-error-*' após o input
        let $errorSpan = $(inputElement).nextAll('.error-message, [class^="js-error-"]').first();
        if ($errorSpan.length === 0) { // Se não existir, cria um
            $errorSpan = $('<span class="error-message" style="color: red; font-size: 0.8em; display: block;"></span>');
            $(inputElement).after($errorSpan);
        }
        $errorSpan.text(mensagem);
    }

    // Função para limpar mensagens de erro de um formulário
    function limparErros(formElement) {
        $(formElement).find('.error-message, [class^="js-error-"]').text('');
    }

    // Função de validação de CPF (Formato e Dígitos Verificadores)
    function validarCPF(cpf) {
         // Primeiro, valida o formato visual esperado XXX.XXX.XXX-XX
        // if (!CPF_REGEX.test(cpf)) {
        //     console.log("Formato visual do CPF inválido:", cpf); // Debug
        //    return false;
        // }
         // A máscara já garante ou tenta garantir o formato visual. A validação lógica focará nos dígitos.

        cpf = cpf.replace(/[^\d]+/g, ''); // Remove formatação para validação lógica
        if (cpf === '' || cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) {
            return false;
        }
        let soma = 0, resto;
        for (let i = 1; i <= 9; i++) soma += parseInt(cpf.substring(i - 1, i)) * (11 - i);
        resto = (soma * 10) % 11;
        if ((resto === 10) || (resto === 11)) resto = 0;
        if (resto !== parseInt(cpf.substring(9, 10))) return false;
        soma = 0;
        for (let i = 1; i <= 10; i++) soma += parseInt(cpf.substring(i - 1, i)) * (12 - i);
        resto = (soma * 10) % 11;
        if ((resto === 10) || (resto === 11)) resto = 0;
        if (resto !== parseInt(cpf.substring(10, 11))) return false;
        return true;
    }

    // Função simples para máscara de CPF
    function aplicarMascaraCpf(selector) {
        $(selector).on('input', function (e) {
            let value = e.target.value.replace(/\D/g, ''); // Remove não-dígitos
            value = value.substring(0, 11); // Limita a 11 dígitos
            let maskedValue = '';
            maskedValue = value.replace(/(\d{3})(\d)/, '$1.$2');    // 123.4
            maskedValue = maskedValue.replace(/(\d{3})(\d)/, '$1.$2'); // 123.456.7
            maskedValue = maskedValue.replace(/(\d{3})(\d{1,2})$/, '$1-$2'); // 123.456.789-10
            e.target.value = maskedValue;
        });
         // Valida ao perder o foco também, além do submit
        $(selector).on('blur', function(e){
            const $input = $(this);
            const cpfVal = $input.val();
             limparErros($input.closest('form')); // Limpa erros do form para revalidar este campo
            if(cpfVal && !validarCPF(cpfVal)){
                 mostrarErro(this, 'CPF inválido.');
            }
        });
    }


    // --- VALIDAÇÃO DOS FORMULÁRIOS ---

    // 1. Validação Cadastro de Cliente (seja-cliente.html)
    const $formCliente = $('#seja-cliente-form form');
    if ($formCliente.length) {
        aplicarMascaraCpf('#cpf'); // Aplica máscara no campo CPF

        $formCliente.on('submit', function(event) {
            let isValid = true;
            limparErros(this);

            // Valida Nome Completo
            const $nomeCompleto = $('#nomeCompleto');
            if (!$nomeCompleto.val().trim()) {
                mostrarErro($nomeCompleto, "Nome completo é obrigatório."); isValid = false;
            } else if ($nomeCompleto.val().trim().length < 3) {
                mostrarErro($nomeCompleto, "Nome deve ter pelo menos 3 caracteres."); isValid = false;
            }

            // Valida CPF (Já validado pelo blur, mas checa aqui de novo)
            const $cpf = $('#cpf');
            if (!$cpf.val().trim() || !validarCPF($cpf.val())) { // Valida preenchimento e lógica
                mostrarErro($cpf, $cpf.val() ? 'CPF inválido.' : 'CPF é obrigatório.');
                isValid = false;
            }

            // Valida Email
            const $email = $('#email');
            if (!$email.val().trim()) {
                mostrarErro($email, "Email é obrigatório."); isValid = false;
            } else if (!EMAIL_REGEX.test($email.val().trim())) {
                mostrarErro($email, "Formato de email inválido."); isValid = false;
            }

             // Valida Renda Mensal (Opcional, mas evita negativo)
            const $rendaMensal = $('#rendaMensal');
            if ($rendaMensal.length && $rendaMensal.val() && parseFloat($rendaMensal.val()) < 0) {
                mostrarErro($rendaMensal, "Renda mensal não pode ser negativa."); isValid = false;
            }

            // ** Validação de Senha e Confirmação **
            const $senha = $('#senha');
            const $confirmacaoSenha = $('#confirmacaoSenha');
            const $errorSenha = $(this).find('.js-error-senha'); // Usa span específico
            const $errorConfirmacao = $(this).find('.js-error-confirmacao'); // Usa span específico

            if (!$senha.val()) {
                $errorSenha.text("Senha é obrigatória."); isValid = false;
            } else if ($senha.val().length < 6) { // Mínimo 6 caracteres
                $errorSenha.text("Senha deve ter pelo menos 6 caracteres."); isValid = false;
            }

            if (!$confirmacaoSenha.val()) {
                $errorConfirmacao.text("Confirmação de senha é obrigatória."); isValid = false;
            } else if ($senha.val() && $senha.val() !== $confirmacaoSenha.val()) { // Só compara se a senha foi digitada
                $errorConfirmacao.text("As senhas não coincidem.");
                $errorSenha.text("As senhas não coincidem."); // Erro em ambos por clareza
                isValid = false;
            }

            // Ação Final do Submit (Cadastro)
            if (!isValid) {
                event.preventDefault();
                alert("Por favor, corrija os erros no formulário.");
                // Tenta focar no primeiro campo com erro visível
                const $firstErrorField = $(this).find('input:visible, select:visible').filter(function() {
                    return $(this).nextAll('.error-message, [class^="js-error-"]').text() !== '';
                }).first();
                if($firstErrorField.length) { $firstErrorField.focus(); }
            } else {
                console.log("Formulário de cliente (jQuery) válido, enviando...");
            }
        });
    }

    // 2. Validação Solicitação de Empréstimo (emprestimos.html)
    const $formEmprestimo = $('#solicitacao-emprestimo form');
    if ($formEmprestimo.length) {
        aplicarMascaraCpf('#cpfCliente'); // Aplica máscara no campo CPF Cliente

        $formEmprestimo.on('submit', function(event) {
            let isValid = true;
            limparErros(this);

            // CPF do Cliente
            const $cpfCliente = $('#cpfCliente');
            if (!$cpfCliente.val().trim() || !validarCPF($cpfCliente.val())) {
                 mostrarErro($cpfCliente, $cpfCliente.val() ? 'CPF do cliente inválido.' : 'CPF do cliente é obrigatório.');
                 isValid = false;
            }

            // Valor Solicitado
            const $valorSolicitado = $('#valorSolicitado');
            if (!$valorSolicitado.val() || parseFloat($valorSolicitado.val()) <= 0) {
                mostrarErro($valorSolicitado, "Valor solicitado deve ser um número maior que zero.");
                isValid = false;
            }

            // Número de Parcelas
            const $numeroParcelas = $('#numeroParcelas');
            if (!$numeroParcelas.val() || parseInt($numeroParcelas.val()) <= 0) {
                mostrarErro($numeroParcelas, "Número de parcelas deve ser um inteiro maior que zero.");
                isValid = false;
            }

            // Ação Final do Submit (Empréstimo)
            if (!isValid) {
                event.preventDefault();
                alert("Por favor, corrija os erros na solicitação de empréstimo.");
                const $firstErrorField = $(this).find('input:visible').filter(function() { return $(this).nextAll('.error-message').text() !== ''; }).first();
                 if($firstErrorField.length) { $firstErrorField.focus(); }
            } else {
                console.log("Formulário de empréstimo (jQuery) válido, enviando...");
            }
        });
    }

    // 3. Validação Busca de Empréstimos (emprestimos.html)
    const $formBuscaEmprestimos = $('#meus-emprestimos form');
    if ($formBuscaEmprestimos.length && $formBuscaEmprestimos.attr('method').toLowerCase() === 'get') {
        aplicarMascaraCpf('#cpfBusca'); // Aplica máscara

        $formBuscaEmprestimos.on('submit', function(event){
            let isValid = true;
            limparErros(this);
            const $cpfBusca = $('#cpfBusca');
            if (!$cpfBusca.val() || !validarCPF($cpfBusca.val())) {
                 mostrarErro($cpfBusca, $cpfBusca.val() ? 'CPF inválido.' : 'Informe o CPF para busca.');
                 isValid = false;
                 event.preventDefault();
            }
            if (isValid) {
                console.log("Iniciando busca de empréstimos...");
            }
        });
    }

    // 4. Validação Solicitação de Cartão (cartao.html)
    const $formCartao = $('#solicita-cartao form');
    if ($formCartao.length) {
        aplicarMascaraCpf('#cpfCartao'); // Aplica máscara

        $formCartao.on('submit', function(event) {
            let isValid = true;
            limparErros(this);

            // Validar CPF (Cartão)
            const $cpfCartao = $('#cpfCartao');
            if (!$cpfCartao.val() || !validarCPF($cpfCartao.val())) {
                 mostrarErro($cpfCartao, $cpfCartao.val() ? 'CPF inválido.' : 'CPF é obrigatório.');
                 isValid = false;
            }

             // Validar Nome (Opcional mas recomendado)
            const $nomeCartao = $('#nomeCartao');
            if ($nomeCartao.length && !$nomeCartao.val().trim()) {
                mostrarErro($nomeCartao, "Nome é obrigatório.");
                isValid = false;
            }

            // Validar Email (Opcional mas recomendado)
            const $emailCartao = $('#emailCartao');
            if ($emailCartao.length && !$emailCartao.val().trim()) {
                 mostrarErro($emailCartao, "Email é obrigatório.");
                 isValid = false;
            } else if ($emailCartao.length && $emailCartao.val().trim() && !EMAIL_REGEX.test($emailCartao.val().trim())) {
                mostrarErro($emailCartao, "Formato de e-mail inválido.");
                isValid = false;
            }

            // Ação Final (Cartão)
            if (!isValid) {
                event.preventDefault();
                alert("Por favor, verifique os campos e tente novamente.");
                 const $firstErrorField = $(this).find('input:visible').filter(function() { return $(this).nextAll('.error-message').text() !== ''; }).first();
                if($firstErrorField.length) { $firstErrorField.focus(); }
            } else {
                console.log("Formulário de cartão (jQuery) válido, enviando...");
            }
        });
    }

    // 5. Validação Login (index.html)
    const $formLogin = $('#login-section form');
    if ($formLogin.length) {
         aplicarMascaraCpf('#cpfLogin');

         $formLogin.on('submit', function(event){
            let isValid = true;
            limparErros(this);

            const $cpfLogin = $('#cpfLogin');
             if (!$cpfLogin.val() || !validarCPF($cpfLogin.val())) {
                 mostrarErro($cpfLogin, $cpfLogin.val() ? 'CPF inválido.' : 'CPF é obrigatório.');
                 isValid = false;
             }

            const $senhaLogin = $('#senhaLogin');
            if (!$senhaLogin.val()){
                 mostrarErro($senhaLogin, "Senha é obrigatória.");
                 isValid = false;
            }

             if (!isValid) {
                 event.preventDefault();
                 const $firstErrorField = $(this).find('input:visible').filter(function() { return $(this).nextAll('.error-message').text() !== ''; }).first();
                if($firstErrorField.length) { $firstErrorField.focus(); }
             }
         });
    }

    // 6. Validação Depósito (minha-conta.html)
    const $formDeposito = $('#formDeposito');
    if($formDeposito.length){
        $formDeposito.on('submit', function(event){
            let isValid = true;
            limparErros(this);
            const $valorDeposito = $('#valorDeposito');
            const valor = parseFloat($valorDeposito.val());

            if(!$valorDeposito.val() || isNaN(valor) || valor <= 0){
                mostrarErro($valorDeposito, "Informe um valor positivo para depositar.");
                isValid = false;
            }

             if (!isValid) {
                 event.preventDefault();
                 $valorDeposito.focus();
             } else {
                  console.log("Formulário de depósito (jQuery) válido, enviando...");
             }
        });
    }

     // 7. Validação Saque (minha-conta.html)
    const $formSaque = $('#formSaque');
    if($formSaque.length){
        $formSaque.on('submit', function(event){
            let isValid = true;
            limparErros(this);
            const $valorSaque = $('#valorSaque');
            const valor = parseFloat($valorSaque.val());

             if(!$valorSaque.val() || isNaN(valor) || valor <= 0){
                 mostrarErro($valorSaque, "Informe um valor positivo para sacar.");
                 isValid = false;
             }

             // Não validar saldo aqui

             if (!isValid) {
                 event.preventDefault();
                  $valorSaque.focus();
             } else {
                  console.log("Formulário de saque (jQuery) válido, enviando...");
             }
        });
    }


}); // Fim do $(document).ready()
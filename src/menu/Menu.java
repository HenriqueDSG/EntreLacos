package menu;

import exception.ExcecaoNegocio;
import model.Usuario;
import model.interesse.CatalogoInteresses;
import service.MatchService;
import service.MensagemService;
import service.PersistenciaJsonService;
import service.UsuarioService;
import util.EntradaUtil;
import model.plano.Plano;
import model.plano.PlanoConexao;
import model.plano.PlanoEssencial;
import java.util.List;
import model.Match;
import model.Mensagem;
import model.interesse.Interesse;

public class Menu {
    protected UsuarioService usuarioService;
    protected MatchService matchService;
    protected MensagemService mensagemService;
    protected Usuario usuarioSelecionado;
    protected CatalogoInteresses catalogoInteresses;
    protected PersistenciaJsonService persistenciaJsonService;

    public Menu() {
        usuarioService = new UsuarioService();
        matchService = new MatchService();
        mensagemService = new MensagemService(matchService);
        catalogoInteresses = new CatalogoInteresses();
        persistenciaJsonService = new PersistenciaJsonService();
        carregarDados();
    }

    public void iniciar() {
        int opcao;

        do {
            EntradaUtil.limparTela();
            exibirMenu();
            opcao = EntradaUtil.lerInteiro("Escolha: ");
            EntradaUtil.limparTela();
            processarOpcao(opcao);

            if (opcao != 0) {
                EntradaUtil.aguardarEnter();
            }
        }
        while (opcao != 0);
    }

    protected void exibirMenu() {
        System.out.println();
        System.out.println("================================");
        System.out.println("          ENTRELAÇOS");
        System.out.println("================================");

        if (usuarioSelecionado != null) {
            System.out.println(String.format("Usuário selecionado: %s", usuarioSelecionado.getNome()));
        } 
        else {
            System.out.println("Nenhum usuário selecionado.");
        }

        System.out.println();
        System.out.println("1 - Cadastrar usuário");
        System.out.println("2 - Selecionar usuário");
        System.out.println("3 - Listar perfis");
        System.out.println("4 - Pesquisar perfil");
        System.out.println("5 - Curtir perfil");
        System.out.println("6 - Consultar matches");
        System.out.println("7 - Enviar mensagem");
        System.out.println("8 - Ver conversa");
        System.out.println("0 - Sair");
        System.out.println();
    }

    protected void processarOpcao(int opcao) {

        switch (opcao) {
            case 1:
                cadastrarUsuario();
                break;

            case 2:
                selecionarUsuario();
                break;

            case 3:
                listarUsuarios();
                break;

            case 4:
                pesquisarUsuario();
                break;

            case 5:
                curtirUsuario();
                break;

            case 6:
                listarMatches();
                break;

            case 7:
                enviarMensagem();
                break;

            case 8:
                listarMensagens();
                break;

            case 0:
                System.out.println("Encerrando o EntreLaços...");
                break;

            default:
                System.out.println("Opção inválida.");
        }
    }

    protected void cadastrarUsuario() {
        System.out.println();
        System.out.println("=== Cadastro de usuário ===");

        String nome = lerTextoObrigatorio("Nome: ", "Nome é obrigatório.");
        String email = lerEmailDisponivel();
        int idade = lerIdadeValida();
        String cidade = lerCidadeValida();

        Plano plano = lerPlanoValido();

        Usuario usuario = new Usuario(nome, email, idade, cidade, plano);
        selecionarInteresses(usuario);

        try {
            usuarioService.adicionar(usuario);
            salvarDados();
            System.out.println("Usuário cadastrado com sucesso!");
        } 
        catch (ExcecaoNegocio e) {
            System.out.println(String.format("Cadastro recusado: %s", e.getMessage()));
        }
    }

    protected void selecionarUsuario() {
        List < Usuario > usuarios = usuarioService.listar();

        System.out.println("\n=== Usuários cadastrados ===");

        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }

        for (int i = 0; i < usuarios.size(); i++) {
            Usuario usuario = usuarios.get(i);
            System.out.println(String.format("%d - %s - %s", i + 1, usuario.getNome(), usuario.getCidade()));
        }

        int opcaoUsuario = EntradaUtil.lerInteiro("Escolha o número do usuário: ");

        if (opcaoUsuario < 1 || opcaoUsuario > usuarios.size()) {
            System.out.println("Usuário inválido.");
            return;
        }

        Usuario usuario = usuarios.get(opcaoUsuario - 1);

        if (usuario == null) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        usuarioSelecionado = usuario;
        System.out.println(String.format("Usuário selecionado: %s", usuario.getNome()));
    }

    protected void listarUsuarios() {
        System.out.println();
        System.out.println("=== Perfis ===");

        if (usuarioSelecionado == null) {
            System.out.println("Selecione um usuário primeiro.");
            return;
        }

        for (Usuario usuario : usuarioService.listar()) {

            if (usuario == usuarioSelecionado) {
                continue;
            }

            System.out.println();
            System.out.println(usuario);
            System.out.println("Interesses:");

            for (Interesse interesse: usuario.getInteresses()) {
                if (interesse != null) {
                    System.out.println(String.format("   - %s", interesse.getNome()));
                }
            }
        }
    }

    protected void pesquisarUsuario() {
        if (usuarioSelecionado == null) {
            System.out.println("Selecione um usuário primeiro.");
            return;
        }

        System.out.println();
        System.out.println("1 - Pesquisar por nome");
        System.out.println("2 - Pesquisar por nome e cidade");

        int opcao = EntradaUtil.lerInteiro("Escolha: ");
        String nome = EntradaUtil.lerTexto("Nome: ");
        List < Usuario > resultados;

        if (opcao == 1) {
            resultados = usuarioService.pesquisar(nome);
        } 
        else if (opcao == 2) {
            String cidade = EntradaUtil.lerTexto("Cidade: ");
            resultados = usuarioService.pesquisar(nome, cidade);
        } 
        else {
            System.out.println("Opção inválida.");
            return;
        }

        boolean encontrou = false;

        for (Usuario usuario: resultados) {

            if (usuario == usuarioSelecionado) {
                continue;
            }

            encontrou = true;

            System.out.println(String.format("%s - %s", usuario.getNome(), usuario.getCidade()));
        }

        if (!encontrou) {
            System.out.println("Nenhum perfil encontrado.");
        }
    }

    protected void curtirUsuario() {
        if (usuarioSelecionado == null) {
            System.out.println("Selecione um usuário primeiro.");
            return;
        }

        List < Usuario > perfisDisponiveis = new java.util.ArrayList < > ();
        System.out.println("\n=== Perfis disponíveis para curtir ===");

        for (Usuario usuario: usuarioService.listar()) {
            if (!usuario.equals(usuarioSelecionado) && !usuarioSelecionado.getCurtidasEnviadas().contains(usuario)) {
                perfisDisponiveis.add(usuario);

                System.out.println(String.format("%d - %s - %s", perfisDisponiveis.size(), usuario.getNome(), usuario.getCidade()));
            }
        }

        if (perfisDisponiveis.isEmpty()) {
            System.out.println("Nenhum perfil disponível para curtir.");
            return;
        }

        int opcaoPerfil = EntradaUtil.lerInteiro("Escolha o número do perfil: ");

        if (opcaoPerfil < 1 || opcaoPerfil > perfisDisponiveis.size()) {
            System.out.println("Perfil inválido. A curtida não será enviada.");
            return;
        }

        Usuario destinatario = perfisDisponiveis.get(opcaoPerfil - 1);

        try {
            matchService.curtir(usuarioSelecionado, destinatario);
            salvarDados();
            System.out.println("Curtida enviada com sucesso!");

        } 
        catch (ExcecaoNegocio e) {
            System.out.println(String.format("Curtida recusada: %s", e.getMessage()));
        }
    }

    protected void listarMatches() {
        if (usuarioSelecionado == null) {
            System.out.println("Selecione um usuário primeiro.");
            return;
        }

        List < Match > matches = matchService.listarMatches(usuarioSelecionado);
        System.out.println();
        System.out.println("=== Matches ===");

        if (matches.isEmpty()) {
            System.out.println("Você ainda não possui matches.");
            return;
        }

        for (Match match: matches) {
            Usuario outro = match.obterOutroUsuario(usuarioSelecionado);
            System.out.println(outro.getNome());
        }
    }

    protected void enviarMensagem() {
        if (usuarioSelecionado == null) {
            System.out.println("Selecione um usuário primeiro.");
            return;
        }

        List < Match > matches = matchService.listarMatches(usuarioSelecionado);
        System.out.println("\n=== Contatos disponíveis para mensagem ===");

        if (matches.isEmpty()) {
            System.out.println("Você ainda não possui conversas disponíveis.");
            return;
        }

        for (int i = 0; i < matches.size(); i++) {
            Usuario contato = matches.get(i).obterOutroUsuario(usuarioSelecionado);
            System.out.println(String.format("%d - %s", i + 1, contato.getNome()));
        }

        int opcaoContato = EntradaUtil.lerInteiro("Escolha o número do contato: ");

        if (opcaoContato < 1 || opcaoContato > matches.size()) {
            System.out.println("Contato inválido. A mensagem não será enviada.");
            return;
        }

        Usuario destinatario = matches.get(opcaoContato - 1).obterOutroUsuario(usuarioSelecionado);

        exibirMensagensAnteriores(destinatario);
        String texto = EntradaUtil.lerTexto("Mensagem: ");

        try {
            mensagemService.enviarMensagem(usuarioSelecionado, destinatario, texto);
            salvarDados();
            System.out.println("Mensagem enviada!");
        } 
        catch (ExcecaoNegocio e) {
            System.out.println(String.format("Mensagem recusada: %s", e.getMessage()));
        }
    }

    protected void listarMensagens() {
        if (usuarioSelecionado == null) {
            System.out.println("Selecione um usuário primeiro.");
            return;
        }

        List < Match > matches = matchService.listarMatches(usuarioSelecionado);
        System.out.println("\n=== Conversas disponíveis ===");

        if (matches.isEmpty()) {
            System.out.println("Você ainda não possui conversas disponíveis.");
            return;
        }

        for (int i = 0; i < matches.size(); i++) {
            Usuario contato = matches.get(i).obterOutroUsuario(usuarioSelecionado);
            System.out.println(String.format("%d - %s", i + 1, contato.getNome()));
        }

        int opcaoContato = EntradaUtil.lerInteiro("Escolha o número da conversa: ");

        if (opcaoContato < 1 || opcaoContato > matches.size()) {
            System.out.println("Conversa inválida.");
            return;
        }

        Usuario outroUsuario = matches.get(opcaoContato - 1).obterOutroUsuario(usuarioSelecionado);

        try {
            List < Mensagem > mensagens = mensagemService.listarMensagens(usuarioSelecionado, outroUsuario);
            System.out.println();
            System.out.println("=== Conversa ===");

            if (mensagens.isEmpty()) {
                System.out.println("Ainda não há mensagens nesta conversa.");
            } 
            else
                for (Mensagem mensagem: mensagens) {
                    System.out.println(mensagem);
                }

        } 
        catch (ExcecaoNegocio e) {
            System.out.println(String.format("Não foi possível abrir a conversa: %s", e.getMessage()));
        }
    }

    private void selecionarInteresses(Usuario usuario) {
        System.out.println("\nInteresses disponíveis:");

        for (Interesse interesse: catalogoInteresses.listar()) {
            System.out.println(String.format("%d - %s", interesse.getId(), interesse.getNome()));
        }

        System.out.println("\nEscolha até 3 interesses.");
        System.out.println("Digite 0 para finalizar.");

        int quantidade = 0;

        while (quantidade < 3) {
            int idInteresse = EntradaUtil.lerInteiro("Escolha o interesse: ");

            if (idInteresse == 0) {
                break;
            }

            Interesse interesse = catalogoInteresses.buscarPorId(idInteresse);

            if (interesse == null) {
                System.out.println("Interesse inválido.");
                continue;
            }

            if (usuario.adicionarInteresse(interesse)) {
                quantidade++;

                System.out.println("Interesse adicionado.");
            } 
            else {
                System.out.println("Esse interesse já foi selecionado ou o limite de 3 foi atingido.");
            }
        }
    }

    protected boolean exibirPerfisParaCurtir() {
        if (usuarioSelecionado == null) {
            return true;
        }

        System.out.println("\n=== Perfis disponíveis para curtir ===");
        boolean encontrouPerfil = false;

        for (Usuario usuario: usuarioService.listar()) {
            if (!usuario.equals(usuarioSelecionado) && !usuarioSelecionado.getCurtidasEnviadas().contains(usuario)) {
                System.out.println(String.format("- %s - %s", usuario.getNome(), usuario.getCidade()));
                encontrouPerfil = true;
            }
        }

        if (!encontrouPerfil) {
            System.out.println("Nenhum perfil disponível para curtir.");
        }

        return encontrouPerfil;
    }

    protected boolean exibirConversasDisponiveis() {
        if (usuarioSelecionado == null) {
            return true;
        }

        List < Match > matches = matchService.listarMatches(usuarioSelecionado);
        System.out.println("\n=== Conversas disponíveis ===");

        if (matches.isEmpty()) {
            System.out.println("Você ainda não possui conversas disponíveis.");
            return false;
        }

        for (Match match: matches) {
            System.out.println(String.format("- %s", match.obterOutroUsuario(usuarioSelecionado).getNome()));
        }

        return true;
    }

    protected void exibirMensagensAnteriores(Usuario destinatario) {
        if (destinatario == null) {
            return;
        }

        try {
            List < Mensagem > mensagens = mensagemService.listarMensagens(usuarioSelecionado, destinatario);
            System.out.println("\n=== Mensagens anteriores ===");

            if (mensagens.isEmpty()) {
                System.out.println("Ainda não há mensagens nesta conversa.");
                return;
            }

            for (Mensagem mensagem: mensagens) {
                System.out.println(mensagem);
            }
        } 
        catch (ExcecaoNegocio e) {
            System.out.println("Ainda não há mensagens nesta conversa.");
        }
    }

    protected void salvarDados() {
        try {
            persistenciaJsonService.salvar(usuarioService.listar(), matchService);
        } 
        catch (Exception e) {
            System.out.println("Aviso: não foi possível salvar os dados em dados.json.");
        }
    }

    protected String lerTextoObrigatorio(String mensagem, String erro) {
        String texto;
        do {
            texto = EntradaUtil.lerTexto(mensagem);

            if (texto.trim().isEmpty()) {
                System.out.println(erro + " Digite novamente.");
            }
        } 
        while (texto.trim().isEmpty());

        return texto;
    }

    protected String lerEmailDisponivel() {
        String email;
        boolean formatoValido;
        boolean disponivel;

        do {
            email = lerTextoObrigatorio("E-mail: ", "E-mail é obrigatório.");
            formatoValido = emailValido(email);
            disponivel = !usuarioService.emailCadastrado(email);

            if (!formatoValido) {
                System.out.println("E-mail inválido. Use o formato nome@dominio.com. Digite novamente.");
            } 
            else if (!disponivel) {
                System.out.println("E-mail já cadastrado. Digite outro e-mail.");
            }
        } 
        while (!formatoValido || !disponivel);

        return email;
    }

    protected boolean emailValido(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    protected String lerCidadeValida() {
        String cidade;
        boolean cidadeValida;

        do {
            cidade = lerTextoObrigatorio("Cidade: ", "Cidade é obrigatória.");
            cidadeValida = cidade.matches("^[\\p{L}]+(?:[\\s'-]+[\\p{L}]+)*$");

            if (!cidadeValida) {
                System.out.println("Cidade inválida. Informe apenas letras, espaços ou hífen. Digite novamente.");
            }
        }
        while (!cidadeValida);

        return cidade;
    }

    protected int lerIdadeValida() {
        int idade;

        do {
            idade = EntradaUtil.lerInteiro("Idade: ");

            if (idade < 18) {
                System.out.println("O usuário deve ter pelo menos 18 anos. Digite novamente.");
            }
        } 
        while (idade < 18);

        return idade;
    }

    protected Plano lerPlanoValido() {
        Plano plano = null;

        do {
            int opcaoPlano = EntradaUtil.lerInteiro("Plano (1 - Essencial, 2 - Conexão): ");

            if (opcaoPlano == 1) {
                plano = new PlanoEssencial();
            } 
            else if (opcaoPlano == 2) {
                plano = new PlanoConexao();
            } 
            else {
                System.out.println("Plano inválido. Digite 1 para Essencial ou 2 para Conexão.");
            }
        } 
        while (plano == null);

        return plano;
    }

    protected void carregarDados() {
        try {
            persistenciaJsonService.carregar(usuarioService, matchService, mensagemService, catalogoInteresses);
        } 
        catch (Exception e) {
            System.out.println(String.format("Aviso: não foi possível carregar os dados de dados.json: %s", e.getMessage()));
        }
    }
}
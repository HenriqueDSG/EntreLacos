package service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exception.ExcecaoNegocio;
import model.Match;
import model.Mensagem;
import model.Usuario;
import model.interesse.CatalogoInteresses;
import model.interesse.Interesse;
import model.plano.Plano;
import model.plano.PlanoConexao;
import model.plano.PlanoEssencial;

public class PersistenciaJsonService {
    private final Path arquivo;

    public PersistenciaJsonService() {
        this(Path.of("dados.json"));
    }

    public PersistenciaJsonService(Path arquivo) {
        this.arquivo = arquivo;
    }

    public void carregar(UsuarioService usuarioService, MatchService matchService, MensagemService mensagemService, CatalogoInteresses catalogoInteresses)
    throws IOException, ExcecaoNegocio {

        if (!Files.exists(arquivo)) {
            return;
        }

        String json = Files.readString(arquivo, StandardCharsets.UTF_8);
        String usuariosJson = valorArray(json, "usuarios");

        for (String usuarioJson: objetos(usuariosJson)) {
            int id = valorInteiro(usuarioJson, "id");
            String nome = valorTexto(usuarioJson, "nome");
            String email = valorTexto(usuarioJson, "email");
            int idade = valorInteiro(usuarioJson, "idade");
            String cidade = valorTexto(usuarioJson, "cidade");
            String nomePlano = valorTexto(usuarioJson, "plano");
            Plano plano = (nomePlano.equalsIgnoreCase("Conexão")) ? new PlanoConexao() : new PlanoEssencial();

            Usuario usuario = new Usuario(id, nome, email, idade, cidade, plano);
            String interessesJson = valorArray(usuarioJson, "interesses");

            for (String nomeInteresse: textos(interessesJson)) {
                Interesse interesse = catalogoInteresses.buscarPorNome(nomeInteresse);

                if (interesse != null) {
                    usuario.adicionarInteresse(interesse);
                }
            }

            usuarioService.adicionar(usuario);
        }

        for (String usuarioJson: objetos(usuariosJson)) {
            Usuario remetente = usuarioService.buscarPorId(valorInteiro(usuarioJson, "id"));

            for (int destinatarioId: inteiros(valorArray(usuarioJson, "curtidasEnviadas"))) {

                Usuario destinatario = usuarioService.buscarPorId(destinatarioId);
                if (remetente != null && destinatario != null) {
                    matchService.curtir(remetente, destinatario);
                }
            }
        }

        String matchesJson = valorArray(json, "matches");

        for (String matchJson: objetos(matchesJson)) {
            Usuario usuario1 = usuarioService.buscarPorId(valorInteiro(matchJson, "usuario1"));
            Usuario usuario2 = usuarioService.buscarPorId(valorInteiro(matchJson, "usuario2"));
            String mensagensJson = valorArray(matchJson, "mensagens");

            for (String mensagemJson: objetos(mensagensJson)) {
                Usuario remetente = usuarioService.buscarPorId(valorInteiro(mensagemJson, "remetente"));
                String texto = valorTexto(mensagemJson, "texto");

                if (remetente != null && usuario1 != null && usuario2 != null) {
                    mensagemService.enviarMensagem(remetente, remetente.equals(usuario1) ? usuario2 : usuario1, texto);
                }
            }
        }
    }

    public void salvar(List < Usuario > usuarios, MatchService matchService) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{\n  \"usuarios\": [\n");

        for (int i = 0; i < usuarios.size(); i++) {
            Usuario usuario = usuarios.get(i);
            json.append("    {\n");
            json.append("      \"id\": ").append(usuario.getId()).append(",\n");
            json.append("      \"nome\": \"").append(escapar(usuario.getNome())).append("\",\n");
            json.append("      \"email\": \"").append(escapar(usuario.getEmail())).append("\",\n");
            json.append("      \"idade\": ").append(usuario.getIdade()).append(",\n");
            json.append("      \"cidade\": \"").append(escapar(usuario.getCidade())).append("\",\n");
            json.append("      \"plano\": \"").append(escapar(usuario.getPlano().getNome())).append("\",\n");
            json.append("      \"interesses\": [");

            for (int j = 0; j < usuario.getInteresses().size(); j++) {
                if (j > 0) {
                    json.append(", ");
                }

                Interesse interesse = usuario.getInteresses().get(j);
                json.append("\"").append(escapar(interesse.getNome())).append("\"");
            }

            json.append("],\n      \"curtidasEnviadas\": [");

            for (int j = 0; j < usuario.getCurtidasEnviadas().size(); j++) {
                if (j > 0) {
                    json.append(", ");
                }

                json.append(usuario.getCurtidasEnviadas().get(j).getId());
            }

            json.append("]\n    }");

            if (i < usuarios.size() - 1) {
                json.append(",");
            }

            json.append("\n");
        }

        json.append("  ],\n  \"matches\": [\n");
        List < Match > matches = matchService.listarTodos();

        for (int i = 0; i < matches.size(); i++) {
            Match match = matches.get(i);
            json.append("    {\n");
            json.append("      \"usuario1\": ").append(match.getUsuario1().getId()).append(",\n");
            json.append("      \"usuario2\": ").append(match.getUsuario2().getId()).append(",\n");
            json.append("      \"mensagens\": [\n");

            List < Mensagem > mensagens = match.getMensagens();

            for (int j = 0; j < mensagens.size(); j++) {
                Mensagem mensagem = mensagens.get(j);
                json.append("        {\"remetente\": ")
                    .append(mensagem.getRemetente().getId())
                    .append(", \"texto\": \"")
                    .append(escapar(mensagem.getTexto()))
                    .append("\"}");

                if (j < mensagens.size() - 1) {
                    json.append(",");
                }

                json.append("\n");
            }

            json.append("      ]\n    }");

            if (i < matches.size() - 1) {
                json.append(",");
            }

            json.append("\n");
        }

        json.append("  ]\n}\n");
        Files.writeString(arquivo, json.toString(), StandardCharsets.UTF_8);
    }

    private String escapar(String valor) {
        return valor.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r");
    }

    private String valorArray(String json, String campo) {
        int inicioCampo = json.indexOf("\"" + campo + "\"");

        if (inicioCampo < 0) {
            return "";
        }

        int inicio = json.indexOf('[', inicioCampo);
        int fim = encontrarFechamento(json, inicio, '[', ']');
        return (inicio >= 0 && fim >= 0) ? json.substring(inicio + 1, fim) : "";
    }

    private String valorTexto(String json, String campo) {
        Pattern pattern = Pattern.compile("\\\"" + campo + "\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"");
        Matcher matcher = pattern.matcher(json);

        if (!matcher.find()) {
            return "";
        }

        return desescapar(matcher.group(1));
    }

    private int valorInteiro(String json, String campo) {
        Pattern pattern = Pattern.compile("\\\"" + campo + "\\\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        return (matcher.find()) ? Integer.parseInt(matcher.group(1)) : 0;
    }

    private List < Integer > inteiros(String json) {
        List < Integer > valores = new ArrayList < > ();
        Matcher matcher = Pattern.compile("\\d+").matcher(json);

        while (matcher.find()) {
            valores.add(Integer.parseInt(matcher.group()));
        }

        return valores;
    }

    private List < String > textos(String json) {
        List < String > valores = new ArrayList < > ();
        Matcher matcher = Pattern.compile("\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"").matcher(json);

        while (matcher.find()) {
            valores.add(desescapar(matcher.group(1)));
        }

        return valores;
    }

    private List < String > objetos(String json) {
        List < String > valores = new ArrayList < > ();
        boolean dentroTexto = false;
        boolean escapado = false;
        int profundidade = 0;
        int inicioObjeto = -1;

        for (int i = 0; i < json.length(); i++) {
            char caractere = json.charAt(i);

            if (caractere == '\\' && dentroTexto && !escapado) {
                escapado = true;
                continue;
            }

            if (caractere == '"' && !escapado) {
                dentroTexto = !dentroTexto;
            }

            escapado = false;

            if (dentroTexto) {
                continue;
            }

            if (caractere == '{') {
                if (profundidade == 0) {
                    inicioObjeto = i;
                }

                profundidade++;
            } 
            else if (caractere == '}') {
                profundidade--;

                if (profundidade == 0 && inicioObjeto >= 0) {
                    valores.add(json.substring(inicioObjeto, i + 1));
                    inicioObjeto = -1;
                }
            }
        }

        return valores;
    }

    private int encontrarFechamento(String texto, int inicio, char abertura, char fechamento) {
        if (inicio < 0) {
            return -1;
        }

        int profundidade = 0;
        boolean dentroTexto = false;
        boolean escapado = false;

        for (int i = inicio; i < texto.length(); i++) {
            char caractere = texto.charAt(i);

            if (caractere == '\\' && dentroTexto && !escapado) {
                escapado = true;
                continue;
            }

            if (caractere == '"' && !escapado) {
                dentroTexto = !dentroTexto;
            }

            escapado = false;

            if (dentroTexto) {
                continue;
            }

            if (caractere == abertura) {
                profundidade++;
            } 
            else if (caractere == fechamento && --profundidade == 0) {
                return i;
            }
        }
        
        return -1;
    }

    private String desescapar(String valor) {
        return valor.replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\");
    }
}
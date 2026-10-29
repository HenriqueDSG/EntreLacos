package service;

import exception.ExcecaoNegocio;
import model.Match;
import model.Mensagem;
import model.Usuario;

import java.util.List;

public class MensagemService {
    protected MatchService matchService;

    public MensagemService(MatchService matchService) {
        this.matchService = matchService;
    }

    public void enviarMensagem(Usuario remetente, Usuario destinatario, String texto) throws ExcecaoNegocio {
        validarMensagem(remetente, destinatario, texto);
        Match match = encontrarMatch(remetente, destinatario);
        Mensagem mensagem = new Mensagem(remetente, texto.trim());
        match.adicionarMensagem(mensagem);
    }

    protected void validarMensagem(Usuario remetente, Usuario destinatario, String texto)
    
    throws ExcecaoNegocio {
        if (remetente == null || destinatario == null) {
            throw new ExcecaoNegocio("Usuário não encontrado.");
        }

        if (texto == null || texto.trim().isEmpty()) {
            throw new ExcecaoNegocio("A mensagem não pode estar vazia.");
        }

        if (encontrarMatch(remetente, destinatario) == null) {
            throw new ExcecaoNegocio("Os usuários não possuem um match.");
        }
    }

    protected Match encontrarMatch(Usuario usuario1, Usuario usuario2) {
        List < Match > matches = matchService.listarMatches(usuario1);

        for (Match match: matches) {
            if (match.envolve(usuario1, usuario2)) {
                return match;
            }
        }

        return null;
    }

    public List < Mensagem > listarMensagens(Usuario usuario1, Usuario usuario2)

    throws ExcecaoNegocio {
        Match match = encontrarMatch(usuario1, usuario2);

        if (match == null) {
            throw new ExcecaoNegocio("Os usuários não possuem um match.");
        }

        return match.getMensagens();
    }
}
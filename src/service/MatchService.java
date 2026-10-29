package service;

import java.util.ArrayList;
import java.util.List;

import exception.ExcecaoNegocio;
import model.Match;
import model.Usuario;

public class MatchService {
    protected List < Match > matches;

    public MatchService() {
        matches = new ArrayList < > ();
    }

    public void curtir(Usuario remetente, Usuario destinatario) throws ExcecaoNegocio {
        validarCurtida(remetente, destinatario);
        remetente.getCurtidasEnviadas().add(destinatario);
        verificarMatch(remetente, destinatario);
    }

    protected void validarCurtida(Usuario remetente, Usuario destinatario)

    throws ExcecaoNegocio {
        if (remetente == null || destinatario == null) {
            throw new ExcecaoNegocio("Usuário não encontrado.");
        }

        if (remetente.equals(destinatario)) {
            throw new ExcecaoNegocio("Não é possível curtir a si próprio.");
        }

        if (jaCurtiu(remetente, destinatario)) {
            throw new ExcecaoNegocio("Você já curtiu este usuário.");
        }

        int limite = remetente.getPlano().getLimiteCurtidas();
        int quantidadeCurtidas = remetente.getCurtidasEnviadas().size();

        if (quantidadeCurtidas >= limite) {
            throw new ExcecaoNegocio("Limite de curtidas atingido.");
        }
    }

    protected void verificarMatch(Usuario remetente, Usuario destinatario) {
        if (jaFoiCurtido(destinatario, remetente)) {
            if (!existeMatch(remetente, destinatario)) {
                Match match = new Match(remetente, destinatario);
                matches.add(match);
            }
        }
    }

    protected boolean jaFoiCurtido(Usuario usuario, Usuario outroUsuario) {
        return usuario.getCurtidasEnviadas().contains(outroUsuario);
    }

    protected boolean existeMatch(Usuario usuario1, Usuario usuario2) {
        return buscarMatch(usuario1, usuario2) != null;
    }

    protected boolean jaCurtiu(Usuario remetente, Usuario destinatario) {
        return remetente.getCurtidasEnviadas().contains(destinatario);
    }

    public List < Match > listarMatches(Usuario usuario) {
        List < Match > resultado = new ArrayList < > ();

        for (Match match: matches) {
            if (match.envolve(usuario)) {
                resultado.add(match);
            }
        }

        return resultado;
    }

    public List < Match > listarTodos() {
        return new ArrayList<>(matches);
    }

    public Match buscarMatch(Usuario usuario1, Usuario usuario2) {
        for (Match match: matches) {
            if (match.envolve(usuario1, usuario2)) {
                return match;
            }
        }

        return null;
    }
}
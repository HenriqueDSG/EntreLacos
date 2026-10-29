package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Match {
    protected Usuario usuario1;
    protected Usuario usuario2;
    protected List < Mensagem > mensagens;

    public Match(Usuario usuario1, Usuario usuario2) {
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
        this.mensagens = new ArrayList < > ();
    }

    public Usuario getUsuario1() {
        return usuario1;
    }

    public Usuario getUsuario2() {
        return usuario2;
    }

    public List < Mensagem > getMensagens() {
        return mensagens;
    }

    public void adicionarMensagem(Mensagem mensagem) {
        mensagens.add(mensagem);
    }

    public boolean envolve(Usuario usuario) {
        return Objects.equals(usuario1, usuario) || Objects.equals(usuario2, usuario);
    }

    public boolean envolve(Usuario usuarioA, Usuario usuarioB) {
        return (Objects.equals(usuario1, usuarioA) && Objects.equals(usuario2, usuarioB)) || (Objects.equals(usuario1, usuarioB) && Objects.equals(usuario2, usuarioA));
    }

    public Usuario obterOutroUsuario(Usuario usuario) {
        if (Objects.equals(usuario1, usuario)) {
            return usuario2;
        }

        if (Objects.equals(usuario2, usuario)) {
            return usuario1;
        }

        return null;
    }
}
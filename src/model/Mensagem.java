package model;

public class Mensagem {
    protected Usuario remetente;
    protected String texto;

    public Mensagem(Usuario remetente, String texto) {
        this.remetente = remetente;
        this.texto = texto;
    }

    public Usuario getRemetente() {
        return remetente;
    }

    public String getTexto() {
        return texto;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", remetente.getNome(), texto);
    }
}
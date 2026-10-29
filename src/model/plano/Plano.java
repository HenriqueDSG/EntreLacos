package model.plano;

public abstract class Plano {
    protected String nome;

    public String getNome() {
        return nome;
    }

    public abstract int getLimiteCurtidas();
}
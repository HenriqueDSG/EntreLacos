package model.plano;

public class PlanoConexao extends Plano {
    private static final int LIMITE_CURTIDAS = 5;

    public PlanoConexao() {
        this.nome = "Conexão";
    }

    @Override
    public int getLimiteCurtidas() {
        return LIMITE_CURTIDAS;
    }
}
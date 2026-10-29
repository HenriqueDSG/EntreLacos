package model.plano;

public class PlanoEssencial extends Plano {
    private static final int LIMITE_CURTIDAS = 2;
    
    public PlanoEssencial() {
        this.nome = "Essencial";
    }

    @Override
    public int getLimiteCurtidas() {
        return LIMITE_CURTIDAS;
    }
}
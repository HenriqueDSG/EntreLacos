package model.interesse;

import java.util.ArrayList;
import java.util.List;

public class CatalogoInteresses {
    private List<Interesse> interesses;

    public CatalogoInteresses() {
        interesses = new ArrayList<>();

        interesses.add(new Interesse(1, "Música"));
        interesses.add(new Interesse(2, "Cinema"));
        interesses.add(new Interesse(3, "Esportes"));
        interesses.add(new Interesse(4, "Viagens"));
        interesses.add(new Interesse(5, "Livros"));
        interesses.add(new Interesse(6, "Games"));
    }

    public List<Interesse> listar() {
        return interesses;
    }

    public Interesse buscarPorId(int id) {

        for (Interesse interesse : interesses) {

            if (interesse.getId() == id) {
                return interesse;
            }
        }

        return null;
    }

    public Interesse buscarPorNome(String nome) {
        if (nome == null) {
            return null;
        }

        for (Interesse interesse : interesses) {
            if (interesse.getNome().equalsIgnoreCase(nome.trim())) {
                return interesse;
            }
        }

        return null;
    }
}
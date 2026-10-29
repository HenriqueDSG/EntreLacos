package model;
import model.plano.Plano;
import java.util.ArrayList;
import java.util.List;
import model.interesse.Interesse;

public class Usuario {
    protected int id;
    protected String nome;
    protected String email;
    protected int idade;
    protected String cidade;
    protected Plano plano;
    protected List < Interesse > interesses;
    protected List < Usuario > curtidasEnviadas;

    public Usuario(int id, String nome, String email, int idade, String cidade, Plano plano) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.idade = idade;
        this.cidade = cidade;
        this.plano = plano;
        this.interesses = new ArrayList < > ();
        this.curtidasEnviadas = new ArrayList < > ();
    }

    public Usuario(String nome, String email, int idade, String cidade, Plano plano) {
        this(0, nome, email, idade, cidade, plano);
    }

    public int getId() {
        return id;
    }

    public void definirId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public int getIdade() {
        return idade;
    }

    public String getCidade() {
        return cidade;
    }

    public Plano getPlano() {
        return plano;
    }

    public List < Interesse > getInteresses() {
        return interesses;
    }

    public boolean adicionarInteresse(Interesse interesse) {
        if (interesse == null) {
            return false;
        }

        if (interesses.size() >= 3) {
            return false;
        }

        if (interesses.contains(interesse)) {
            return false;
        }

        interesses.add(interesse);

        return true;
    }

    public List < Usuario > getCurtidasEnviadas() {
        return curtidasEnviadas;
    }

    @Override
    public String toString() {
        return String.format("%s - %d anos - %s - Plano: %s", nome, idade, cidade, plano.getNome());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Usuario)) {
            return false;
        }

        Usuario outro = (Usuario) obj;
        return id == outro.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
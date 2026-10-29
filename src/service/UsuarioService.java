package service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import exception.ExcecaoNegocio;
import model.Usuario;

public class UsuarioService {

    protected List < Usuario > usuarios;

    public UsuarioService() {
        usuarios = new ArrayList < > ();
    }

    public void adicionar(Usuario usuario) throws ExcecaoNegocio {
        if (usuario != null && usuario.getId() <= 0) {
            usuario.definirId(proximoId());
        }

        validarUsuario(usuario);
        usuarios.add(usuario);
    }

    protected int proximoId() {
        int maiorId = 0;

        for (Usuario usuario : usuarios) {
            if (usuario.getId() > maiorId) {
                maiorId = usuario.getId();
            }
        }

        return maiorId + 1;
    }

    protected void validarUsuario(Usuario usuario)

    throws ExcecaoNegocio {
        if (usuario == null) {
            throw new ExcecaoNegocio("Usuário não pode ser nulo.");
        }

        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new ExcecaoNegocio("Nome é obrigatório.");
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new ExcecaoNegocio("E-mail é obrigatório.");
        }

        if (usuario.getCidade() == null || usuario.getCidade().trim().isEmpty()) {
            throw new ExcecaoNegocio("Cidade é obrigatória.");
        }

        if (usuario.getPlano() == null) {
            throw new ExcecaoNegocio("Plano é obrigatório.");
        }

        if (usuario.getIdade() < 18) {
            throw new ExcecaoNegocio("O usuário deve ter pelo menos 18 anos.");
        }

        if (emailExiste(usuario.getEmail())) {
            throw new ExcecaoNegocio("E-mail já cadastrado.");
        }

        if (idExiste(usuario.getId())) {
            throw new ExcecaoNegocio("ID já cadastrado.");
        }
    }

    protected boolean emailExiste(String email) {
        for (Usuario usuario: usuarios) {

            if (usuario.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }

        return false;
    }

    public boolean emailCadastrado(String email) {
        if (email == null) {
            return false;
        }

        return emailExiste(email.trim());
    }

    public List < Usuario > listar() {
        List < Usuario > resultado = new ArrayList < > (usuarios);
        resultado.sort(Comparator.comparing(Usuario::getNome, String.CASE_INSENSITIVE_ORDER));
        return resultado;
    }

    public List < Usuario > pesquisar(String nome) {
        List < Usuario > resultado = new ArrayList < > ();

        for (Usuario usuario: usuarios) {
            if (usuario.getNome().toLowerCase().contains(nome.toLowerCase())) {
                resultado.add(usuario);
            }
        }

        resultado.sort(Comparator.comparing(Usuario::getNome, String.CASE_INSENSITIVE_ORDER));
        return resultado;
    }

    public List < Usuario > pesquisar(String nome, String cidade) {
        List < Usuario > resultado = new ArrayList < > ();

        for (Usuario usuario: usuarios) {
            boolean nomeEncontrado = usuario.getNome().toLowerCase().contains(nome.toLowerCase());
            boolean cidadeEncontrada = usuario.getCidade().toLowerCase().contains(cidade.toLowerCase());

            if (nomeEncontrado && cidadeEncontrada) {
                resultado.add(usuario);
            }
        }

        resultado.sort(Comparator.comparing(Usuario::getNome, String.CASE_INSENSITIVE_ORDER));

        return resultado;
    }

    public Usuario buscarPorId(int id) {
        for (Usuario usuario: usuarios) {
            if (usuario.getId() == id) {
                return usuario;
            }
        }

        return null;
    }

    public Usuario buscarPorNome(String nome) {
        if (nome == null) {
            return null;
        }

        String nomeNormalizado = nome.trim();

        for (Usuario usuario : usuarios) {
            if (usuario.getNome().equalsIgnoreCase(nomeNormalizado)) {
                return usuario;
            }
        }

        return null;
    }

    protected boolean idExiste(int id) {
        for (Usuario usuario: usuarios) {

            if (usuario.getId() == id) {
                return true;
            }
        }

        return false;
    }
}
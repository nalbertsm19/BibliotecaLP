package Repository;

import Model.UsuarioModel;
import jakarta.persistence.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {
    protected EntityManager entityManager;

    // Construtor
    public UsuarioRepository(EntityManager entityManager) {
        this.entityManager = entityManager; // Use o entityManager passado
    }


    // Criar um usuário (CREATE)
    public String salvar(UsuarioModel usuario) throws SQLException {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(usuario);
            entityManager.getTransaction().commit();
            return "Salvo com Sucesso.";
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            return e.getMessage();
        }
    }

    // Buscar um usuário por ID (READ)
    public UsuarioModel buscarPorId(int id) {
        UsuarioModel usuario = new UsuarioModel();
        try{
            usuario = entityManager.find(UsuarioModel.class, id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return usuario;
    }

    public String remover(UsuarioModel usuario){
        try {
            entityManager.getTransaction().begin();
            entityManager.remove(usuario);
            entityManager.getTransaction().commit();
            return "Removido com sucesso!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // Listar todos os usuários (READ)
    public List<UsuarioModel> listarTodos() {
        try{
            List<UsuarioModel> usuarios = entityManager.createQuery("from UsuarioModel").getResultList();
            return usuarios;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    // Atualizar um usuário (UPDATE)
    public void atualizar(UsuarioModel usuario) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(usuario); // Atualiza o usuário se já existe
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    // Deletar um usuário por ID (DELETE)
    public void deletar(int id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            UsuarioModel usuario = entityManager.find(UsuarioModel.class, id);
            if (usuario != null) {
                entityManager.remove(usuario);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }
}

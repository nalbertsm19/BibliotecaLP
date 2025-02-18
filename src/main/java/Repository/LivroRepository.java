package Repository;

import Model.LivroModel;
import Model.UsuarioModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;

public class LivroRepository {
    private final EntityManager entityManager;

    // Construtor
    public LivroRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Método auxiliar para manipular transações
    private void executarTransacao(Runnable acao) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            acao.run();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    // Listar livros com poucas unidades (quantidadeDisponivel < 5)
    public List<LivroModel> listarLivrosPoucasUnidades() {
        return entityManager.createQuery(
                "SELECT l FROM LivroModel l WHERE l.quantidadeDisponivel < 5", LivroModel.class
        ).getResultList();
    }

    // Criar um livro (CREATE)
    public String salvar(LivroModel livro) {
        try {
            executarTransacao(() -> entityManager.persist(livro));
            return "Salvo com Sucesso.";
        } catch (Exception e) {
            return "Erro ao salvar: " + e.getMessage();
        }
    }

    // Buscar um livro por ID (READ)
    public LivroModel buscarPorId(int id) {
        return entityManager.find(LivroModel.class, id);
    }

    // Buscar livros disponíveis (quantidadeDisponivel > 0)
    public List<LivroModel> buscarLivrosDisponiveis() {
        try {
            return entityManager.createQuery(
                    "SELECT l FROM LivroModel l WHERE l.quantidadeDisponivel > 0", LivroModel.class
            ).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Atualizar um livro (UPDATE)
    public void atualizar(LivroModel livro) {
        executarTransacao(() -> entityManager.merge(livro));
    }

    // Deletar um livro por ID (DELETE)
    public void deletar(int id) {
        executarTransacao(() -> {
            LivroModel livro = entityManager.find(LivroModel.class, id);
            if (livro != null) {
                entityManager.remove(livro);
            }
        });
    }
    // Listar todos os livros (READ)
    public List<LivroModel> listarTodos() {
        try {
            return entityManager.createQuery("SELECT l FROM LivroModel l", LivroModel.class)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // Retorna uma lista vazia em caso de erro
        }
    }

}

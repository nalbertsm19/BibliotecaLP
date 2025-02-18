package Repository;

import Model.EmprestimoModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class EmprestimoRepository {
    private EntityManager em;

    public EmprestimoRepository(EntityManager entityManager) {
        this.em = entityManager;
    }

    // Métodos CRUD
    public void salvar(EmprestimoModel emprestimo) {
        em.getTransaction().begin();
        em.persist(emprestimo);
        em.getTransaction().commit();
    }

    public EmprestimoModel buscarPorId(int id) {
        return em.find(EmprestimoModel.class, id);
    }

    public List<EmprestimoModel> listar() {
        return em.createQuery("FROM EmprestimoModel", EmprestimoModel.class).getResultList();
    }

    public void atualizar(EmprestimoModel emprestimo) {
        em.getTransaction().begin();
        em.merge(emprestimo);
        em.getTransaction().commit();
    }

    public void remover(EmprestimoModel emprestimo) {
        em.getTransaction().begin();
        em.remove(em.contains(emprestimo) ? emprestimo : em.merge(emprestimo));
        em.getTransaction().commit();
    }

    // Método para deletar empréstimo por ID
    public void deletar(int id) {
        EmprestimoModel emprestimo = buscarPorId(id);
        if (emprestimo != null) {
            em.getTransaction().begin();
            em.remove(emprestimo);
            em.getTransaction().commit();
        }
    }

    // Método para atualizar devolução
    public void atualizarDevolucao(int id, boolean devolvido) {
        EmprestimoModel emprestimo = buscarPorId(id);
        if (emprestimo != null) {
            emprestimo.setDevolvido(devolvido);
            atualizar(emprestimo);
        }
    }

    // Método para listar empréstimos por livro
    public List<EmprestimoModel> listarPorLivro(int livroId) {
        TypedQuery<EmprestimoModel> query = em.createQuery(
                "SELECT e FROM EmprestimoModel e WHERE e.livro.id = :livroId", EmprestimoModel.class);
        query.setParameter("livroId", livroId);
        return query.getResultList();
    }
}

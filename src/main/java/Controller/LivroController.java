package Controller;

import Model.EmprestimoModel;
import Model.LivroModel;
import Repository.EmprestimoRepository;
import Repository.LivroRepository;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class LivroController {
    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final EmprestimoController emprestimoController;
    private final EntityManager entityManager;

    public LivroController(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.livroRepository = new LivroRepository(entityManager);
        this.emprestimoRepository = new EmprestimoRepository(entityManager);
        this.emprestimoController = new EmprestimoController(entityManager);
    }

    public LivroController(EmprestimoRepository emprestimoRepository, EmprestimoController emprestimoController, EntityManager entityManager) {
        this.entityManager = entityManager;
        this.livroRepository = new LivroRepository(entityManager);
        this.emprestimoRepository = emprestimoRepository;
        this.emprestimoController = emprestimoController;
    }

    public void deletarLivro(int id) {
        try {
            if (temEmprestimosPendentes(id)) {
                JOptionPane.showMessageDialog(null, "Esse livro possui um empréstimo pendente, portanto não pode ser excluído.");
                return;
            }
            deletarEmprestimosPorLivro(id); // Primeiro, exclui os empréstimos associados ao livro
            LivroModel livro = livroRepository.buscarPorId(id);
            if (livro != null) {
                livroRepository.deletar(id);
                JOptionPane.showMessageDialog(null, "Livro deletado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(null, "Livro não encontrado!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao deletar livro: " + e.getMessage());
        }
    }


    public List<LivroModel> buscarLivrosPoucasUnidades() {
        return livroRepository.listarLivrosPoucasUnidades();
    }

    public String salvar(LivroModel livro) throws SQLException {
        return livroRepository.salvar(livro);
    }

    public LivroModel buscarPorId(int id) {
        try {
            LivroModel livro = livroRepository.buscarPorId(id);
            if (livro != null) {
                return livro;
            } else {
                System.out.println("Livro não encontrado!");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar livro: " + e.getMessage());
            return null;
        }
    }

    public void atualizarLivro(int id, String titulo, String tema, String autor, String isbn, Date dataPublicacao, int quantidadeDisponivel) {
        try {
            LivroModel livro = livroRepository.buscarPorId(id);
            if (livro != null) {
                livro.setTitulo(titulo);
                livro.setTema(tema);
                livro.setAutor(autor);
                livro.setIsbn(isbn);
                livro.setDataPublicacao(dataPublicacao);
                livro.setQuantidadeDisponivel(quantidadeDisponivel);
                livroRepository.atualizar(livro);
                System.out.println("Livro atualizado com sucesso!");
            } else {
                System.out.println("Livro não encontrado!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao atualizar livro: " + e.getMessage());
        }
    }

    // Método para atualizar a quantidade de exemplares disponíveis de um livro
    public void atualizarQuantidadeDisponivel(int id, int novaQuantidade) {
        try {
            LivroModel livro = livroRepository.buscarPorId(id);
            if (livro != null) {
                if (novaQuantidade >= 0) {
                    livro.setQuantidadeDisponivel(novaQuantidade);
                    livroRepository.atualizar(livro);  // Atualiza o livro no banco de dados
                    System.out.println("Quantidade disponível do livro atualizada com sucesso!");
                } else {
                    System.out.println("Erro: A quantidade disponível não pode ser negativa!");
                }
            } else {
                System.out.println("Erro: Livro não encontrado!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao atualizar quantidade disponível: " + e.getMessage());
        }
    }

    // Método para buscar livros disponíveis
    public List<LivroModel> buscarLivrosDisponiveis() {
        return livroRepository.buscarLivrosDisponiveis();
    }

    // Método para verificar se há empréstimos pendentes
    private boolean temEmprestimosPendentes(int livroId) {
        List<EmprestimoModel> emprestimos = emprestimoRepository.listarPorLivro(livroId);
        return emprestimos.stream().anyMatch(emprestimo -> !emprestimo.isDevolvido());
    }

    // Método para deletar empréstimos por livro
    public void deletarEmprestimosPorLivro(int livroId) {
        try {
            List<EmprestimoModel> emprestimos = emprestimoController.listarPorLivro(livroId);
            for (EmprestimoModel emprestimo : emprestimos) {
                emprestimoController.deletar(emprestimo.getId());
            }
            System.out.println("Empréstimos relacionados ao livro deletados com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao deletar empréstimos relacionados ao livro: " + e.getMessage());
        }
    }
    public List<LivroModel> listarTodos() {
        return entityManager.createQuery("SELECT l FROM LivroModel l", LivroModel.class).getResultList();
    }

}

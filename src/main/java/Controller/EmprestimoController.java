package Controller;

import Model.EmprestimoModel;
import Model.UsuarioModel;
import Model.LivroModel;
import Repository.EmprestimoRepository;
import Repository.UsuarioRepository;
import Repository.LivroRepository;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import java.util.Date;
import java.util.List;

public class EmprestimoController {
    private EmprestimoRepository emprestimoRepository;
    private UsuarioRepository usuarioRepository;
    private LivroRepository livroRepository;

    public EmprestimoController(EntityManager entityManager) {
        this.emprestimoRepository = new EmprestimoRepository(entityManager);
        this.usuarioRepository = new UsuarioRepository(entityManager);
        this.livroRepository = new LivroRepository(entityManager);
    }

    // Registrar um novo empréstimo
    public void salvar(EmprestimoModel emprestimo) {
        try {
            // Buscar o usuário e o livro pelo ID
            UsuarioModel usuario = usuarioRepository.buscarPorId(emprestimo.getUsuario().getId());
            LivroModel livro = livroRepository.buscarPorId(emprestimo.getLivro().getId());

            // Verificar se o usuário e o livro existem
            if (usuario != null && livro != null) {
                emprestimo.setUsuario(usuario);
                emprestimo.setLivro(livro);
                emprestimo.setDataEmprestimo(new Date());
                emprestimo.setDevolvido(false); // Inicialmente, o livro não está devolvido

                // Definir a data de devolução (14 dias após o empréstimo)
                Date dataDevolucao = new Date();
                dataDevolucao.setTime(dataDevolucao.getTime() + (14L * 24 * 60 * 60 * 1000));
                emprestimo.setDataDevolucao(dataDevolucao);

                // Salvar o empréstimo
                emprestimoRepository.salvar(emprestimo);
                System.out.println("Empréstimo registrado com sucesso!");
            } else {
                System.out.println("Usuário ou livro não encontrados.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Listar todos os empréstimos
    public List<EmprestimoModel> listarEmprestimos() {
        return emprestimoRepository.listar();
    }

    // Devolver um livro
    public void devolverEmprestimo(int emprestimoId) {
        EmprestimoModel emprestimo = emprestimoRepository.buscarPorId(emprestimoId);

        if (emprestimo != null) {
            // Verifica se o livro já foi devolvido
            if (emprestimo.isDevolvido()) {
                JOptionPane.showMessageDialog(null, "Este livro já foi devolvido.");
                return;
            }

            // Define a data real de devolução (agora)
            emprestimo.setDataDevolucao(new Date());

            // Verifica o atraso e calcula a multa, se houver
            long diasAtraso = (emprestimo.getDataDevolucao().getTime() - emprestimo.getDataDevolucao().getTime()) / (1000 * 60 * 60 * 24);
            if (diasAtraso > 0) {
                double valorMulta = diasAtraso * 2.50; // Exemplo: R$2,50 por dia de atraso
                JOptionPane.showMessageDialog(null, "O livro está " + diasAtraso + " dias atrasado. Multa: R$ " + valorMulta);
            }

            // Define como devolvido
            emprestimo.setDevolvido(true);

            // Atualiza o empréstimo no banco
            emprestimoRepository.salvar(emprestimo);

            JOptionPane.showMessageDialog(null, "Livro devolvido com sucesso!");
        } else {
            JOptionPane.showMessageDialog(null, "Empréstimo não encontrado!");
        }
    }

    // Buscar um empréstimo por ID
    public EmprestimoModel buscarEmprestimoPorId(int id) {
        return emprestimoRepository.buscarPorId(id);
    }

    // Atualizar um empréstimo existente
    public void atualizarEmprestimo(EmprestimoModel emprestimo) {
        try {
            // Verifica se o empréstimo existe
            EmprestimoModel existente = emprestimoRepository.buscarPorId(emprestimo.getId());
            if (existente != null) {
                // Atualiza os dados do empréstimo
                existente.setUsuario(emprestimo.getUsuario());
                existente.setLivro(emprestimo.getLivro());
                existente.setDataEmprestimo(emprestimo.getDataEmprestimo());
                existente.setDataDevolucao(emprestimo.getDataDevolucao());
                existente.setDevolvido(emprestimo.isDevolvido());

                // Salva as alterações
                emprestimoRepository.salvar(existente);
                System.out.println("Empréstimo atualizado com sucesso!");
            } else {
                System.out.println("Empréstimo não encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public EmprestimoModel buscarPorId(int id) {
        return emprestimoRepository.buscarPorId(id);
    }

    public List<EmprestimoModel> listarPorLivro(int livroId) {
        return emprestimoRepository.listarPorLivro(livroId);
    }
    // Remover um empréstimo
    public void removerEmprestimo(int id) {
        try {
            EmprestimoModel emprestimo = emprestimoRepository.buscarPorId(id);
            if (emprestimo != null) {
                emprestimoRepository.remover(emprestimo);
                System.out.println("Empréstimo removido com sucesso!");
            } else {
                System.out.println("Empréstimo não encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void deletar(int id) {
        emprestimoRepository.deletar(id);
    }

    public void deletarEmprestimosPorLivro(int livroId) {
        try {
            List<EmprestimoModel> emprestimos = emprestimoRepository.listarPorLivro(livroId);
            for (EmprestimoModel emprestimo : emprestimos) {
                emprestimoRepository.deletar(emprestimo.getId());
            }
            System.out.println("Empréstimos relacionados ao livro deletados com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao deletar empréstimos relacionados ao livro: " + e.getMessage());
        }
    }


}

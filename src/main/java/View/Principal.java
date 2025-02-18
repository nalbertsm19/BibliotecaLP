package View;

import Controller.EmprestimoController;
import Controller.LivroController;
import Controller.UsuarioController;
import Model.LivroModel;
import Repository.EmprestimoRepository;
import Repository.LivroRepository;
import Repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Principal extends JFrame {
    private LivroController livroController;
    private LivroRepository livroRepo;
    private JPanel jPanelPrincipal;
    private EmprestimoController emprestimoController;
    private EmprestimoRepository emprestimoRepo;
    private JMenuBar menuBar;
    private UsuarioRepository usuarioRepo;
    private UsuarioController usuarioController;
    private EntityManager em;

    // Construtor Sem Argumentos
    public Principal() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("crudHibernatePU");
        this.em = emf.createEntityManager();
        this.usuarioRepo = new UsuarioRepository(em);
        this.usuarioController = new UsuarioController(em);
        this.livroRepo = new LivroRepository(em);
        this.livroController = new LivroController(em);
        this.emprestimoRepo = new EmprestimoRepository(em);
        this.emprestimoController = new EmprestimoController(em);
        criacaoDoMenu();
        criarCardsLivros();
        this.setTitle("Sistema de Gestão de Biblioteca");
        this.setContentPane(jPanelPrincipal);
        this.setSize(800, 600); // Aumentar o tamanho da janela para torná-la mais espaçosa
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // Centralizar a janela na tela
        this.setVisible(true);
    }

    public Principal(EntityManager entityManager) {
        this.em = entityManager;
        this.usuarioRepo = new UsuarioRepository(entityManager);
        this.usuarioController = new UsuarioController(entityManager);
        this.livroRepo = new LivroRepository(entityManager);
        this.livroController = new LivroController(entityManager);
        this.emprestimoRepo = new EmprestimoRepository(entityManager);
        this.emprestimoController = new EmprestimoController(entityManager);
        criacaoDoMenu();
        criarCardsLivros();
        this.setTitle("Sistema de Gestão de Biblioteca");
        this.setContentPane(jPanelPrincipal);
        this.setSize(800, 600); // Aumentar o tamanho da janela para torná-la mais espaçosa
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // Centralizar a janela na tela
        this.setVisible(true);
    }

    private void criacaoDoMenu() {
        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        // Adicionar estilos ao menu bar
        menuBar.setBackground(new Color(70, 130, 180));
        menuBar.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Estilo dos menus
        JMenu manterLivro = criarMenu("Gerenciar Meus Livros", new Font("Arial", Font.BOLD, 14), Color.WHITE);
        JMenuItem cadastrarLivro = criarMenuItem("Cadastrar Livro");
        JMenuItem editarLivro = criarMenuItem("Editar Livro");
        JMenuItem listarLivro = criarMenuItem("Listar Livro");
        JMenuItem listarLivroDisponivel = criarMenuItem("Listar Livro Disponivel");
        manterLivro.add(cadastrarLivro);
        manterLivro.add(editarLivro);
        manterLivro.add(listarLivro);
        manterLivro.add(listarLivroDisponivel);

        JMenu emprestimo = criarMenu("Área de empréstimos", new Font("Arial", Font.BOLD, 14), Color.WHITE);
        JMenuItem emprestar = criarMenuItem("Cadastrar novo empréstimo de Livro");
        JMenuItem devolver = criarMenuItem("Devolver Livro");
        emprestimo.add(emprestar);
        emprestimo.add(devolver);

        JMenu manterUsuario = criarMenu("Gerenciar Usuários", new Font("Arial", Font.BOLD, 14), Color.WHITE);
        JMenuItem cadastrarUsuario = criarMenuItem("Cadastrar Usuario");
        JMenuItem editarUsuario = criarMenuItem("Editar Usuario");
        JMenuItem listarUsuario = criarMenuItem("Listar Usuario");
        manterUsuario.add(cadastrarUsuario);
        manterUsuario.add(editarUsuario);
        manterUsuario.add(listarUsuario);

        menuBar.add(emprestimo);
        menuBar.add(manterLivro);
        menuBar.add(manterUsuario);

        cadastrarUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CadastroUsuario(em);
            }
        });

        editarUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarUsuario();
            }
        });

        listarUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BuscarUsuario(em);
            }
        });

        cadastrarLivro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CadastroLivro(em);
            }
        });

        editarLivro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarLivro();
            }
        });

        listarLivro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BuscarLivro(em);
            }
        });

        listarLivroDisponivel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<LivroModel> livrosDisponiveis = livroController.buscarLivrosDisponiveis();
                if (livrosDisponiveis.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Nenhum livro disponível no momento.");
                } else {
                    StringBuilder mensagem = new StringBuilder("Livros Disponíveis:\n");
                    for (LivroModel livro : livrosDisponiveis) {
                        mensagem.append(livro.getTitulo()).append(" - ").append(livro.getQuantidadeDisponivel()).append(" exemplar(es)\n");
                    }
                    JOptionPane.showMessageDialog(null, mensagem.toString());
                }
            }
        });

        emprestar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CadastrarEmprestimo(em);
            }
        });

        devolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DevolverEmprestimo(em);
            }
        });
    }

    private void criarCardsLivros() {
        List<LivroModel> livrosPoucasUnidades = livroController.buscarLivrosPoucasUnidades();
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new GridBagLayout()); // Usar GridBagLayout para centralizar
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 10, 10, 10); // Espaçamento entre os cards
        gbc.anchor = GridBagConstraints.CENTER; // Centralizar

        for (LivroModel livro : livrosPoucasUnidades) {
            JPanel card = criarCardLivro(livro);
            cardsPanel.add(card, gbc);
        }

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        jPanelPrincipal.setLayout(new BorderLayout());
        jPanelPrincipal.add(scrollPane, BorderLayout.CENTER);
    }


    private JPanel criarCardLivro(LivroModel livro) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        card.setBackground(new Color(245, 245, 245));
        card.setPreferredSize(new Dimension(300, 180)); // Aumentar tamanho do card

        // Adicionar título
        JLabel titulo = new JLabel(livro.getTitulo(), JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT); // Centralizar
        card.add(titulo);
        card.add(Box.createVerticalStrut(5)); // Espaçamento entre elementos

        JLabel id = new JLabel("ID: " + livro.getId());
        id.setFont(new Font("Arial", Font.PLAIN, 14));
        id.setAlignmentX(Component.CENTER_ALIGNMENT); // Centralizar
        card.add(id);
        // Adicionar informações do livro
        JLabel autor = new JLabel("Autor: " + livro.getAutor());
        autor.setFont(new Font("Arial", Font.PLAIN, 14));
        autor.setAlignmentX(Component.CENTER_ALIGNMENT); // Centralizar
        card.add(autor);

        JLabel tema = new JLabel("Tema: " + livro.getTema());
        tema.setFont(new Font("Arial", Font.PLAIN, 14));
        tema.setAlignmentX(Component.CENTER_ALIGNMENT); // Centralizar
        card.add(tema);

        JLabel quantidade = new JLabel("Disponível: " + livro.getQuantidadeDisponivel());
        quantidade.setFont(new Font("Arial", Font.PLAIN, 14));
        quantidade.setAlignmentX(Component.CENTER_ALIGNMENT); // Centralizar
        card.add(quantidade);

        // Adicionar botão de ação
        JButton btnEmprestar = new JButton("Emprestar");
        btnEmprestar.setBackground(new Color(60, 179, 113));
        btnEmprestar.setForeground(Color.WHITE);
        btnEmprestar.setFocusPainted(false);
        btnEmprestar.setAlignmentX(Component.CENTER_ALIGNMENT); // Centralizar
        btnEmprestar.addActionListener(e -> {
            // Ação do botão
            JOptionPane.showMessageDialog(null, "Emprestar o livro: " + livro.getTitulo());
        });
        card.add(Box.createVerticalStrut(10)); // Espaçamento antes do botão
        card.add(btnEmprestar);

        return card;
    }


    // Método para criar menus estilizados
    private JMenu criarMenu(String titulo, Font fonte, Color cor) {
        JMenu menu = new JMenu(titulo);
        menu.setFont(fonte);
        menu.setForeground(cor);
        return menu;
    }

    // Método para criar itens de menu estilizados
    private JMenuItem criarMenuItem(String titulo) {
        JMenuItem menuItem = new JMenuItem(titulo);
        menuItem.setFont(new Font("Arial", Font.PLAIN, 14));
        menuItem.setBackground(Color.WHITE);
        return menuItem;
    }

    // Método para editar usuário
    private void editarUsuario() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("ID do Usuário:"));
            if (id > 0) {
                new EditarUsuario(em, id);
            } else {
                JOptionPane.showMessageDialog(this, "ID inválido!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID deve ser um número!");
        }
    }

    private void editarLivro() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("ID do Livro:"));
            if (id > 0) {
                new EditarLivro(em, id);
            } else {
                JOptionPane.showMessageDialog(this, "ID inválido!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID deve ser um número!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Principal();
            }
        });
    }





}

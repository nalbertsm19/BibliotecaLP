package View;

import Controller.EmprestimoController;
import Controller.LivroController;
import Controller.UsuarioController;
import Model.EmprestimoModel;
import Model.LivroModel;
import Model.UsuarioModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class CadastrarEmprestimo extends JFrame {
    private JComboBox<UsuarioModel> comboBoxUsuario;
    private JComboBox<LivroModel> comboBoxLivro;
    private JTextField formattedTextFieldDataEmprestimo;
    private JTextField formattedTextFieldDataDevolucao;
    private JCheckBox checkBoxDevolvido;
    private JButton salvarButton;
    private JButton cancelarButton;
    private JPanel panel1;
    private EmprestimoController emprestimoController;
    private UsuarioController usuarioController;
    private LivroController livroController;

    public CadastrarEmprestimo(EntityManager em) {
        this.setTitle("Cadastro de Empréstimo");
        this.setSize(600, 400); // Ajustar o tamanho da janela
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setContentPane(createPanel());
        this.setLocationRelativeTo(null); // Centralizar a janela na tela
        this.setVisible(true);

        // Inicializa os controladores
        this.emprestimoController = new EmprestimoController(em);
        this.usuarioController = new UsuarioController(em);
        this.livroController = new LivroController(em);

        // Carregar dados do banco de dados nos JComboBoxes
        carregarUsuarios();
        carregarLivros();

        // Adicionar ActionListeners
        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UsuarioModel usuarioSelecionado = (UsuarioModel) comboBoxUsuario.getSelectedItem();
                LivroModel livroSelecionado = (LivroModel) comboBoxLivro.getSelectedItem();

                // Verificação se os itens foram selecionados
                if (usuarioSelecionado == null || livroSelecionado == null) {
                    JOptionPane.showMessageDialog(null, "Selecione um usuário e um livro.");
                    return;
                }

                int usuarioId = usuarioSelecionado.getId();
                int livroId = livroSelecionado.getId();

                // Verificação se o usuário já pegou 5 livros emprestados
                if (usuarioController.contarLivrosEmprestados(usuarioId) >= 5) {
                    JOptionPane.showMessageDialog(null, "O usuário já pegou 5 livros emprestados!");
                    return;
                }

                // Verificando se o livro está disponível
                LivroModel livro = livroController.buscarPorId(livroId);
                if (livro == null || livro.getQuantidadeDisponivel() <= 0) {
                    JOptionPane.showMessageDialog(null, "Não há exemplares disponíveis desse livro.");
                    return;
                }

                // Validação das datas
                String dataEmprestimoStr = formattedTextFieldDataEmprestimo.getText();
                String dataDevolucaoStr = formattedTextFieldDataDevolucao.getText();
                Date dataEmprestimo = null;
                Date dataDevolucao = null;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);

                try {
                    if (!dataEmprestimoStr.isEmpty()) {
                        dataEmprestimo = new Date(sdf.parse(dataEmprestimoStr).getTime());
                    }
                    if (!dataDevolucaoStr.isEmpty()) {
                        dataDevolucao = new Date(sdf.parse(dataDevolucaoStr).getTime());
                    }
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null, "Formato de data inválido. Use o formato YYYY-MM-DD.");
                    return;
                }

                // Verifica se a data de devolução é posterior à data de empréstimo
                if (dataEmprestimo != null && dataDevolucao != null && dataDevolucao.before(dataEmprestimo)) {
                    JOptionPane.showMessageDialog(null, "A data de devolução deve ser posterior à data de empréstimo.");
                    return;
                }

                // Verifica se o prazo de empréstimo excede 14 dias
                if (dataEmprestimo != null && dataDevolucao != null) {
                    long diferencaMillis = dataDevolucao.getTime() - dataEmprestimo.getTime();
                    long diferencaDias = diferencaMillis / (1000 * 60 * 60 * 24); // Converte milissegundos para dias

                    if (diferencaDias > 14) {
                        JOptionPane.showMessageDialog(null, "Atenção: O prazo de empréstimo excede 14 dias. O livro estará atrasado!");
                    }
                }

                boolean devolvido = checkBoxDevolvido.isSelected();

                // Criar objeto Emprestimo
                EmprestimoModel emprestimo = new EmprestimoModel();
                emprestimo.setUsuario(usuarioSelecionado);
                emprestimo.setLivro(livroSelecionado);
                emprestimo.setDataEmprestimo(dataEmprestimo);
                emprestimo.setDataDevolucao(dataDevolucao);
                emprestimo.setDevolvido(devolvido);

                // Salva o empréstimo no banco de dados
                try {
                    emprestimoController.salvar(emprestimo);
                    livroController.atualizarQuantidadeDisponivel(livroId, livro.getQuantidadeDisponivel() - 1);

                    JOptionPane.showMessageDialog(null, "Empréstimo realizado com sucesso!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao salvar empréstimo: " + ex.getMessage());
                }

                dispose(); // Fecha a janela
            }
        });

        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha a janela
            }
        });
    }

    private void carregarUsuarios() {
        List<UsuarioModel> usuarios = usuarioController.listarTodos();
        for (UsuarioModel usuario : usuarios) {
            comboBoxUsuario.addItem(usuario);
        }
    }

    private void carregarLivros() {
        List<LivroModel> livros = livroController.listarTodos();
        for (LivroModel livro : livros) {
            comboBoxLivro.addItem(livro);
        }
    }

    private JPanel createPanel() {
        panel1 = new JPanel(new BorderLayout(10, 10));
        panel1.setBackground(new Color(245, 245, 245));
        panel1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Cadastro de Empréstimo");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel1.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;  // Garantir que os campos de texto ocupem todo o espaço disponível
        gbc.anchor = GridBagConstraints.WEST;  // Alinhar à esquerda

        // Inicialização dos componentes
        comboBoxUsuario = new JComboBox<>();
        comboBoxLivro = new JComboBox<>();
        formattedTextFieldDataEmprestimo = new JTextField(20);
        formattedTextFieldDataDevolucao = new JTextField(20);
        checkBoxDevolvido = new JCheckBox();
        salvarButton = new JButton("Salvar");
        cancelarButton = new JButton("Cancelar");

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Usuário:"), gbc);
        gbc.gridx = 1;
        formPanel.add(comboBoxUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Livro:"), gbc);
        gbc.gridx = 1;
        formPanel.add(comboBoxLivro, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Data de Empréstimo (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        formPanel.add(formattedTextFieldDataEmprestimo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Data de Devolução (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        formPanel.add(formattedTextFieldDataDevolucao, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Devolvido:"), gbc);
        gbc.gridx = 1;
        formPanel.add(checkBoxDevolvido, gbc);

        salvarButton.setFont(new Font("Arial", Font.BOLD, 14));
        salvarButton.setBackground(new Color(60, 179, 113));
        salvarButton.setForeground(Color.WHITE);
        salvarButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(salvarButton, gbc);

        cancelarButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelarButton.setBackground(new Color(255, 69, 0));
        cancelarButton.setForeground(Color.WHITE);
        cancelarButton.setFocusPainted(false);
        gbc.gridy = 6;
        formPanel.add(cancelarButton, gbc);

        panel1.add(formPanel, BorderLayout.CENTER);

        return panel1;
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("crudHibernatePU");
        EntityManager em = emf.createEntityManager();
        SwingUtilities.invokeLater(() -> new CadastrarEmprestimo(em));
    }
}

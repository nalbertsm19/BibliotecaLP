package View;

import Controller.EmprestimoController;
import Model.EmprestimoModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DevolverEmprestimo extends JFrame {
    private JPanel panelPrincipal;
    private JComboBox<EmprestimoModel> comboBoxEmprestimos;
    private JTextField textFieldDataDevolucao;
    private JCheckBox checkBoxDevolvido;
    private JButton devolverButton;
    private EmprestimoController emprestimoController;
    private JPanel panel1;

    public DevolverEmprestimo(EntityManager em) {
        // Aplicar o Nimbus Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Usar o Look and Feel padrão
        }

        // Inicializar o controlador de empréstimos antes da configuração da interface gráfica
        this.emprestimoController = new EmprestimoController(em);

        // Configuração da interface gráfica
        createUIComponents();
        this.setTitle("Devolução de Livro");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Restante da configuração da interface gráfica
        this.setContentPane(panelPrincipal);
        this.setSize(500, 300); // Ajustar o tamanho da janela
        this.setLocationRelativeTo(null); // Centralizar a janela na tela
        this.setVisible(true);
    }

    private void createUIComponents() {
        // Inicializar os componentes personalizados aqui
        comboBoxEmprestimos = new JComboBox<>();
        comboBoxEmprestimos.setPreferredSize(new Dimension(250, 30)); // Aumentar o tamanho do JComboBox
        carregarEmprestimos();

        textFieldDataDevolucao = new JTextField("dd-MM-yyyy"); // Placeholder
        textFieldDataDevolucao.setForeground(Color.GRAY);
        textFieldDataDevolucao.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textFieldDataDevolucao.getText().equals("dd-MM-yyyy")) {
                    textFieldDataDevolucao.setText("");
                    textFieldDataDevolucao.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textFieldDataDevolucao.getText().isEmpty()) {
                    textFieldDataDevolucao.setForeground(Color.GRAY);
                    textFieldDataDevolucao.setText("dd-MM-yyyy");
                }
            }
        });

        checkBoxDevolvido = new JCheckBox();
        devolverButton = new JButton("Devolver");
        devolverButton.setBackground(new Color(60, 179, 113)); // Cor de fundo do botão
        devolverButton.setForeground(Color.WHITE); // Cor do texto do botão
        devolverButton.setFocusPainted(false); // Remover foco pintado

        panel1 = new JPanel(new GridBagLayout()); // Usar GridBagLayout para melhor controle de layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Adicionar margens entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel1.add(new JLabel("ID do Empréstimo:"), gbc);

        gbc.gridx = 1;
        panel1.add(comboBoxEmprestimos, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel1.add(new JLabel("Data de Devolução (dd-MM-yyyy):"), gbc); // Ajustar o formato da data esperado

        gbc.gridx = 1;
        panel1.add(textFieldDataDevolucao, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel1.add(new JLabel("Devolvido:"), gbc);

        gbc.gridx = 1;
        panel1.add(checkBoxDevolvido, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Centralizar o botão
        panel1.add(devolverButton, gbc);

        // Configuração do painel principal
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margens ao redor do painel principal
        panelPrincipal.add(panel1, BorderLayout.CENTER); // Adicionar panel1 ao panelPrincipal

        // Adiciona ActionListener ao botão de devolução
        devolverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Obtém o empréstimo selecionado
                    EmprestimoModel emprestimoSelecionado = (EmprestimoModel) comboBoxEmprestimos.getSelectedItem();

                    if (emprestimoSelecionado == null) {
                        JOptionPane.showMessageDialog(null, "Por favor, selecione um empréstimo!");
                        return;
                    }

                    int emprestimoId = emprestimoSelecionado.getId();

                    // Busca o empréstimo no banco de dados
                    EmprestimoModel emprestimo = emprestimoController.buscarEmprestimoPorId(emprestimoId);

                    if (emprestimo == null) {
                        JOptionPane.showMessageDialog(null, "Empréstimo não encontrado!");
                        return;
                    }

                    // Verifica se o livro já foi devolvido
                    if (emprestimo.isDevolvido()) {
                        JOptionPane.showMessageDialog(null, "Este livro já foi devolvido.");
                        return;
                    }

                    // Obtém a data de devolução inserida pelo usuário
                    String dataDevolucaoTexto = textFieldDataDevolucao.getText();
                    if (dataDevolucaoTexto.isEmpty() || dataDevolucaoTexto.equals("dd-MM-yyyy")) {
                        JOptionPane.showMessageDialog(null, "Por favor, insira a data de devolução.");
                        return;
                    }

                    // Converte a string da data para o formato Date
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); // Ajustar o formato da data para o esperado
                    Date dataDevolucao = sdf.parse(dataDevolucaoTexto);

                    // Obtém a data de devolução prevista
                    Date dataDevolucaoPrevista = emprestimo.getDataDevolucao();

                    // Calcula a diferença em dias
                    long diferencaMillis = dataDevolucao.getTime() - dataDevolucaoPrevista.getTime();
                    long diasAtraso = diferencaMillis / (1000 * 60 * 60 * 24); // Converte milissegundos para dias

                    // Calcula a taxa de atraso (R$ 1,00 por dia de atraso)
                    double taxaPorDia = 1.0; // Valor da taxa por dia de atraso
                    double multaTotal = 0.0;

                    if (diasAtraso > 0) {
                        multaTotal = diasAtraso * taxaPorDia;
                        JOptionPane.showMessageDialog(null, "Atenção: O livro foi devolvido com " + diasAtraso + " dias de atraso.\nMulta total: R$ " + multaTotal);
                    }

                    // Atualiza o status do empréstimo como devolvido
                    emprestimoController.devolverEmprestimo(emprestimoId);

                    // Marca o checkbox "Devolvido" como verdadeiro (livro foi devolvido)
                    checkBoxDevolvido.setSelected(true);

                    JOptionPane.showMessageDialog(null, "Livro devolvido com sucesso!");
                    dispose();  // Fecha o formulário após devolução

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao devolver o livro: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
    }

    private void carregarEmprestimos() {
        try {
            List<EmprestimoModel> emprestimos = emprestimoController.listarEmprestimos();
            for (EmprestimoModel emprestimo : emprestimos) {
                comboBoxEmprestimos.addItem(emprestimo);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar os empréstimos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("crudHibernatePU");
        EntityManager em = emf.createEntityManager();
        SwingUtilities.invokeLater(() -> new DevolverEmprestimo(em));
    }
}

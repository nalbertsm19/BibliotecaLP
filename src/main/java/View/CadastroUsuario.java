package View;

import Controller.UsuarioController;
import Model.UsuarioModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class CadastroUsuario extends JFrame {
    private JPanel jpanelUsuario;
    private JPanel panelUsuario;
    private JTextField textFieldNome;
    private JTextField textFieldEmail;
    private JTextField textFieldTelefone;
    private JTextField textFieldSexo;
    private JButton buttonCadastrar;
    private JLabel labelNome;
    private JLabel labelEmail;
    private JLabel labelTelefone;
    private JLabel labelSexo;
    private UsuarioController usuarioController;

    public CadastroUsuario(EntityManager entityManager) {
        this.setTitle("Sistema de Gestão de Biblioteca");
        this.setSize(400, 400); // Aumentar o tamanho da janela
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setContentPane(createPanel()); // Usar o novo método createPanel()
        this.setLocationRelativeTo(null); // Centralizar a janela na tela
        this.setVisible(true);

        usuarioController = new UsuarioController(entityManager); // Inicialize o usuarioController com EntityManager
    }

    private JPanel createPanel() {
        jpanelUsuario = new JPanel();
        jpanelUsuario.setBackground(new Color(245, 245, 245));
        jpanelUsuario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        jpanelUsuario.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Cadastro de Usuário");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jpanelUsuario.add(titleLabel, BorderLayout.NORTH);

        panelUsuario = new JPanel();
        panelUsuario.setLayout(new GridBagLayout());
        panelUsuario.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        labelNome = createLabel("Nome:");
        textFieldNome = createTextField();

        labelEmail = createLabel("Email:");
        textFieldEmail = createTextField();

        labelTelefone = createLabel("Telefone:");
        textFieldTelefone = createTextField();

        labelSexo = createLabel("Sexo:");
        textFieldSexo = createTextField();

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelUsuario.add(labelNome, gbc);

        gbc.gridx = 1;
        panelUsuario.add(textFieldNome, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelUsuario.add(labelEmail, gbc);

        gbc.gridx = 1;
        panelUsuario.add(textFieldEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelUsuario.add(labelTelefone, gbc);

        gbc.gridx = 1;
        panelUsuario.add(textFieldTelefone, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelUsuario.add(labelSexo, gbc);

        gbc.gridx = 1;
        panelUsuario.add(textFieldSexo, gbc);

        buttonCadastrar = new JButton("Cadastrar");
        buttonCadastrar.setFont(new Font("Arial", Font.BOLD, 14));
        buttonCadastrar.setBackground(new Color(60, 179, 113));
        buttonCadastrar.setForeground(Color.WHITE);
        buttonCadastrar.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelUsuario.add(buttonCadastrar, gbc);

        jpanelUsuario.add(panelUsuario, BorderLayout.CENTER);

        buttonCadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = textFieldNome.getText();
                String email = textFieldEmail.getText();
                String telefone = textFieldTelefone.getText();
                UsuarioModel usuario = new UsuarioModel(nome, email, telefone, textFieldSexo.getText()); // Inicialize o usuario

                try {
                    usuarioController.salvar(usuario); // Salve o usuário
                    JOptionPane.showMessageDialog(null, "Usuário cadastrado:\nNome: " + nome + "\nEmail: " + email + "\nTelefone: " + telefone);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                dispose();
            }
        });

        return jpanelUsuario;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                textField.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return textField;
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("crudHibernatePU");
        EntityManager em = emf.createEntityManager();
        new CadastroUsuario(em);
    }
}

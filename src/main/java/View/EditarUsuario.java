package View;

import Controller.UsuarioController;
import Model.UsuarioModel;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditarUsuario extends JFrame {
    private JTextField textFieldNome;
    private JTextField textFieldSexo;
    private JTextField textFieldCelular;
    private JTextField textFieldEmail;
    private JButton salvarButton;
    private JPanel panel1;
    private UsuarioController usuarioController;
    private int usuarioId;

    public EditarUsuario(EntityManager em, int id) {
        this.setTitle("Editar Usuário");
        this.setSize(500, 400); // Tamanho adequado para visualização
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setContentPane(createPanel());
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        usuarioController = new UsuarioController(em);
        usuarioId = id;

        carregarDadosUsuario();

        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nome = textFieldNome.getText();
                    String sexo = textFieldSexo.getText();
                    String celular = textFieldCelular.getText();
                    String email = textFieldEmail.getText();

                    usuarioController.atualizarUsuario(usuarioId, nome, sexo, celular, email);
                    JOptionPane.showMessageDialog(null, "Usuário atualizado com sucesso!");
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao atualizar usuário: " + ex.getMessage());
                }
            }
        });
    }

    private void carregarDadosUsuario() {
        try {
            UsuarioModel usuario = usuarioController.buscarUsuarioPorId(usuarioId);
            if (usuario != null) {
                textFieldNome.setText(usuario.getNome());
                textFieldSexo.setText(usuario.getSexo());
                textFieldCelular.setText(usuario.getCelular());
                textFieldEmail.setText(usuario.getEmail());
            } else {
                JOptionPane.showMessageDialog(this, "Usuário não encontrado!");
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do usuário: " + e.getMessage());
            dispose();
        }
    }

    private JPanel createPanel() {
        panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel1.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel1.add(createLabel("Nome:"), gbc);
        textFieldNome = createTextField();
        gbc.gridx = 1;
        panel1.add(textFieldNome, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel1.add(createLabel("Sexo:"), gbc);
        textFieldSexo = createTextField();
        gbc.gridx = 1;
        panel1.add(textFieldSexo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel1.add(createLabel("Celular:"), gbc);
        textFieldCelular = createTextField();
        gbc.gridx = 1;
        panel1.add(textFieldCelular, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel1.add(createLabel("Email:"), gbc);
        textFieldEmail = createTextField();
        gbc.gridx = 1;
        panel1.add(textFieldEmail, gbc);

        salvarButton = new JButton("Salvar");
        salvarButton.setFont(new Font("Arial", Font.BOLD, 14));
        salvarButton.setBackground(new Color(60, 179, 113));
        salvarButton.setForeground(Color.WHITE);
        salvarButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel1.add(salvarButton, gbc);

        return panel1;
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
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        textField.setPreferredSize(new Dimension(200, 30));
        textField.setBackground(Color.WHITE);

        textField.setOpaque(false);
        textField.setBackground(new Color(255, 255, 255));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        return textField;
    }
}

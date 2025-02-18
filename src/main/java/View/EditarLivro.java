package View;

import Controller.LivroController;
import Model.LivroModel;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditarLivro extends JFrame {
    private JTextField textFieldTitulo;
    private JTextField textFieldTema;
    private JTextField textFieldAutor;
    private JTextField textFieldISBN;
    private JTextField textFieldDataPublicacao;
    private JTextField textFieldQuantidadeDisponivel;
    private JButton salvarButton;
    private JPanel panel1;
    private LivroController livroController;
    private int livroId;

    public EditarLivro(EntityManager em, int id) {
        this.setTitle("Editar Livro");
        this.setSize(500, 400);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setContentPane(createPanel());
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        livroController = new LivroController(em);
        livroId = id;

        carregarDadosLivro();

        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String titulo = textFieldTitulo.getText();
                    String tema = textFieldTema.getText();
                    String autor = textFieldAutor.getText();
                    String isbn = textFieldISBN.getText();
                    String dataPublicacaoString = textFieldDataPublicacao.getText();
                    Integer quantidadeDisponivel = Integer.valueOf(textFieldQuantidadeDisponivel.getText());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date dataPublicacao = sdf.parse(dataPublicacaoString);

                    livroController.atualizarLivro(livroId, titulo, tema, autor, isbn, dataPublicacao, quantidadeDisponivel);
                    JOptionPane.showMessageDialog(null, "Livro atualizado com sucesso!");
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao atualizar livro: " + ex.getMessage());
                }
            }
        });
    }

    private void carregarDadosLivro() {
        try {
            LivroModel livro = livroController.buscarPorId(livroId);
            if (livro != null) {
                textFieldTitulo.setText(livro.getTitulo());
                textFieldTema.setText(livro.getTema());
                textFieldAutor.setText(livro.getAutor());
                textFieldISBN.setText(livro.getIsbn());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                textFieldDataPublicacao.setText(sdf.format(livro.getDataPublicacao()));
                textFieldQuantidadeDisponivel.setText(String.valueOf(livro.getQuantidadeDisponivel()));
            } else {
                JOptionPane.showMessageDialog(this, "Livro não encontrado!");
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do livro: " + e.getMessage());
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
        panel1.add(createLabel("Título:"), gbc);
        textFieldTitulo = createTextField();
        gbc.gridx = 1;
        panel1.add(textFieldTitulo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel1.add(createLabel("Tema:"), gbc);
        textFieldTema = createTextField();
        gbc.gridx = 1;
        panel1.add(textFieldTema, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel1.add(createLabel("Autor:"), gbc);
        textFieldAutor = createTextField();
        gbc.gridx = 1;
        panel1.add(textFieldAutor, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel1.add(createLabel("ISBN:"), gbc);
        textFieldISBN = createTextField();
        gbc.gridx = 1;
        panel1.add(textFieldISBN, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel1.add(createLabel("Data de Publicação:"), gbc);
        textFieldDataPublicacao = createTextField();
        gbc.gridx = 1;
        panel1.add(textFieldDataPublicacao, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel1.add(createLabel("Quantidade Disponível:"), gbc);
        textFieldQuantidadeDisponivel = createTextField();
        gbc.gridx = 1;
        panel1.add(textFieldQuantidadeDisponivel, gbc);

        salvarButton = new JButton("Salvar");
        salvarButton.setFont(new Font("Arial", Font.BOLD, 14));
        salvarButton.setBackground(new Color(60, 179, 113));
        salvarButton.setForeground(Color.WHITE);
        salvarButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 6;
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

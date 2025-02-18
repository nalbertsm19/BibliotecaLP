package View;

import javax.swing.*;
import Controller.LivroController;
import Model.LivroModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.SQLException;

import static java.lang.Integer.parseInt;

public class CadastroLivro extends JFrame {
    private JPanel cadLivro;
    private JPanel CadLivro;  // Definindo o campo CadLivro, conforme esperado pelo formulário
    private JTextField formattedTextFieldTitulo;
    private JTextField formattedTextFieldTema;
    private JTextField formattedTextFieldAutor;
    private JTextField formattedTextFieldISBN;
    private JTextField formattedTextFieldDataPublicacao;
    private JTextField formattedTextFieldQtdeDisponivel;
    private JButton cadastrarButton;
    private LivroController livroController;

    public CadastroLivro(EntityManager em) {
        this.setTitle("Sistema de Gestão de Biblioteca - Cadastro de Livro");
        this.setSize(600, 400); // Ajustar o tamanho da janela
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setContentPane(createPanel()); // Usar o novo método createPanel()
        this.setLocationRelativeTo(null); // Centralizar a janela na tela
        this.setVisible(true);

        livroController = new LivroController(em); // Inicializa o LivroController com EntityManager
    }

    private JPanel createPanel() {
        cadLivro = new JPanel(new BorderLayout(10, 10));
        cadLivro.setBackground(new Color(245, 245, 245));
        cadLivro.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Cadastro de Livro");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cadLivro.add(titleLabel, BorderLayout.NORTH);

        CadLivro = new JPanel();
        CadLivro.setLayout(new GridBagLayout());
        CadLivro.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;  // Garantir que os campos de texto ocupem todo o espaço disponível
        gbc.anchor = GridBagConstraints.WEST;  // Alinhar à esquerda

        JLabel labelTitulo = createLabel("Título:");
        formattedTextFieldTitulo = createTextField();

        JLabel labelTema = createLabel("Tema:");
        formattedTextFieldTema = createTextField();

        JLabel labelAutor = createLabel("Autor:");
        formattedTextFieldAutor = createTextField();

        JLabel labelISBN = createLabel("ISBN:");
        formattedTextFieldISBN = createTextField();

        JLabel labelDataPublicacao = createLabel("Data de Publicação (yyyy-MM-dd):");
        formattedTextFieldDataPublicacao = createTextField();

        JLabel labelQtdeDisponivel = createLabel("Quantidade Disponível:");
        formattedTextFieldQtdeDisponivel = createTextField();

        addComponentsToPanel(CadLivro, labelTitulo, formattedTextFieldTitulo, gbc, 0);
        addComponentsToPanel(CadLivro, labelTema, formattedTextFieldTema, gbc, 1);
        addComponentsToPanel(CadLivro, labelAutor, formattedTextFieldAutor, gbc, 2);
        addComponentsToPanel(CadLivro, labelISBN, formattedTextFieldISBN, gbc, 3);
        addComponentsToPanel(CadLivro, labelDataPublicacao, formattedTextFieldDataPublicacao, gbc, 4);
        addComponentsToPanel(CadLivro, labelQtdeDisponivel, formattedTextFieldQtdeDisponivel, gbc, 5);

        cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.setFont(new Font("Arial", Font.BOLD, 14));
        cadastrarButton.setBackground(new Color(60, 179, 113));
        cadastrarButton.setForeground(Color.WHITE);
        cadastrarButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        CadLivro.add(cadastrarButton, gbc);

        cadLivro.add(CadLivro, BorderLayout.CENTER);

        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titulo = formattedTextFieldTitulo.getText();
                String tema = formattedTextFieldTema.getText();
                String autor = formattedTextFieldAutor.getText();
                String isbn = formattedTextFieldISBN.getText();

                // Valida a data inserida
                String dataString = formattedTextFieldDataPublicacao.getText();
                Date dataPublicacao = null;
                try {
                    // Verifica se a data está no formato correto (yyyy-MM-dd)
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdf.setLenient(false);
                    java.util.Date parsedDate = sdf.parse(dataString);
                    dataPublicacao = new Date(parsedDate.getTime());
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null, "Formato de data inválido. Use o formato YYYY-MM-DD.");
                    return; // Interrompe o cadastro caso a data seja inválida
                }

                Integer quantidadeDisponivel = parseInt(formattedTextFieldQtdeDisponivel.getText());

                // Cria e preenche o modelo de livro
                LivroModel livro = new LivroModel();
                livro.setTitulo(titulo);
                livro.setTema(tema);
                livro.setAutor(autor);
                livro.setIsbn(isbn);
                livro.setDataPublicacao(dataPublicacao);
                livro.setQuantidadeDisponivel(quantidadeDisponivel);

                try {
                    // Salva o livro no banco de dados
                    livroController.salvar(livro);
                    JOptionPane.showMessageDialog(null, "Livro cadastrado:\nTítulo: " + titulo + "\nTema: " + tema + "\nAutor: " + autor + "\nISBN: " + isbn + "\nData de Publicação: " + dataPublicacao + "\nQuantidade Disponível: " + quantidadeDisponivel);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar o livro: " + ex.getMessage());
                }
                dispose(); // Fecha a janela de cadastro
            }
        });

        return cadLivro;
    }

    private void addComponentsToPanel(JPanel panel, JLabel label, JTextField textField, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);
        gbc.gridx = 1;
        panel.add(textField, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(200, 30)); // Ajustar o tamanho do JTextField
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                textField.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return textField;
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("crudHibernatePU");
        EntityManager em = emf.createEntityManager();
        new CadastroLivro(em);
    }
}

package View;

import Controller.EmprestimoController;
import Controller.LivroController;
import Model.LivroModel;
import Repository.EmprestimoRepository;
import Repository.LivroRepository;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.util.List;

public class BuscarLivro extends JFrame {
    private JPanel panelPrincipal;
    private JButton buttonBuscar;
    private JTextField textFieldBusca;
    private JTable tableBuscaLivro;
    private JButton removerButton;
    private JScrollPane scrollPaneLIvro;

    private LivroRepository LivroRepo;
    private LivroController livroController;

    public BuscarLivro(EntityManager em) {
        this.setTitle("Sistema de Gestão de Biblioteca");

        // Certifique-se de que os parâmetros corretos sejam passados para o construtor do LivroController
        EmprestimoRepository emprestimoRepository = new EmprestimoRepository(em);
        EmprestimoController emprestimoController = new EmprestimoController(em);
        this.livroController = new LivroController(emprestimoRepository, emprestimoController, em);

        LivroModeloDeTabela livroModeloDeTabela = new LivroModeloDeTabela(em);
        tableBuscaLivro.setModel(livroModeloDeTabela);
        tableBuscaLivro.setAutoCreateRowSorter(true);
        tableBuscaLivro.setRowHeight(30);
        tableBuscaLivro.setIntercellSpacing(new Dimension(10, 10));
        tableBuscaLivro.setBackground(new Color(245, 245, 245));
        tableBuscaLivro.setGridColor(new Color(200, 200, 200));

        // Estilizando cabeçalhos da tabela
        JTableHeader header = tableBuscaLivro.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(60, 179, 113));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40)); // Ajuste da altura do cabeçalho

        // Estilizando células da tabela
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        cellRenderer.setFont(new Font("Arial", Font.PLAIN, 14));
        cellRenderer.setBackground(new Color(255, 255, 255)); // Fundo branco para células
        cellRenderer.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 200, 200))); // Bordas cinzas
        tableBuscaLivro.setDefaultRenderer(Object.class, cellRenderer);

        // Estilizando painel principal e botões
        panelPrincipal.setBackground(new Color(230, 230, 250)); // Fundo lilás suave
        buttonBuscar.setFont(new Font("Arial", Font.BOLD, 14));
        buttonBuscar.setBackground(new Color(60, 179, 113));
        buttonBuscar.setForeground(Color.WHITE);
        buttonBuscar.setFocusPainted(false);

        removerButton.setFont(new Font("Arial", Font.BOLD, 14));
        removerButton.setBackground(new Color(255, 69, 0));
        removerButton.setForeground(Color.WHITE);
        removerButton.setFocusPainted(false);

        // Estilizando campos de texto
        textFieldBusca.setFont(new Font("Arial", Font.PLAIN, 14));
        textFieldBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        this.setContentPane(panelPrincipal);
        this.setSize(800, 600);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null); // Centralizar a janela na tela
        this.setVisible(true);

        removerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int linhaSelecionada = tableBuscaLivro.getSelectedRow();
                if (linhaSelecionada != -1) {
                    Long idLivroSelecionado = Long.parseLong(tableBuscaLivro.getValueAt(linhaSelecionada, 0).toString());
                    try {
                        livroController.deletarLivro(idLivroSelecionado.intValue());
                        livroModeloDeTabela.atualizarTabela();
                        JOptionPane.showMessageDialog(null, "Livro removido com sucesso!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Erro ao remover livro: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione o registro que deseja remover.");
                }
            }
        });

        buttonBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String buscaId = textFieldBusca.getText();
                if (!buscaId.isEmpty()) {
                    try {
                        int idLivro = Integer.parseInt(buscaId);
                        LivroModel livro = livroController.buscarPorId(idLivro);
                        if (livro != null) {
                            livroModeloDeTabela.atualizarTabelaComLivro(livro);
                        } else {
                            JOptionPane.showMessageDialog(null, "Livro não encontrado!");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "ID inválido!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Digite o ID do livro para buscar.");
                }
            }
        });
    }

    private static class LivroModeloDeTabela extends AbstractTableModel {
        private final LivroRepository livroRepository;
        private final String[] COLUMNS = new String[]{"Id", "Título", "Tema", "Autor", "ISBN", "Data de Publicação", "Quantidade Disponível"};
        private List<LivroModel> listaDeLivros;

        public LivroModeloDeTabela(EntityManager em) {
            this.livroRepository = new LivroRepository(em);
            this.listaDeLivros = livroRepository.listarTodos();
        }

        @Override
        public int getRowCount() {
            return listaDeLivros.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return switch (columnIndex) {
                case 0 -> listaDeLivros.get(rowIndex).getId();
                case 1 -> listaDeLivros.get(rowIndex).getTitulo();
                case 2 -> listaDeLivros.get(rowIndex).getTema();
                case 3 -> listaDeLivros.get(rowIndex).getAutor();
                case 4 -> listaDeLivros.get(rowIndex).getIsbn();
                case 5 -> listaDeLivros.get(rowIndex).getDataPublicacao();
                case 6 -> listaDeLivros.get(rowIndex).getQuantidadeDisponivel();
                default -> "-";
            };
        }

        @Override
        public String getColumnName(int columnIndex) {
            return COLUMNS[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (getValueAt(0, columnIndex) != null) {
                return getValueAt(0, columnIndex).getClass();
            } else {
                return Object.class;
            }
        }

        public void atualizarTabela() {
            this.listaDeLivros = livroRepository.listarTodos();
            fireTableDataChanged();
        }

        public void atualizarTabelaComLivro(LivroModel livro) {
            this.listaDeLivros = List.of(livro);
            fireTableDataChanged();
        }
    }
}

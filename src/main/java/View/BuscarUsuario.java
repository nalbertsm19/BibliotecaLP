package View;

import Controller.UsuarioController;
import Model.UsuarioModel;
import Repository.UsuarioRepository;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.util.List;

public class BuscarUsuario extends JFrame {
    private JPanel panelPrincipal;
    private JButton buttonBuscar;
    private JTextField textFieldBusca;
    private JTable tableBuscaUsuario;
    private JScrollPane scrollPaneUsuario;
    private JButton removerButton;

    private UsuarioController usuarioController;

    public BuscarUsuario(EntityManager em) {
        this.setTitle("Sistema de Gestão de Biblioteca");
        this.usuarioController = new UsuarioController(em);
        UsuarioModeloDeTabela usuarioModeloDeTabela = new UsuarioModeloDeTabela(em);
        tableBuscaUsuario.setModel(usuarioModeloDeTabela);
        tableBuscaUsuario.setAutoCreateRowSorter(true);
        tableBuscaUsuario.setRowHeight(30);
        tableBuscaUsuario.setIntercellSpacing(new Dimension(10, 10));
        tableBuscaUsuario.setBackground(new Color(245, 245, 245));
        tableBuscaUsuario.setGridColor(new Color(200, 200, 200));

        // Estilizando cabeçalhos da tabela
        JTableHeader header = tableBuscaUsuario.getTableHeader();
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
        tableBuscaUsuario.setDefaultRenderer(Object.class, cellRenderer);

        this.setContentPane(panelPrincipal);
        this.getContentPane().setBackground(new Color(230, 230, 250)); // Fundo lilás suave
        this.setSize(800, 600);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null); // Centralizar a janela na tela
        this.setVisible(true);

        removerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int linhaSelecionada = tableBuscaUsuario.getSelectedRow();
                if (linhaSelecionada != -1) {
                    Long idUsuarioSelecionado = Long.parseLong(tableBuscaUsuario.getValueAt(linhaSelecionada, 0).toString());
                    try {
                        usuarioController.deletarUsuario(idUsuarioSelecionado.intValue());
                        usuarioModeloDeTabela.atualizarTabela();
                        JOptionPane.showMessageDialog(null, "Usuário removido com sucesso!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Erro ao remover usuário: " + ex.getMessage());
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
                        int idUsuario = Integer.parseInt(buscaId);
                        UsuarioModel usuario = usuarioController.buscarUsuarioPorId(idUsuario);
                        if (usuario != null) {
                            usuarioModeloDeTabela.atualizarTabelaComUsuario(usuario);
                        } else {
                            JOptionPane.showMessageDialog(null, "Usuário não encontrado!");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "ID inválido!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Digite o ID do usuário para buscar.");
                }
            }
        });
    }

    private static class UsuarioModeloDeTabela extends AbstractTableModel {
        private final UsuarioRepository usuarioRepository;
        private final String[] COLUMNS = new String[]{"Id", "Nome", "Telefone", "Email", "Sexo"};
        private List<UsuarioModel> listaDeUsuarios;

        public UsuarioModeloDeTabela(EntityManager em) {
            this.usuarioRepository = new UsuarioRepository(em);
            this.listaDeUsuarios = usuarioRepository.listarTodos();
        }

        @Override
        public int getRowCount() {
            return listaDeUsuarios.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return switch (columnIndex) {
                case 0 -> listaDeUsuarios.get(rowIndex).getId();
                case 1 -> listaDeUsuarios.get(rowIndex).getNome();
                case 2 -> listaDeUsuarios.get(rowIndex).getCelular();
                case 3 -> listaDeUsuarios.get(rowIndex).getEmail();
                case 4 -> listaDeUsuarios.get(rowIndex).getSexo();
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
            this.listaDeUsuarios = usuarioRepository.listarTodos();
            fireTableDataChanged();
        }

        public void atualizarTabelaComUsuario(UsuarioModel usuario) {
            this.listaDeUsuarios = List.of(usuario);
            fireTableDataChanged();
        }
    }
}

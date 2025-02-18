package Model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class UsuarioModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nome;
    private String sexo;
    private String celular;
    private String email;

    @OneToMany(mappedBy = "usuario")
    private List<EmprestimoModel> emprestimos;

    // Construtor
    public UsuarioModel() {
    }
    @Override
    public String toString() {
        return this.getNome(); // ou o atributo correto que armazena o nome do usuário
    }


    public UsuarioModel(String nome, String sexo, String celular, String email) {
        this.nome = nome;
        this.sexo = sexo;
        this.celular = celular;
        this.email = email;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<EmprestimoModel> getEmprestimos() {
        return emprestimos;
    }

    public void setEmprestimos(List<EmprestimoModel> emprestimos) {
        this.emprestimos = emprestimos;
    }
}

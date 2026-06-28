package classes;

// Representa um administrador do sistema, com nome, email, senha e nível de permissão
public class Admin {

    // Define os três níveis de acesso disponíveis no sistema
    public enum Permissao {
        GERENTE("Gerente"),    // acesso total: relatórios, audit, guardar dados
        OPERADOR("Operador"),  // pode registar carros, clientes e devoluções
        CONSULTA("Consulta");  // só pode ver listas, sem fazer alterações

        private final String descricao;

        Permissao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    private String nome;
    private String email;
    private String senha;
    private Permissao permissao;

    public Admin(String nome, String email, String senha, Permissao permissao) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.permissao = permissao;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public Permissao getPermissao() { return permissao; }

    // Verifica se a senha fornecida corresponde à senha guardada
    public boolean autenticar(String senha) {
        return this.senha.equals(senha);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) — %s", nome, email, permissao.getDescricao());
    }
}

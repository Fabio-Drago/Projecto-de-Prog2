package classes;

// Representa um cliente registado no sistema
public class Cliente {

    private int id;          // identificador único gerado automaticamente
    private String nome;
    private String telefone;
    private String email;    // campo opcional, pode estar vazio

    public Cliente(int id, String nome, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }

    // Formato de apresentação usado nas listagens do sistema
    @Override
    public String toString() {
        return String.format("ID: %d | %-25s | Tel: %-12s | Email: %s",
                id, nome, telefone, email);
    }
}

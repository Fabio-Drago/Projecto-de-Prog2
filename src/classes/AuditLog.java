package classes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Regista uma acção feita por um administrador ou cliente, com data e hora exactas
public class AuditLog {

    private LocalDateTime data;   // momento em que a acção foi feita
    private String adminNome;     // nome de quem fez a acção (admin ou "Cliente:Nome")
    private String acao;          // descrição da acção realizada

    public AuditLog(LocalDateTime data, String adminNome, String acao) {
        this.data = data;
        this.adminNome = adminNome;
        this.acao = acao;
    }

    public LocalDateTime getData() { return data; }
    public String getAdminNome() { return adminNome; }
    public String getAcao() { return acao; }

    // Formato de apresentação: [dd-MM-yyyy HH:mm] Nome — Acção
    @Override
    public String toString() {
        return String.format("[%s] %s — %s",
                data.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")),
                adminNome, acao);
    }
}

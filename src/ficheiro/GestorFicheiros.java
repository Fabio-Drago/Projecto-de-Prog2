package ficheiro;

import classes.Admin;
import classes.Admin.Permissao;
import classes.Aluguer;
import classes.AuditLog;
import classes.Carro;
import classes.Carro.Categoria;
import classes.Cliente;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

// Trata de guardar e ler todos os dados do sistema em ficheiros .txt
// Cada tipo de dados tem o seu próprio ficheiro dentro da pasta "dados"
public class GestorFicheiros {

    // Caminhos dos ficheiros onde os dados são guardados
    private static final String PASTA_DADOS = "dados";
    private static final String FICHEIRO_CLIENTES   = PASTA_DADOS + "/clientes.txt";
    private static final String FICHEIRO_CARROS     = PASTA_DADOS + "/carros.txt";
    private static final String FICHEIRO_ALUGUERES  = PASTA_DADOS + "/alugueres.txt";
    private static final String FICHEIRO_ADMINS     = PASTA_DADOS + "/admins.txt";
    private static final String FICHEIRO_AUDIT      = PASTA_DADOS + "/audit.txt";

    // Cria a pasta "dados" se ainda não existir, para evitar erros ao escrever ficheiros
    private static boolean garantirPasta() {
        File pasta = new File(PASTA_DADOS);
        return pasta.exists() || pasta.mkdirs();
    }

    // ─── Clientes ────────────────────────────────────────────────────────────────

    // Escreve todos os clientes no ficheiro, um por linha, no formato Campo:Valor|Campo:Valor
    public static void guardarClientes(ArrayList<Cliente> clientes) {
        if (!garantirPasta()) { System.out.println("Erro: não foi possível criar a pasta de dados."); return; }
        try (BufferedWriter w = new BufferedWriter(new FileWriter(FICHEIRO_CLIENTES))) {
            for (Cliente c : clientes) {
                w.write("ID:" + c.getId() + "|Nome:" + c.getNome() + "|Telefone:" + c.getTelefone() + "|Email:" + c.getEmail());
                w.newLine();
            }
        } catch (IOException e) { System.out.println("Erro ao guardar clientes: " + e.getMessage()); }
    }

    // Lê o ficheiro de clientes e devolve a lista; linhas com erros são ignoradas individualmente
    public static ArrayList<Cliente> lerClientes() {
        ArrayList<Cliente> lista = new ArrayList<>();
        File f = new File(FICHEIRO_CLIENTES);
        if (!f.exists()) return lista; // ficheiro ainda não existe, devolve lista vazia

        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String linha; int n = 0;
            while ((linha = r.readLine()) != null) {
                n++; linha = linha.trim();
                if (linha.isEmpty()) continue;
                try {
                    int id        = lerCampoInteiro(linha, "ID");
                    String nome   = lerCampo(linha, "Nome");
                    String tel    = lerCampo(linha, "Telefone");
                    String email  = lerCampoOpcional(linha, "Email", ""); // email pode estar em branco
                    if (nome.isEmpty() || tel.isEmpty()) { System.out.println("Linha " + n + " de clientes ignorada (dados em falta)."); continue; }
                    lista.add(new Cliente(id, nome, tel, email));
                } catch (Exception e) { System.out.println("Linha " + n + " de clientes ignorada: " + e.getMessage()); }
            }
        } catch (IOException e) { System.out.println("Erro ao ler clientes: " + e.getMessage()); }
        return lista;
    }

    // ─── Carros ──────────────────────────────────────────────────────────────────

    // Escreve todos os carros no ficheiro com todos os campos necessários para os reconstruir
    public static void guardarCarros(ArrayList<Carro> carros) {
        if (!garantirPasta()) { System.out.println("Erro: não foi possível criar a pasta de dados."); return; }
        try (BufferedWriter w = new BufferedWriter(new FileWriter(FICHEIRO_CARROS))) {
            for (Carro c : carros) {
                w.write("ID:" + c.getId()
                        + "|Marca:" + c.getMarca()
                        + "|Modelo:" + c.getModelo()
                        + "|Ano:" + c.getAnoFabrico()
                        + "|Categoria:" + c.getCategoria().name() // guarda o nome do enum, ex: SUV
                        + "|Disponivel:" + c.isDisponivel()
                        + "|PrecoPorDia:" + c.getPrecoPorDia());
                w.newLine();
            }
        } catch (IOException e) { System.out.println("Erro ao guardar carros: " + e.getMessage()); }
    }

    // Lê o ficheiro de carros e devolve a lista; linhas com dados inválidos são ignoradas
    public static ArrayList<Carro> lerCarros() {
        ArrayList<Carro> lista = new ArrayList<>();
        File f = new File(FICHEIRO_CARROS);
        if (!f.exists()) return lista;

        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String linha; int n = 0;
            while ((linha = r.readLine()) != null) {
                n++; linha = linha.trim();
                if (linha.isEmpty()) continue;
                try {
                    int id          = lerCampoInteiro(linha, "ID");
                    String marca    = lerCampo(linha, "Marca");
                    String modelo   = lerCampo(linha, "Modelo");
                    int ano         = lerCampoInteiroOpcional(linha, "Ano", 2020);
                    Categoria cat   = Categoria.valueOf(lerCampoOpcional(linha, "Categoria", "ECONOMICO"));
                    boolean disp    = Boolean.parseBoolean(lerCampo(linha, "Disponivel"));
                    double preco    = Double.parseDouble(lerCampo(linha, "PrecoPorDia"));
                    if (marca.isEmpty() || modelo.isEmpty() || preco <= 0) {
                        System.out.println("Linha " + n + " de carros ignorada (dados inválidos)."); continue;
                    }
                    lista.add(new Carro(id, marca, modelo, ano, cat, disp, preco));
                } catch (Exception e) { System.out.println("Linha " + n + " de carros ignorada: " + e.getMessage()); }
            }
        } catch (IOException e) { System.out.println("Erro ao ler carros: " + e.getMessage()); }
        return lista;
    }

    // ─── Alugueres ───────────────────────────────────────────────────────────────

    // Guarda os alugueres; a data de devolução é guardada como "null" se o aluguer ainda estiver activo
    public static void guardarAlugueres(ArrayList<Aluguer> alugueres) {
        if (!garantirPasta()) { System.out.println("Erro: não foi possível criar a pasta de dados."); return; }
        try (BufferedWriter w = new BufferedWriter(new FileWriter(FICHEIRO_ALUGUERES))) {
            for (Aluguer a : alugueres) {
                String devStr = (a.getDataDevolucao() != null) ? a.getDataDevolucao().toString() : "null";
                w.write("ID:" + a.getId()
                        + "|IDCliente:" + a.getCliente().getId()
                        + "|IDCarro:" + a.getCarro().getId()
                        + "|DataInicio:" + a.getDataInicio()
                        + "|DataFim:" + a.getDataFim()
                        + "|DataDevolucao:" + devStr
                        + "|Dias:" + a.getNumeroDias()
                        + "|Total:" + a.getTotalPago()
                        + "|Activo:" + a.isActivo());
                w.newLine();
            }
        } catch (IOException e) { System.out.println("Erro ao guardar alugueres: " + e.getMessage()); }
    }

    // Lê os alugueres e reconstrói os objectos usando as listas de clientes e carros já carregadas
    public static ArrayList<Aluguer> lerAlugueres(ArrayList<Cliente> clientes, ArrayList<Carro> carros) {
        ArrayList<Aluguer> lista = new ArrayList<>();
        File f = new File(FICHEIRO_ALUGUERES);
        if (!f.exists()) return lista;

        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String linha; int n = 0;
            while ((linha = r.readLine()) != null) {
                n++; linha = linha.trim();
                if (linha.isEmpty()) continue;
                try {
                    int id           = lerCampoInteiro(linha, "ID");
                    int idCliente    = lerCampoInteiro(linha, "IDCliente");
                    int idCarro      = lerCampoInteiro(linha, "IDCarro");
                    LocalDate inicio = LocalDate.parse(lerCampo(linha, "DataInicio"));
                    LocalDate fim    = LocalDate.parse(lerCampo(linha, "DataFim"));
                    String devRaw    = lerCampoOpcional(linha, "DataDevolucao", "null");
                    LocalDate dev    = devRaw.equals("null") ? null : LocalDate.parse(devRaw); // null se ainda activo
                    int dias         = lerCampoInteiroOpcional(linha, "Dias", 1);
                    double total     = Double.parseDouble(lerCampoOpcional(linha, "Total", "0"));
                    boolean activo   = Boolean.parseBoolean(lerCampo(linha, "Activo"));

                    // Procura o cliente e o carro pelo ID guardado no ficheiro
                    Cliente cliente  = encontrarCliente(clientes, idCliente);
                    Carro carro      = encontrarCarro(carros, idCarro);
                    if (cliente == null || carro == null) {
                        System.out.println("Linha " + n + " de alugueres ignorada (cliente ou carro não encontrado)."); continue;
                    }
                    lista.add(new Aluguer(id, cliente, carro, inicio, fim, dev, dias, total, activo));
                } catch (Exception e) { System.out.println("Linha " + n + " de alugueres ignorada: " + e.getMessage()); }
            }
        } catch (IOException e) { System.out.println("Erro ao ler alugueres: " + e.getMessage()); }
        return lista;
    }

    // ─── Admins ──────────────────────────────────────────────────────────────────

    // Guarda os admins com nome, email, senha e permissão
    public static void guardarAdmins(ArrayList<Admin> admins) {
        if (!garantirPasta()) { System.out.println("Erro: não foi possível criar a pasta de dados."); return; }
        try (BufferedWriter w = new BufferedWriter(new FileWriter(FICHEIRO_ADMINS))) {
            for (Admin a : admins) {
                w.write("Nome:" + a.getNome() + "|Email:" + a.getEmail() + "|Senha:" + a.getSenha() + "|Permissao:" + a.getPermissao().name());
                w.newLine();
            }
        } catch (IOException e) { System.out.println("Erro ao guardar admins: " + e.getMessage()); }
    }

    // Lê os admins do ficheiro; se o ficheiro não existir, o Sistema cria o admin por defeito
    public static ArrayList<Admin> lerAdmins() {
        ArrayList<Admin> lista = new ArrayList<>();
        File f = new File(FICHEIRO_ADMINS);
        if (!f.exists()) return lista;

        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String linha; int n = 0;
            while ((linha = r.readLine()) != null) {
                n++; linha = linha.trim();
                if (linha.isEmpty()) continue;
                try {
                    String nome       = lerCampo(linha, "Nome");
                    String email      = lerCampo(linha, "Email");
                    String senha      = lerCampo(linha, "Senha");
                    Permissao perm    = Permissao.valueOf(lerCampo(linha, "Permissao"));
                    lista.add(new Admin(nome, email, senha, perm));
                } catch (Exception e) { System.out.println("Linha " + n + " de admins ignorada: " + e.getMessage()); }
            }
        } catch (IOException e) { System.out.println("Erro ao ler admins: " + e.getMessage()); }
        return lista;
    }

    // ─── Audit Log ───────────────────────────────────────────────────────────────

    // Guarda o histórico de acções; a data é guardada no formato ISO para facilitar a leitura posterior
    public static void guardarAudit(ArrayList<AuditLog> audit) {
        if (!garantirPasta()) { System.out.println("Erro: não foi possível criar a pasta de dados."); return; }
        try (BufferedWriter w = new BufferedWriter(new FileWriter(FICHEIRO_AUDIT))) {
            for (AuditLog a : audit) {
                w.write("Data:" + a.getData().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        + "|Admin:" + a.getAdminNome()
                        + "|Acao:" + a.getAcao());
                w.newLine();
            }
        } catch (IOException e) { System.out.println("Erro ao guardar audit: " + e.getMessage()); }
    }

    // Lê o histórico de acções do ficheiro e reconstrói os registos
    public static ArrayList<AuditLog> lerAudit() {
        ArrayList<AuditLog> lista = new ArrayList<>();
        File f = new File(FICHEIRO_AUDIT);
        if (!f.exists()) return lista;

        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String linha; int n = 0;
            while ((linha = r.readLine()) != null) {
                n++; linha = linha.trim();
                if (linha.isEmpty()) continue;
                try {
                    LocalDateTime data = LocalDateTime.parse(lerCampo(linha, "Data"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    String admin       = lerCampo(linha, "Admin");
                    String acao        = lerCampo(linha, "Acao");
                    lista.add(new AuditLog(data, admin, acao));
                } catch (Exception e) { System.out.println("Linha " + n + " de audit ignorada: " + e.getMessage()); }
            }
        } catch (IOException e) { System.out.println("Erro ao ler audit: " + e.getMessage()); }
        return lista;
    }

    // ─── Auxiliares de leitura de campos ─────────────────────────────────────────

    // Extrai o valor de um campo no formato "Campo:Valor|OutroCampo:..."
    // Lança excepção se o campo não existir na linha
    private static String lerCampo(String linha, String campo) {
        String prefixo = campo + ":";
        int inicio = linha.indexOf(prefixo);
        if (inicio == -1) throw new IllegalArgumentException("Campo '" + campo + "' não encontrado.");
        inicio += prefixo.length();
        int fim = linha.indexOf("|", inicio); // procura o próximo separador
        return (fim == -1 ? linha.substring(inicio) : linha.substring(inicio, fim)).trim();
    }

    // Igual ao anterior, mas devolve o valor por defeito se o campo não existir
    private static String lerCampoOpcional(String linha, String campo, String padrao) {
        try { return lerCampo(linha, campo); } catch (Exception e) { return padrao; }
    }

    // Versão de lerCampo que converte o resultado para inteiro
    private static int lerCampoInteiro(String linha, String campo) {
        return Integer.parseInt(lerCampo(linha, campo));
    }

    // Versão opcional que devolve um valor por defeito se o campo não existir ou não for número
    private static int lerCampoInteiroOpcional(String linha, String campo, int padrao) {
        try { return Integer.parseInt(lerCampo(linha, campo)); } catch (Exception e) { return padrao; }
    }

    // Percorre a lista de clientes até encontrar o que tem o ID indicado
    private static Cliente encontrarCliente(ArrayList<Cliente> lista, int id) {
        for (Cliente c : lista) { if (c.getId() == id) return c; }
        return null;
    }

    // Percorre a lista de carros até encontrar o que tem o ID indicado
    private static Carro encontrarCarro(ArrayList<Carro> lista, int id) {
        for (Carro c : lista) { if (c.getId() == id) return c; }
        return null;
    }
}

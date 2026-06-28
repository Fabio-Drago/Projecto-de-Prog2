package sistema;

import classes.Admin;
import classes.Admin.Permissao;
import classes.Aluguer;
import classes.AuditLog;
import classes.Carro;
import classes.Carro.Categoria;
import classes.Cliente;
import classes.Relatorio;
import ficheiro.GestorFicheiros;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Sistema {

    private ArrayList<Cliente> clientes = new ArrayList<>();
    private ArrayList<Carro> carros = new ArrayList<>();
    private ArrayList<Aluguer> alugueres = new ArrayList<>();
    private ArrayList<Admin> admins = new ArrayList<>();
    private ArrayList<AuditLog> auditLog = new ArrayList<>();

    private int contadorClientes = 1;
    private int contadorCarros = 1;
    private int contadorAlugueres = 1;

    // ─── Getters
    // ──────────────────────────────────────────────────────────────────

    public ArrayList<Cliente> getClientes() {
        return clientes;
    }

    public ArrayList<Carro> getCarros() {
        return carros;
    }

    public ArrayList<Aluguer> getAlugueres() {
        return alugueres;
    }

    // ─── Dados de exemplo (carregados quando não há ficheiros guardados)
    // ──────────

    public void carregarDadosExemplo() {
        // 5 carros de exemplo com diferentes categorias e preços dentro dos intervalos
        // definidos
        carros.add(new Carro(contadorCarros++, "Toyota", "Yaris", 2022, Categoria.ECONOMICO, true, 22000));
        carros.add(new Carro(contadorCarros++, "Volkswagen", "Golf", 2023, Categoria.COMPACTO, true, 38000));
        carros.add(new Carro(contadorCarros++, "Toyota", "Corolla", 2023, Categoria.BERLINA, true, 48000));
        carros.add(new Carro(contadorCarros++, "Ford", "Explorer", 2024, Categoria.SUV, true, 65000));
        carros.add(new Carro(contadorCarros++, "BMW", "Série 5", 2024, Categoria.PREMIUM, true, 85000));

        // 5 clientes de exemplo
        clientes.add(new Cliente(contadorClientes++, "João Baptista Silva", "923456789", "joao.silva@email.com"));
        clientes.add(new Cliente(contadorClientes++, "Maria da Conceição", "912345678", "maria.conceicao@email.com"));
        clientes.add(new Cliente(contadorClientes++, "Carlos Eduardo Mendes", "934567890", "carlos.mendes@email.com"));
        clientes.add(new Cliente(contadorClientes++, "Ana Luísa Fernandes", "945678901", "ana.fernandes@email.com"));
        clientes.add(new Cliente(contadorClientes++, "Pedro António Lopes", "956789012", "pedro.lopes@email.com"));
    }

    // ─── Clientes
    // ─────────────────────────────────────────────────────────────────

    public boolean adicionarCliente(String nome, String telefone, String email) {
        nome = nome.trim();
        telefone = telefone.trim();
        email = email.trim();

        if (nome.isEmpty()) {
            System.out.println("O nome não pode estar vazio.");
            return false;
        }
        if (!nomeValido(nome)) {
            System.out.println("O nome só pode conter letras e espaços.");
            return false;
        }
        if (telefone.isEmpty()) {
            System.out.println("O telefone não pode estar vazio.");
            return false;
        }
        if (!telefoneValido(telefone)) {
            System.out.println("O telefone só pode conter dígitos (9 a 12 algarismos).");
            return false;
        }
        if (!email.isEmpty() && !emailValido(email)) {
            System.out.println("Endereço de email inválido.");
            return false;
        }

        for (Cliente c : clientes) {
            if (c.getTelefone().equals(telefone)) {
                System.out.println("Já existe um cliente com esse telefone.");
                return false;
            }
        }

        Cliente cliente = new Cliente(contadorClientes++, nome, telefone, email);
        clientes.add(cliente);
        System.out.println("\nCliente registado com sucesso:");
        System.out.println("  " + cliente);
        return true;
    }

    public void listarClientes() {
        if (clientes.isEmpty()) {
            System.out.println("Não existem clientes registados.");
            return;
        }
        System.out.println("Total: " + clientes.size() + " cliente(s)");
        System.out.println("─".repeat(90));
        for (Cliente c : clientes)
            System.out.println(c);
        System.out.println("─".repeat(90));
    }

    public Cliente procurarCliente(int id) {
        for (Cliente c : clientes) {
            if (c.getId() == id)
                return c;
        }
        return null;
    }

    // ─── Carros
    // ───────────────────────────────────────────────────────────────────

    public boolean adicionarCarro(String marca, String modelo, int anoFabrico, Categoria categoria, double preco) {
        marca = marca.trim();
        modelo = modelo.trim();

        if (marca.isEmpty()) {
            System.out.println("A marca não pode estar vazia.");
            return false;
        }
        if (!textoAlfanumerico(marca)) {
            System.out.println("A marca só pode conter letras, números e espaços.");
            return false;
        }
        if (modelo.isEmpty()) {
            System.out.println("O modelo não pode estar vazio.");
            return false;
        }
        if (!textoAlfanumerico(modelo)) {
            System.out.println("O modelo só pode conter letras, números e espaços.");
            return false;
        }

        int anoAtual = LocalDate.now().getYear();
        if (anoFabrico < 2000 || anoFabrico > anoAtual) {
            System.out.printf("O ano de fabrico deve estar entre 2000 e %d.%n", anoAtual);
            return false;
        }
        if (categoria == null) {
            System.out.println("Categoria inválida.");
            return false;
        }

        // Validação do preço dentro do intervalo da categoria
        if (preco < categoria.getPrecoMinimo() || preco > categoria.getPrecoMaximo()) {
            System.out.printf("O preço para a categoria %s deve estar entre %,.0f Kz e %,.0f Kz.%n",
                    categoria.getNome(), categoria.getPrecoMinimo(), categoria.getPrecoMaximo());
            return false;
        }

        Carro carro = new Carro(contadorCarros++, marca, modelo, anoFabrico, categoria, true, preco);
        carros.add(carro);
        System.out.println("\nCarro registado com sucesso:");
        System.out.println("  " + carro);
        return true;
    }

    public void listarCarros() {
        if (carros.isEmpty()) {
            System.out.println("Não existem carros registados.");
            return;
        }
        System.out.println("Total: " + carros.size() + " carro(s)");
        System.out.println("─".repeat(100));
        for (Carro c : carros)
            System.out.println(c);
        System.out.println("─".repeat(100));
    }

    public void listarCarrosDisponiveis() {
        ArrayList<Carro> disponiveis = new ArrayList<>();
        for (Carro c : carros) {
            if (c.isDisponivel())
                disponiveis.add(c);
        }

        if (disponiveis.isEmpty()) {
            System.out.println("Não há carros disponíveis de momento.");
            return;
        }

        System.out.println("Carros disponíveis: " + disponiveis.size());
        System.out.println("─".repeat(100));
        for (Carro c : disponiveis)
            System.out.println(c);
        System.out.println("─".repeat(100));
    }

    public void listarCarrosPorCategoria(Categoria categoria) {
        System.out.println("Categoria: " + categoria.getNome());
        System.out.println("─".repeat(100));
        boolean encontrou = false;
        for (Carro c : carros) {
            if (c.getCategoria() == categoria) {
                System.out.println(c);
                encontrou = true;
            }
        }
        if (!encontrou)
            System.out.println("Nenhum carro desta categoria registado.");
        System.out.println("─".repeat(100));
    }

    public Carro procurarCarro(int id) {
        for (Carro c : carros) {
            if (c.getId() == id)
                return c;
        }
        return null;
    }

    // ─── Alugueres
    // ────────────────────────────────────────────────────────────────

    public boolean realizarAluguer(int idCliente, int idCarro, int numeroDias) {
        if (numeroDias < 1) {
            System.out.println("O número de dias deve ser no mínimo 1.");
            return false;
        }

        Cliente cliente = procurarCliente(idCliente);
        if (cliente == null) {
            System.out.println("Cliente com ID " + idCliente + " não encontrado.");
            return false;
        }

        Carro carro = procurarCarro(idCarro);
        if (carro == null) {
            System.out.println("Carro com ID " + idCarro + " não encontrado.");
            return false;
        }
        if (!carro.isDisponivel()) {
            System.out.println("O carro " + carro.getMarca() + " " + carro.getModelo() + " está indisponível.");
            return false;
        }

        for (Aluguer a : alugueres) {
            if (a.getCliente().getId() == idCliente && a.isActivo()) {
                System.out.println("Este cliente já tem um aluguer activo (ID " + a.getId() + ").");
                return false;
            }
        }

        double totalEstimado = numeroDias * carro.getPrecoPorDia();
        carro.setDisponivel(false);

        Aluguer aluguer = new Aluguer(contadorAlugueres++, cliente, carro, LocalDate.now(), numeroDias, true);
        alugueres.add(aluguer);

        System.out.println("\n" + "═".repeat(60));
        System.out.println("       COMPROVATIVO DE ALUGUER");
        System.out.println("═".repeat(60));
        System.out.printf("  Nº Aluguer  : %d%n", aluguer.getId());
        System.out.printf("  Cliente     : %s%n", cliente.getNome());
        System.out.printf("  Carro       : %s %s (%d)%n", carro.getMarca(), carro.getModelo(), carro.getAnoFabrico());
        System.out.printf("  Categoria   : %s%n", carro.getCategoria().getNome());
        System.out.printf("  Início      : %s%n", aluguer.getDataInicio());
        System.out.printf("  Previsão    : %s (%d dias)%n", aluguer.getDataFim(), numeroDias);
        System.out.printf("  Preço/dia   : %,.0f Kz%n", carro.getPrecoPorDia());
        System.out.printf("  Total estim.: %,.0f Kz%n", totalEstimado);
        System.out.println("  (O total final é calculado na devolução)");
        System.out.println("═".repeat(60));
        return true;
    }

    public boolean devolverCarro(int idAluguer, LocalDate dataDevolucao) {
        for (Aluguer a : alugueres) {
            if (a.getId() == idAluguer) {
                if (!a.isActivo()) {
                    System.out.println("Este aluguer já foi concluído.");
                    return false;
                }

                if (dataDevolucao.isBefore(a.getDataInicio())) {
                    System.out.println(
                            "A data de devolução não pode ser anterior à data de início (" + a.getDataInicio() + ").");
                    return false;
                }

                double total = a.calcularTotal(dataDevolucao);
                long diasReais = ChronoUnit.DAYS.between(a.getDataInicio(), dataDevolucao);
                if (diasReais < 1)
                    diasReais = 1;

                a.setActivo(false);
                a.getCarro().setDisponivel(true);
                a.setTotalPago(total);
                a.setDataDevolucao(dataDevolucao);

                System.out.println("\n" + "═".repeat(60));
                System.out.println("       RECIBO DE DEVOLUÇÃO");
                System.out.println("═".repeat(60));
                System.out.printf("  Nº Aluguer  : %d%n", a.getId());
                System.out.printf("  Cliente     : %s%n", a.getCliente().getNome());
                System.out.printf("  Carro       : %s %s%n", a.getCarro().getMarca(), a.getCarro().getModelo());
                System.out.printf("  Início      : %s%n", a.getDataInicio());
                System.out.printf("  Devolução   : %s%n", dataDevolucao);
                System.out.printf("  Dias  : %d%n", diasReais);
                System.out.printf("  Preço/dia   : %,.0f Kz%n", a.getCarro().getPrecoPorDia());
                System.out.println("─".repeat(60));
                System.out.printf("  TOTAL A PAGAR: %,.0f Kz%n", total);
                System.out.println("═".repeat(60));
                return true;
            }
        }
        System.out.println("Aluguer com ID " + idAluguer + " não encontrado.");
        return false;
    }

    public void listarAlugueres() {
        if (alugueres.isEmpty()) {
            System.out.println("Não existem alugueres registados.");
            return;
        }
        System.out.println("Total: " + alugueres.size() + " aluguer(es)");
        System.out.println("─".repeat(120));
        for (Aluguer a : alugueres)
            System.out.println(a);
        System.out.println("─".repeat(120));
    }

    // ─── Relatórios
    // ────────────────────────────────────────────────────────────────

    public Relatorio getRelatorio() {
        return new Relatorio(clientes, carros, alugueres);
    }

    // ─── Persistência ────────────────────────────────────────────────────────────

    public void guardarDados() {
        GestorFicheiros.guardarClientes(clientes);
        GestorFicheiros.guardarCarros(carros);
        GestorFicheiros.guardarAlugueres(alugueres);
        GestorFicheiros.guardarAdmins(admins);
        GestorFicheiros.guardarAudit(auditLog);
        System.out.println("Dados guardados com sucesso.");
    }

    public boolean carregarDados() {
        clientes = GestorFicheiros.lerClientes();
        carros = GestorFicheiros.lerCarros();
        alugueres = GestorFicheiros.lerAlugueres(clientes, carros);
        admins = GestorFicheiros.lerAdmins();
        auditLog = GestorFicheiros.lerAudit();

        contadorClientes = proximoIdClientes();
        contadorCarros = proximoIdCarros();
        contadorAlugueres = proximoIdAlugueres();

        if (admins.isEmpty()) {
            admins.add(new Admin("Admin Principal", "admin@sistema.com", "admin@123", Permissao.GERENTE));
        }

        return clientes.isEmpty() && carros.isEmpty();
    }

    public Admin autenticarAdmin(String nome, String senha) {
        for (Admin a : admins) {
            if (a.getNome().equalsIgnoreCase(nome.trim()) && a.autenticar(senha)) {
                return a;
            }
        }
        return null;
    }

    public void registarAudit(String autor, String acao) {
        auditLog.add(new AuditLog(LocalDateTime.now(), autor, acao));
    }

    public ArrayList<AuditLog> getAuditLog() {
        return auditLog;
    }

    public void listarAuditLog() {
        if (auditLog.isEmpty()) {
            System.out.println("Nenhuma acção registada.");
            return;
        }
        System.out.println("Total: " + auditLog.size() + " registo(s)");
        System.out.println("-".repeat(90));
        for (AuditLog a : auditLog) {
            System.out.println(a);
        }
        System.out.println("-".repeat(90));
    }

    // ─── Validações ──────────────────────────────────────────────────────────────

    private boolean nomeValido(String nome) {
        if (!nome.matches("[a-zA-ZÀ-ÿ ]+"))
            return false;
        for (char c : nome.toCharArray()) {
            if (Character.isLetter(c))
                return true;
        }
        return false;
    }

    private boolean telefoneValido(String tel) {
        return tel.matches("[0-9]{9,12}");
    }

    private boolean emailValido(String email) {
        return email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    }

    private boolean textoAlfanumerico(String texto) {
        return texto.matches("[a-zA-ZÀ-ÿ0-9 ]+");
    }

    private int proximoIdClientes() {
        return clientes.stream().mapToInt(Cliente::getId).max().orElse(0) + 1;
    }

    private int proximoIdCarros() {
        return carros.stream().mapToInt(Carro::getId).max().orElse(0) + 1;
    }

    private int proximoIdAlugueres() {
        return alugueres.stream().mapToInt(Aluguer::getId).max().orElse(0) + 1;
    }
}

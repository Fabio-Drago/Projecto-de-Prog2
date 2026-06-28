package main;

import classes.Admin;
import classes.Aluguer;
import classes.Carro;
import classes.Carro.Categoria;
import classes.Cliente;
import sistema.Sistema;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

// Ponto de entrada do programa. Gere os menus e a interacção com o utilizador
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Sistema sistema = new Sistema();

    public static void main(String[] args) {
        // Tenta carregar os dados guardados; devolve true se não existir nenhum
        boolean semDados = sistema.carregarDados();
        System.out.println("\nBem-vindo ao Sistema de Aluguer de Carros");

        // Se não há dados guardados, oferece carregar dados de exemplo para demonstração
        if (semDados) {
            System.out.println("Nenhum dado guardado encontrado.");
            System.out.print("Deseja carregar dados de exemplo (5 carros e 5 clientes)? (s/n): ");
            String resposta = scanner.nextLine().trim().toLowerCase();
            if (resposta.equals("s")) {
                sistema.carregarDadosExemplo();
                System.out.println("Dados de exemplo carregados.");
            }
        }

        // Ciclo principal do programa — repete até o utilizador escolher sair
        int opcao;
        do {
            mostrarMenuPrincipal();
            opcao = lerInteiro("Opção");
            switch (opcao) {
                case 1:
                    menuClienteLogin();
                    break;
                case 2:
                    menuAdminLogin();
                    break;
                case 0:
                    System.out.println("\nSistema encerrado. Até breve!");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);

        // Guardar dados automaticamente ao sair para não perder alterações
        sistema.guardarDados();
        scanner.close();
    }

    // ─── Menus de acesso ─────────────────────────────────────────────────────────

    private static void mostrarMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE ALUGUER DE CARROS    ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println("  1. Entrar como Cliente");
        System.out.println("  2. Entrar como Administrador");
        System.out.println("  0. Sair");
    }

    // O cliente entra com o seu ID; não há senha porque é um sistema interno
    private static void menuClienteLogin() {
        System.out.println("\n─── Login de Cliente ───");
        int idCliente = lerInteiro("ID do cliente");
        if (idCliente <= 0) {
            System.out.println("ID inválido.");
            return;
        }

        Cliente cliente = sistema.procurarCliente(idCliente);
        if (cliente == null) {
            System.out.println("Cliente com ID " + idCliente + " não encontrado.");
            return;
        }
        System.out.println("Bem-vindo, " + cliente.getNome() + "!");
        menuCliente(cliente);
    }

    // O administrador entra com nome e password; a verificação é feita no Sistema
    private static void menuAdminLogin() {
        System.out.println("\n─── Acesso Administrador ───");
        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();
        System.out.print("Password: ");
        String senha = scanner.nextLine().trim();

        Admin admin = sistema.autenticarAdmin(nome, senha);
        if (admin == null) {
            System.out.println("Nome ou password incorrectos. Acesso negado.");
            return;
        }
        System.out.println("Acesso concedido. Bem-vindo, " + admin.getNome() + " (" + admin.getPermissao().getDescricao() + ")!");
        menuAdministrador(admin);
    }

    // ─── Menu do cliente ─────────────────────────────────────────────────────────

    // Mostra as opções disponíveis para o cliente e repete até ele fazer logout
    private static void menuCliente(Cliente cliente) {
        int opcao;
        do {
            System.out.println("\n╔═══════════════════════════╗");
            System.out.println("║      MENU DO CLIENTE      ║");
            System.out.println("╚═══════════════════════════╝");
            System.out.println("  1. Ver carros disponíveis");
            System.out.println("  2. Filtrar por categoria");
            System.out.println("  3. Realizar aluguer");
            System.out.println("  4. Devolver carro");
            System.out.println("  5. Os meus alugueres");
            System.out.println("  0. Sair (logout)");

            opcao = lerInteiro("Opção");
            switch (opcao) {
                case 1:
                    sistema.listarCarrosDisponiveis();
                    break;
                case 2:
                    menuFiltrarPorCategoria();
                    break;
                case 3:
                    realizarAluguerCliente(cliente);
                    break;
                case 4:
                    devolverCarroCliente(cliente);
                    break;
                case 5:
                    listarAlugueresCliente(cliente);
                    break;
                case 0:
                    System.out.println("Sessão encerrada.");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    // Pede ao cliente para escolher um carro disponível, o número de dias,
    // mostra a estimativa e pede confirmação antes de criar o aluguer
    private static void realizarAluguerCliente(Cliente cliente) {
        sistema.listarCarrosDisponiveis();
        int idCarro = lerInteiro("ID do carro a alugar");
        if (idCarro <= 0) {
            System.out.println("ID inválido.");
            return;
        }

        Carro carro = sistema.procurarCarro(idCarro);
        if (carro == null) {
            System.out.println("Carro não encontrado.");
            return;
        }
        if (!carro.isDisponivel()) {
            System.out.println("Esse carro não está disponível.");
            return;
        }

        System.out.printf("Carro: %s %s | %,.0f Kz/dia%n", carro.getMarca(), carro.getModelo(), carro.getPrecoPorDia());
        int dias = lerInteiroPositivo("Número de dias");
        if (dias < 1)
            return;

        double estimativa = dias * carro.getPrecoPorDia();
        System.out.printf("Estimativa total: %,.0f Kz (%d dias × %,.0f Kz)%n", estimativa, dias,
                carro.getPrecoPorDia());
        System.out.print("Confirmar aluguer? (s/n): ");
        String confirmacao = scanner.nextLine().trim().toLowerCase();

        if (confirmacao.equals("s")) {
            boolean sucesso = sistema.realizarAluguer(cliente.getId(), idCarro, dias);
            if (sucesso) {
                sistema.registarAudit("Cliente:" + cliente.getNome(),
                        "Alugou carro " + carro.getMarca() + " " + carro.getModelo() + " por " + dias + " dia(s)");
            }
        } else {
            System.out.println("Aluguer cancelado.");
        }
    }

    // Mostra os alugueres activos do cliente e processa a devolução do escolhido
    // Verifica que o aluguer pertence ao cliente antes de devolver
    private static void devolverCarroCliente(Cliente cliente) {
        boolean temActivos = false;
        System.out.println("\nOs seus alugueres activos:");
        for (Aluguer a : sistema.getAlugueres()) {
            if (a.getCliente().getId() == cliente.getId() && a.isActivo()) {
                System.out.println("  " + a);
                temActivos = true;
            }
        }
        if (!temActivos) {
            System.out.println("Não tem alugueres activos.");
            return;
        }

        int idAluguer = lerInteiro("ID do aluguer a devolver");
        if (idAluguer <= 0) {
            System.out.println("ID inválido.");
            return;
        }

        // Verificação: o aluguer pertence ao cliente
        Aluguer alvo = null;
        for (Aluguer a : sistema.getAlugueres()) {
            if (a.getId() == idAluguer && a.getCliente().getId() == cliente.getId() && a.isActivo()) {
                alvo = a;
                break;
            }
        }
        if (alvo == null) {
            System.out.println("ID de aluguer inválido ou não pertence à sua conta.");
            return;
        }

        LocalDate dataDevolucao = lerData("Data de devolução (DD-MM-AAAA)");
        if (dataDevolucao == null)
            return;
        boolean sucesso = sistema.devolverCarro(idAluguer, dataDevolucao);
        if (sucesso) {
            sistema.registarAudit("Cliente:" + cliente.getNome(),
                    "Devolveu aluguer #" + idAluguer + " em " + dataDevolucao);
        }
    }

    // Mostra o histório completo de alugueres do cliente (activos e concluídos)
    private static void listarAlugueresCliente(Cliente cliente) {
        boolean encontrou = false;
        System.out.println("\nHistórico de alugueres:");
        System.out.println("─".repeat(120));
        for (Aluguer a : sistema.getAlugueres()) {
            if (a.getCliente().getId() == cliente.getId()) {
                System.out.println(a);
                encontrou = true;
            }
        }
        if (!encontrou)
            System.out.println("Não tem alugueres registados.");
        System.out.println("─".repeat(120));
    }

    // Pede ao utilizador para escolher uma categoria e lista os carros dessa categoria
    private static void menuFiltrarPorCategoria() {
        Categoria.listarCategorias();
        int opcao = lerInteiro("Número da categoria");
        Categoria cat = Categoria.porIndice(opcao);
        if (cat == null) {
            System.out.println("Categoria inválida.");
            return;
        }
        sistema.listarCarrosPorCategoria(cat);
    }

    // ─── Menu do administrador ───────────────────────────────────────────────────

    // O menu muda consoante a permissão do admin: Gerente vê tudo, Operador gere dados,
    // Consulta só pode ver listas sem fazer alterações
    private static void menuAdministrador(Admin admin) {
        // Determina o nível de acesso para controlar o que aparece no menu
        boolean isGerente = admin.getPermissao() == Admin.Permissao.GERENTE;
        boolean isOperador = admin.getPermissao() == Admin.Permissao.OPERADOR;
        boolean isConsulta = admin.getPermissao() == Admin.Permissao.CONSULTA;

        int opcao;
        do {
            System.out.println("\n\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557");
            System.out.println("\u2551     MENU ADMINISTRADOR           \u2551");
            System.out.println("\u2551  " + admin.getNome() + " (" + admin.getPermissao().getDescricao() + ")\u2551");
            System.out.println("\u2551" + "\u2550".repeat(36) + "\u2551");

            if (!isConsulta) {
                System.out.println("\u2551  CARROS                          \u2551");
                System.out.println("\u2551   1. Registar carro              \u2551");
            }
            System.out.println("\u2551   2. Listar todos os carros      \u2551");
            System.out.println("\u2551   3. Filtrar por categoria       \u2551");

            if (!isConsulta) {
                System.out.println("\u2551" + "\u2550".repeat(36) + "\u2551");
                System.out.println("\u2551  CLIENTES                        \u2551");
                System.out.println("\u2551   4. Registar cliente            \u2551");
            }
            System.out.println("\u2551   5. Listar clientes             \u2551");

            if (!isConsulta) {
                System.out.println("\u2551" + "\u2550".repeat(36) + "\u2551");
                System.out.println("\u2551  ALUGUERES                       \u2551");
                System.out.println("\u2551   6. Listar todos os alugueres   \u2551");
                System.out.println("\u2551   7. Devolver carro (admin)      \u2551");
            }

            if (isGerente) {
                System.out.println("\u2551" + "\u2550".repeat(36) + "\u2551");
                System.out.println("\u2551  RELAT\u00d3RIOS                      \u2551");
                System.out.println("\u2551   8. Relat\u00f3rio geral             \u2551");
                System.out.println("\u2551   9. Carros mais alugados        \u2551");
                System.out.println("\u2551  10. Receita por categoria       \u2551");
                System.out.println("\u2551  11. Clientes mais activos       \u2551");
            }

            if (isGerente) {
                System.out.println("\u2551" + "\u2550".repeat(36) + "\u2551");
                System.out.println("\u2551  13. Hist\u00f3rico de ac\u00e7\u00f5es      \u2551");
            }

            System.out.println("\u2551" + "\u2550".repeat(36) + "\u2551");
            if (isGerente || isOperador) {
                System.out.println("\u2551  12. Guardar dados               \u2551");
            }
            System.out.println("\u2551   0. Sair (logout)               \u2551");
            System.out.println("\u255a" + "\u2550".repeat(36) + "\u255d");

            opcao = lerInteiro("Op\u00e7\u00e3o");
            switch (opcao) {
                case 1:
                    if (isConsulta) { System.out.println("Op\u00e7\u00e3o inv\u00e1lida."); break; }
                    registarCarro(admin);
                    break;
                case 2:
                    sistema.listarCarros();
                    break;
                case 3:
                    menuFiltrarPorCategoria();
                    break;
                case 4:
                    if (isConsulta) { System.out.println("Op\u00e7\u00e3o inv\u00e1lida."); break; }
                    registarCliente(admin);
                    break;
                case 5:
                    sistema.listarClientes();
                    break;
                case 6:
                    if (isConsulta) { System.out.println("Op\u00e7\u00e3o inv\u00e1lida."); break; }
                    sistema.listarAlugueres();
                    break;
                case 7:
                    if (isConsulta) { System.out.println("Op\u00e7\u00e3o inv\u00e1lida."); break; }
                    devolverCarroAdmin(admin);
                    break;
                case 8:
                    if (!isGerente) { System.out.println("Op\u00e7\u00e3o inv\u00e1lida."); break; }
                    { String rel = sistema.getRelatorio().relatorioGeral(); System.out.print(rel); perguntarExportar("Relatorio_Geral", rel); }
                    break;
                case 9:
                    if (!isGerente) { System.out.println("Op\u00e7\u00e3o inv\u00e1lida."); break; }
                    { String rel = sistema.getRelatorio().relatorioCarrosMaisAlugados(); System.out.print(rel); perguntarExportar("Carros_Mais_Alugados", rel); }
                    break;
                case 10:
                    if (!isGerente) { System.out.println("Op\u00e7\u00e3o inv\u00e1lida."); break; }
                    { String rel = sistema.getRelatorio().relatorioReceitaPorCategoria(); System.out.print(rel); perguntarExportar("Receita_por_Categoria", rel); }
                    break;
                case 11:
                    if (!isGerente) { System.out.println("Op\u00e7\u00e3o inv\u00e1lida."); break; }
                    { String rel = sistema.getRelatorio().relatorioClientesMaisActivos(); System.out.print(rel); perguntarExportar("Clientes_Mais_Activos", rel); }
                    break;
                case 12:
                    if (isConsulta) { System.out.println("Op\u00e7\u00e3o inv\u00e1lida."); break; }
                    sistema.guardarDados();
                    sistema.registarAudit(admin.getNome(), "Guardou dados");
                    break;
                case 13:
                    if (!isGerente) { System.out.println("Op\u00e7\u00e3o inv\u00e1lida."); break; }
                    sistema.listarAuditLog();
                    break;
                case 0:
                    System.out.println("Logout realizado.");
                    break;
                default:
                    System.out.println("Op\u00e7\u00e3o inv\u00e1lida.");
            }
        } while (opcao != 0);
    }

    // Recolhe os dados do novo carro, incluindo a categoria e o preço dentro do intervalo permitido
    private static void registarCarro(Admin admin) {
        System.out.print("Marca: ");
        String marca = scanner.nextLine().trim();
        if (marca.isEmpty()) {
            System.out.println("A marca não pode estar vazia.");
            return;
        }

        System.out.print("Modelo: ");
        String modelo = scanner.nextLine().trim();
        if (modelo.isEmpty()) {
            System.out.println("O modelo não pode estar vazio.");
            return;
        }

        int ano = lerInteiroPositivo("Ano de fabrico");
        if (ano <= 0)
            return;

        Categoria.listarCategorias();
        int numCat = lerInteiro("Número da categoria");
        Categoria categoria = Categoria.porIndice(numCat);
        if (categoria == null) {
            System.out.println("Categoria inválida.");
            return;
        }

        System.out.printf("Preço/dia para %s (%,.0f Kz – %,.0f Kz): ",
                categoria.getNome(), categoria.getPrecoMinimo(), categoria.getPrecoMaximo());
        double preco = lerDouble();
        if (preco <= 0) {
            System.out.println("Preço inválido.");
            return;
        }

        sistema.adicionarCarro(marca, modelo, ano, categoria, preco);
        sistema.registarAudit(admin.getNome(), "Registou carro " + marca + " " + modelo);
    }

    // Recolhe os dados do novo cliente; o email é opcional e pode ficar em branco
    private static void registarCliente(Admin admin) {
        System.out.print("Nome completo: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) {
            System.out.println("O nome não pode estar vazio.");
            return;
        }

        System.out.print("Telefone: ");
        String telefone = scanner.nextLine().trim();

        System.out.print("Email (opcional, prima Enter para omitir): ");
        String email = scanner.nextLine().trim();

        sistema.adicionarCliente(nome, telefone, email);
        sistema.registarAudit(admin.getNome(), "Registou cliente " + nome);
    }

    // Versão do administrador para devolver qualquer aluguer activo do sistema
    private static void devolverCarroAdmin(Admin admin) {
        System.out.println("\nAlugueres activos:");
        boolean temActivos = false;
        for (Aluguer a : sistema.getAlugueres()) {
            if (a.isActivo()) {
                System.out.println("  " + a);
                temActivos = true;
            }
        }
        if (!temActivos) {
            System.out.println("Não há alugueres activos.");
            return;
        }

        int idAluguer = lerInteiro("ID do aluguer a devolver");
        if (idAluguer <= 0) {
            System.out.println("ID inválido.");
            return;
        }
        LocalDate dataDevolucao = lerData("Data de devolução (DD-MM-AAAA)");
        if (dataDevolucao == null)
            return;
        sistema.devolverCarro(idAluguer, dataDevolucao);
        sistema.registarAudit(admin.getNome(), "Devolveu aluguer #" + idAluguer + " em " + dataDevolucao);
    }

    // ─── Exportar relatório ───────────────────────────────────────────────────────

    // Após mostrar um relatório, pergunta se o utilizador quer guardá-lo em ficheiro
    private static void perguntarExportar(String titulo, String conteudo) {
        System.out.print("Deseja exportar este relat\u00f3rio para ficheiro? (s/n): ");
        String resp = scanner.nextLine().trim().toLowerCase();
        if (resp.equals("s")) {
            sistema.getRelatorio().exportarRelatorio(titulo, conteudo);
        }
    }

    // ─── Leitura de input ────────────────────────────────────────────────────────

    // Lê uma data no formato DD-MM-AAAA; devolve null se o formato estiver errado
    private static LocalDate lerData(String campo) {
        System.out.print(campo + ": ");
        String linha = scanner.nextLine().trim();
        try {
            return LocalDate.parse(linha, java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException e) {
            System.out.println("Data inválida. Use o formato DD-MM-AAAA (ex: 25-06-2026).");
            return null;
        }
    }

    // Lê um número inteiro; devolve -1 se o utilizador escrever algo que não seja número
    private static int lerInteiro(String campo) {
        System.out.print(campo + ": ");
        String linha = scanner.nextLine().trim();
        try {
            return Integer.parseInt(linha);
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            return -1;
        }
    }

    // Igual ao anterior, mas garante que o valor é maior que zero
    private static int lerInteiroPositivo(String campo) {
        System.out.print(campo + ": ");
        String linha = scanner.nextLine().trim();
        try {
            int v = Integer.parseInt(linha);
            if (v <= 0) {
                System.out.println("Deve ser um número positivo.");
                return -1;
            }
            return v;
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido.");
            return -1;
        }
    }

    // Lê um número decimal; aceita vírgula ou ponto como separador decimal
    private static double lerDouble() {
        String linha = scanner.nextLine().trim().replace(",", ".");
        try {
            return Double.parseDouble(linha);
        } catch (NumberFormatException e) {
            System.out.println("Valor numérico inválido, a assumir 0.");
            return 0;
        }
    }
}
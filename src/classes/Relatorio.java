package classes;

import classes.Aluguer;
import classes.Carro;
import classes.Cliente;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Classe responsável por gerar e exportar os relatórios do sistema
public class Relatorio {

    private final List<Cliente> clientes;
    private final List<Carro> carros;
    private final List<Aluguer> alugueres;

    // Recebe as listas principais do sistema para poder analisá-las
    public Relatorio(List<Cliente> clientes, List<Carro> carros, List<Aluguer> alugueres) {
        this.clientes = clientes;
        this.carros = carros;
        this.alugueres = alugueres;
    }

    // ─── Relatório geral ─────────────────────────────────────────────────────────

    // Mostra um resumo geral: totais de clientes, carros, alugueres e receita
    public String relatorioGeral() {
        // Conta os alugueres activos e concluídos percorrendo a lista
        long activos = alugueres.stream().filter(Aluguer::isActivo).count();
        long concluidos = alugueres.stream().filter(a -> !a.isActivo()).count();

        // Soma o total pago em todos os alugueres concluídos
        double receitaTotal = alugueres.stream().mapToDouble(Aluguer::getTotalPago).sum();

        // Conta quantos carros estão disponíveis no momento
        long disponiveis = carros.stream().filter(Carro::isDisponivel).count();

        // Constrói o texto do relatório linha a linha
        StringBuilder sb = new StringBuilder();
        linha(sb, 55);
        sb.append("          RELATÓRIO GERAL DO SISTEMA\n");
        linha(sb, 55);
        sb.append(String.format("  Clientes registados  : %d%n", clientes.size()));
        sb.append(String.format("  Carros registados    : %d%n", carros.size()));
        sb.append(String.format("  Carros disponíveis   : %d%n", disponiveis));
        sb.append(String.format("  Carros alugados      : %d%n", carros.size() - disponiveis));
        sb.append(String.format("  Alugueres activos    : %d%n", activos));
        sb.append(String.format("  Alugueres concluídos : %d%n", concluidos));
        sb.append(String.format("  Receita total        : %,.0f Kz%n", receitaTotal));
        linha(sb, 55);
        return sb.toString();
    }

    // ─── Carros mais alugados ────────────────────────────────────────────────────

    // Mostra quais os carros mais vezes alugados, ordenados do mais para o menos
    public String relatorioCarrosMaisAlugados() {
        if (alugueres.isEmpty()) return "Ainda não há alugueres para analisar.\n";

        // Conta quantas vezes cada carro foi alugado e soma a receita gerada
        Map<String, Integer> contagem = new LinkedHashMap<>();
        Map<String, Double> receitas = new LinkedHashMap<>();

        for (Aluguer a : alugueres) {
            // A chave é o nome completo do carro (marca + modelo)
            String chave = a.getCarro().getMarca() + " " + a.getCarro().getModelo();
            contagem.put(chave, contagem.getOrDefault(chave, 0) + 1);
            receitas.put(chave, receitas.getOrDefault(chave, 0.0) + a.getTotalPago());
        }

        // Ordena os carros do mais alugado para o menos alugado
        List<Map.Entry<String, Integer>> lista = new ArrayList<>(contagem.entrySet());
        lista.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // O carro com mais alugueres define o tamanho máximo da barra gráfica
        int maxValor = lista.isEmpty() ? 0 : lista.get(0).getValue();
        int larguraBarra = 20;

        // Monta o texto da tabela com barra gráfica proporcional
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("=".repeat(75)).append("\n");
        sb.append("              CARROS MAIS ALUGADOS\n");
        sb.append("=".repeat(75)).append("\n");
        sb.append(String.format("  %-22s %-22s %-8s  %s%n", "Carro", "", "Alugueres", "Receita"));
        sb.append("-".repeat(75)).append("\n");

        for (Map.Entry<String, Integer> e : lista) {
            String barra = gerarBarra(e.getValue(), maxValor, larguraBarra);
            sb.append(String.format("  %-22s %s %-8d  %,.0f Kz%n",
                    e.getKey(), barra, e.getValue(), receitas.get(e.getKey())));
        }
        sb.append("=".repeat(75)).append("\n");
        return sb.toString();
    }

    // ─── Receita por categoria ───────────────────────────────────────────────────

    // Agrupa os alugueres por categoria de carro e mostra a receita de cada uma
    public String relatorioReceitaPorCategoria() {
        if (alugueres.isEmpty()) return "Ainda não há alugueres para analisar.\n";

        // Para cada categoria, guarda a receita total e o número de alugueres
        Map<String, Double> receitas = new LinkedHashMap<>();
        Map<String, Integer> contagens = new LinkedHashMap<>();

        for (Aluguer a : alugueres) {
            String cat = a.getCarro().getCategoria().getNome();
            receitas.put(cat, receitas.getOrDefault(cat, 0.0) + a.getTotalPago());
            contagens.put(cat, contagens.getOrDefault(cat, 0) + 1);
        }

        // Ordena as categorias da que tem mais alugueres para a que tem menos
        List<Map.Entry<String, Integer>> lista = new ArrayList<>(contagens.entrySet());
        lista.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int maxValor = lista.isEmpty() ? 0 : lista.get(0).getValue();
        int larguraBarra = 20;

        // Monta a tabela com barra gráfica e valores de receita
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("=".repeat(75)).append("\n");
        sb.append("           RECEITA POR CATEGORIA\n");
        sb.append("=".repeat(75)).append("\n");
        sb.append(String.format("  %-16s %-22s %-8s  %s%n", "Categoria", "", "Alugueres", "Receita"));
        sb.append("-".repeat(75)).append("\n");

        for (Map.Entry<String, Integer> e : lista) {
            String cat = e.getKey();
            String barra = gerarBarra(e.getValue(), maxValor, larguraBarra);
            sb.append(String.format("  %-16s %s %-8d  %,.0f Kz%n",
                    cat, barra, e.getValue(), receitas.get(cat)));
        }
        sb.append("=".repeat(75)).append("\n");
        return sb.toString();
    }

    // ─── Clientes mais activos ───────────────────────────────────────────────────

    // Mostra quais os clientes com mais alugueres feitos e quanto gastaram no total
    public String relatorioClientesMaisActivos() {
        if (alugueres.isEmpty()) return "Ainda não há alugueres para analisar.\n";

        // Conta os alugueres de cada cliente e soma o valor total gasto
        Map<String, Integer> contagem = new LinkedHashMap<>();
        Map<String, Double> gastos = new LinkedHashMap<>();

        for (Aluguer a : alugueres) {
            String nome = a.getCliente().getNome();
            contagem.put(nome, contagem.getOrDefault(nome, 0) + 1);
            gastos.put(nome, gastos.getOrDefault(nome, 0.0) + a.getTotalPago());
        }

        // Ordena os clientes do mais activo para o menos activo
        List<Map.Entry<String, Integer>> lista = new ArrayList<>(contagem.entrySet());
        lista.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int maxValor = lista.isEmpty() ? 0 : lista.get(0).getValue();
        int larguraBarra = 20;

        // Monta a tabela com barra gráfica e total gasto por cliente
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("=".repeat(75)).append("\n");
        sb.append("            CLIENTES MAIS ACTIVOS\n");
        sb.append("=".repeat(75)).append("\n");
        sb.append(String.format("  %-24s %-22s %-8s  %s%n", "Cliente", "", "Alugueres", "Total Gasto"));
        sb.append("-".repeat(75)).append("\n");

        for (Map.Entry<String, Integer> e : lista) {
            String barra = gerarBarra(e.getValue(), maxValor, larguraBarra);
            sb.append(String.format("  %-24s %s %-8d  %,.0f Kz%n",
                    e.getKey(), barra, e.getValue(), gastos.get(e.getKey())));
        }
        sb.append("=".repeat(75)).append("\n");
        return sb.toString();
    }

    // ─── Auxiliares ──────────────────────────────────────────────────────────────

    // Gera uma barra de '#' proporcional ao valor em relação ao máximo
    // Exemplo: valor=3, maxValor=6, largura=20 -> "##########          "
    private String gerarBarra(int valor, int maxValor, int largura) {
        if (maxValor == 0) return " ".repeat(largura);
        int preenchido = (int) Math.round((double) valor / maxValor * largura);
        if (preenchido < 1 && valor > 0) preenchido = 1; // garante pelo menos um '#' se houver valor
        return "#".repeat(preenchido) + " ".repeat(largura - preenchido);
    }

    // ─── Exportação para ficheiro ─────────────────────────────────────────────────

    // Guarda o conteúdo de um relatório num ficheiro .txt dentro da pasta "relatorios"
    public void exportarRelatorio(String titulo, String conteudo) {
        String pasta = "relatorios";
        File dir = new File(pasta);

        // Cria a pasta se ainda não existir
        if (!dir.exists()) dir.mkdirs();

        // O nome do ficheiro inclui a data e hora para não sobrescrever relatórios anteriores
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss"));
        String nomeFicheiro = pasta + "/" + titulo.replaceAll("\\s+", "_") + "_" + timestamp + ".txt";

        try (BufferedWriter w = new BufferedWriter(new FileWriter(nomeFicheiro))) {
            w.write(conteudo);
            System.out.println("Relatório exportado: " + nomeFicheiro);
        } catch (IOException e) {
            System.out.println("Erro ao exportar relatório: " + e.getMessage());
        }
    }

    // Acrescenta uma linha separadora ao StringBuilder com o tamanho indicado
    private void linha(StringBuilder sb, int tamanho) {
        sb.append("\n").append("=".repeat(tamanho)).append("\n");
    }
}

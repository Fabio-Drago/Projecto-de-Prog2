package classes;

public class Carro {

    // Categorias baseadas no catálogo Sixt: Económico, Compacto, Berlina, SUV, Premium, Carrinha
    public enum Categoria {
        ECONOMICO("Económico", 20000, 30000),
        COMPACTO("Compacto", 30000, 45000),
        BERLINA("Berlina", 40000, 55000),
        CARRINHA("Carrinha", 45000, 60000),
        SUV("SUV", 55000, 75000),
        PREMIUM("Premium", 70000, 90000);

        private final String nome;
        private final double precoMinimo;
        private final double precoMaximo;

        Categoria(String nome, double precoMinimo, double precoMaximo) {
            this.nome = nome;
            this.precoMinimo = precoMinimo;
            this.precoMaximo = precoMaximo;
        }

        public String getNome() { return nome; }
        public double getPrecoMinimo() { return precoMinimo; }
        public double getPrecoMaximo() { return precoMaximo; }

        // Devolve categoria pelo índice (1-based) para uso no menu
        public static Categoria porIndice(int indice) {
            Categoria[] valores = values();
            if (indice < 1 || indice > valores.length) return null;
            return valores[indice - 1];
        }

        public static void listarCategorias() {
            System.out.println("Categorias disponíveis:");
            Categoria[] valores = values();
            for (int i = 0; i < valores.length; i++) {
                System.out.printf("  %d. %-12s | %,.0f Kz - %,.0f Kz/dia%n",
                        i + 1, valores[i].getNome(),
                        valores[i].getPrecoMinimo(), valores[i].getPrecoMaximo());
            }
        }
    }

    private int id;
    private String marca;
    private String modelo;
    private int anoFabrico;
    private Categoria categoria;
    private boolean disponivel;
    private double precoPorDia;

    public Carro(int id, String marca, String modelo, int anoFabrico, Categoria categoria, boolean disponivel, double precoPorDia) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabrico = anoFabrico;
        this.categoria = categoria;
        this.disponivel = disponivel;
        this.precoPorDia = precoPorDia;
    }

    public int getId() { return id; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public int getAnoFabrico() { return anoFabrico; }
    public Categoria getCategoria() { return categoria; }
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
    public double getPrecoPorDia() { return precoPorDia; }

    @Override
    public String toString() {
        return String.format("ID: %d | %s %s (%d) | Categoria: %-12s | %,.0f Kz/dia | %s",
                id, marca, modelo, anoFabrico,
                categoria.getNome(),
                precoPorDia,
                disponivel ? "Disponível" : "Alugado");
    }
}

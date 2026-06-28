package classes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Aluguer {

    private int id;
    private Cliente cliente;
    private Carro carro;
    private LocalDate dataInicio;
    private LocalDate dataFim;      // previsão de devolução (definida no início)
    private LocalDate dataDevolucao; // data real de devolução (preenchida ao devolver)
    private int numeroDias;
    private double totalPago;
    private boolean activo;

    public Aluguer(int id, Cliente cliente, Carro carro, LocalDate dataInicio, int numeroDias, boolean activo) {
        this.id = id;
        this.cliente = cliente;
        this.carro = carro;
        this.dataInicio = dataInicio;
        this.numeroDias = numeroDias;
        this.dataFim = dataInicio.plusDays(numeroDias);
        this.totalPago = 0;
        this.activo = activo;
    }

    // Construtor usado ao carregar do ficheiro (inclui todos os campos)
    public Aluguer(int id, Cliente cliente, Carro carro, LocalDate dataInicio, LocalDate dataFim,
                   LocalDate dataDevolucao, int numeroDias, double totalPago, boolean activo) {
        this.id = id;
        this.cliente = cliente;
        this.carro = carro;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.dataDevolucao = dataDevolucao;
        this.numeroDias = numeroDias;
        this.totalPago = totalPago;
        this.activo = activo;
    }

    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Carro getCarro() { return carro; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public int getNumeroDias() { return numeroDias; }
    public double getTotalPago() { return totalPago; }
    public boolean isActivo() { return activo; }

    public void setActivo(boolean activo) { this.activo = activo; }
    public void setTotalPago(double totalPago) { this.totalPago = totalPago; }
    public void setDataDevolucao(LocalDate data) { this.dataDevolucao = data; }

    // Calcula o total com base nos dias previstos ou reais ao devolver
    public double calcularTotal(LocalDate dataDevolucaoEfetiva) {
        long diasReais = ChronoUnit.DAYS.between(dataInicio, dataDevolucaoEfetiva);
        if (diasReais < 1) diasReais = 1; // mínimo 1 dia
        return diasReais * carro.getPrecoPorDia();
    }

    @Override
    public String toString() {
        String estado = activo ? "Activo" : "Concluído";
        String devolucaoStr = (dataDevolucao != null) ? dataDevolucao.toString() : "—";
        return String.format("ID: %d | Cliente: %-20s | Carro: %s %s | Início: %s | Previsão: %s | Devolução: %s | Dias: %d | Total: %,.0f Kz | %s",
                id, cliente.getNome(), carro.getMarca(), carro.getModelo(),
                dataInicio, dataFim, devolucaoStr,
                numeroDias, totalPago, estado);
    }
}

package app;

import sistema.Sistema;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static Sistema sistema = new Sistema();

    public static void main(String[] args) {

        sistema.carregarDados();

        int opcao;

        do {
            System.out.println("\n===== SISTEMA DE ALUGUER DE CARROS =====");
            System.out.println("1. Clientes");
            System.out.println("2. Carros");
            System.out.println("3. Alugueres");
            System.out.println("4. Ver Carros Disponiveis");
            System.out.println("5. Guardar Dados");
            System.out.println("0. Sair");
            System.out.print("Opcao: ");

            opcao = lerInteiro();

            switch (opcao) {
                case 1:
                    menuClientes();
                    break;
                case 2:
                    menuCarros();
                    break;
                case 3:
                    menuAlugueres();
                    break;
                case 4:
                    sistema.listarCarrosDisponiveis();
                    break;
                case 5:
                    sistema.guardarDados();
                    break;
                case 0:
                    sistema.guardarDados();
                    System.out.println("Programa encerrado.");
                    break;
                default:
                    System.out.println("Opcao invalida. Tente novamente.");
            }

        } while (opcao != 0);

        scanner.close();
    }

    private static void menuClientes() {
        int opcao;

        do {
            System.out.println("\n===== CLIENTES =====");
            System.out.println("1. Registar Cliente");
            System.out.println("2. Listar Clientes");
            System.out.println("0. Voltar");
            System.out.print("Opcao: ");

            opcao = lerInteiro();

            switch (opcao) {
                case 1:
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine().trim();

                    System.out.print("Telefone: ");
                    String telefone = scanner.nextLine().trim();

                    sistema.adicionarCliente(nome, telefone);
                    break;
                case 2:
                    sistema.listarClientes();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }

        } while (opcao != 0);
    }

    private static void menuCarros() {
        int opcao;

        do {
            System.out.println("\n===== CARROS =====");
            System.out.println("1. Registar Carro");
            System.out.println("2. Listar Carros");
            System.out.println("0. Voltar");
            System.out.print("Opcao: ");

            opcao = lerInteiro();

            switch (opcao) {
                case 1:
                    System.out.print("Marca: ");
                    String marca = scanner.nextLine().trim();

                    System.out.print("Modelo: ");
                    String modelo = scanner.nextLine().trim();

                    double preco = -1;
                    while (preco <= 0) {
                        System.out.print("Preco por dia (Kz): ");
                        preco = lerDouble();
                        if (preco <= 0) {
                            System.out.println("O preco tem de ser maior que zero.");
                        }
                    }

                    sistema.adicionarCarro(marca, modelo, preco);
                    break;
                case 2:
                    sistema.listarCarros();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }

        } while (opcao != 0);
    }

    private static void menuAlugueres() {
        int opcao;

        do {
            System.out.println("\n===== ALUGUERES =====");
            System.out.println("1. Realizar Aluguer");
            System.out.println("2. Devolver Carro");
            System.out.println("3. Listar Alugueres");
            System.out.println("0. Voltar");
            System.out.print("Opcao: ");

            opcao = lerInteiro();

            switch (opcao) {
                case 1:
                    System.out.print("ID do cliente: ");
                    int idCliente = lerInteiro();

                    System.out.print("ID do carro: ");
                    int idCarro = lerInteiro();

                    sistema.realizarAluguer(idCliente, idCarro);
                    break;
                case 2:
                    System.out.print("ID do aluguer: ");
                    int idAluguer = lerInteiro();

                    sistema.devolverCarro(idAluguer);
                    break;
                case 3:
                    sistema.listarAlugueres();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }

        } while (opcao != 0);
    }

    // Le um inteiro com tolerancia a erros de input
    private static int lerInteiro() {
        while (true) {
            String linha = scanner.nextLine().trim();
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.print("Valor invalido. Introduza um numero: ");
            }
        }
    }

    // Le um double com tolerancia a erros de input
    private static double lerDouble() {
        while (true) {
            String linha = scanner.nextLine().trim().replace(",", ".");
            try {
                return Double.parseDouble(linha);
            } catch (NumberFormatException e) {
                System.out.print("Valor invalido. Introduza um numero: ");
            }
        }
    }
}

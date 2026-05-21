package app;

import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        int opcao;

        do {
            System.out.println("\n===== SISTEMA DE ALUGUER DE CARROS =====");
            System.out.println("1. Login como Cliente");
            System.out.println("2. Login como Administrador");
            System.out.println("0. Sair");
            System.out.print("Opcao: ");

            opcao = lerInteiro();

            switch (opcao) {
                case 1:
                    menuClienteLogin();
                    break;
                case 2:
                    menuAdministrador();
                    break;
                case 0:
                    System.out.println("Programa encerrado.");
                    break;
                default:
                    System.out.println("Opcao invalida. Tente novamente.");
            }

        } while (opcao != 0);

        scanner.close();
    }

    private static void menuClienteLogin() {
        System.out.print("Introduza o seu ID de cliente: ");
        int idCliente = lerInteiro();

        menuCliente();
    }

    private static void menuCliente() {
        int opcao;

        do {
            System.out.println("\n===== MENU DO CLIENTE =====");
            System.out.println("1. Ver Carros Disponiveis");
            System.out.println("2. Realizar Aluguer");
            System.out.println("3. Devolver Carro");
            System.out.println("0. Sair");
            System.out.print("Opcao: ");

            opcao = lerInteiro();

            switch (opcao) {
                case 1:
                    System.out.println();
                    System.out.println("Em desenvolvimento...");
                    break;
                case 2:
                    System.out.println();
                    System.out.println("Em desenvolvimento...");
                    break;
                case 3:
                    System.out.println();
                    System.out.println("Em desenvolvimento...");
                    break;
                case 0:
                    System.out.println("Logout realizado.");
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }

        } while (opcao != 0);
    }

    private static void menuAdministrador() {
        int opcao;

        do {
            System.out.println("\n===== MENU ADMINISTRADOR =====");
            System.out.println("1. Registar Carro");
            System.out.println("2. Listar Carros");
            System.out.println("3. Registar Cliente");
            System.out.println("4. Listar Clientes");
            System.out.println("5. Listar Alugueres");
            System.out.println("6. Guardar Dados");
            System.out.println("0. Sair");
            System.out.print("Opcao: ");

            opcao = lerInteiro();

            switch (opcao) {
                case 1:
                    System.out.println();
                    System.out.println("Em desenvolvimento...");
                    break;
                case 2:
                    System.out.println();
                    System.out.println("Em desenvolvimento...");
                    break;
                case 3:
                    System.out.println();
                    System.out.println("Em desenvolvimento...");
                    break;
                case 4:
                    System.out.println();
                    System.out.println("Em desenvolvimento...");
                    break;
                case 5:
                    System.out.println();
                    System.out.println("Em desenvolvimento...");
                    break;
                case 6:
                    System.out.println();
                    System.out.println("Em desenvolvimento...");
                    break;
                case 0:
                    System.out.println("Logout realizado.");
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
}

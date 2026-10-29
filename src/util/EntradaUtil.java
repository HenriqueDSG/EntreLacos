package util;

import java.util.Scanner;

public class EntradaUtil {

    private static final Scanner scanner = new Scanner(System.in);

    public static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    public static int lerInteiro(String mensagem) {
        int valor = 0;
        boolean entradaValida;

        do {
            entradaValida = true;
            try {
                String entrada = lerTexto(mensagem);
                valor = Integer.parseInt(entrada);
            } 
            catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
                entradaValida = false;
            }
        } 
        while (!entradaValida);

        return valor;
    }

    public static void limparTela() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } 
            else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } 
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } 
        catch (Exception e) {
            System.out.println("\n".repeat(30));
        }
    }

    public static void aguardarEnter() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}
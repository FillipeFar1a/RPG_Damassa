package util;

public class Efeitos {

    public static void limparTela() {
        for (int i = 0; i < 50; i++) System.out.println();
    }

    public static void esperar(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    public static void textoDigitando(String[] linhas, int delayChar, int delayLinha) {
        limparTela();
        System.out.println();
        for (String linha : linhas) {
            for (char c : linha.toCharArray()) {
                System.out.print(c);
                esperar(delayChar);
            }
            System.out.println();
            esperar(delayLinha);
        }
    }
}

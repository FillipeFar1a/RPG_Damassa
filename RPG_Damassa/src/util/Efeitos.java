package util;

public final class Efeitos {

    private Efeitos() {}

    public static void limparTela() {
        System.out.println("\n".repeat(50));
    }

    public static void esperar(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    public static void textoDigitando(String[] linhas, int delayCharMs, int delayLinhaMs) {
        if (linhas == null) return;
        for (String l : linhas) {
            if (l == null) l = "";
            for (int i = 0; i < l.length(); i++) {
                System.out.print(l.charAt(i));
                esperar(Math.max(0, delayCharMs));
            }
            System.out.println();
            esperar(Math.max(0, delayLinhaMs));
        }
    }
}

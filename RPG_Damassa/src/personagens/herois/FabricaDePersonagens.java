package personagens.herois;

import personagens.Personagem;

public class FabricaDePersonagens {

    public static Personagem criarPorIndice(int indice) {
        // índice 1..9 (na ordem do menu que você exibir)
        return switch (indice) {
            case 1 -> new Ashe();
            case 2 -> new Tryndamere();
            case 3 -> new Braum();
            case 4 -> new Aurora();
            case 5 -> new Udyr();
            case 6 -> new Gragas();
            case 7 -> new LeeSin();
            case 8 -> new Ryze();
            case 9 -> new Olaf();
            default -> null;
        };
    }

    public static String rotuloPorIndice(int indice) {
        return switch (indice) {
            case 1 -> "Ashe — Arqueira";
            case 2 -> "Tryndamere — Guerreiro";
            case 3 -> "Braum — Tank";
            case 4 -> "Aurora — Maga";
            case 5 -> "Udyr — Druída";
            case 6 -> "Gragas — Bêbado";
            case 7 -> "Lee Sin — Monge Cego";
            case 8 -> "Ryze — Mago";
            case 9 -> "Olaf — Berserker";
            default -> "Desconhecido";
        };
    }

    public static int total() { return 9; }
}

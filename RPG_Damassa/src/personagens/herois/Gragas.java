package personagens.herois;

import personagens.Personagem;
import java.util.Random;
import java.util.Scanner;

public class Gragas extends Personagem {
    public Gragas() {
        super("Gragas", "Bêbado", 38, 14, 8, 4, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "Gragas procurava o barril perfeito e encontrou… tempestades.",
                "Se o gelo vai quebrar, que seja com estilo — e muitos goles.",
                "Ele brinda à pancadaria e tropeça rumo ao trovão."
        };
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha a habilidade de Gragas:");
        System.out.println("1 -");
        System.out.println("2 -");
        System.out.println("3 -");
        System.out.print("Digite o número da habilidade: ");
        int escolha = scanner.nextInt();

        int custo = switch (escolha) {
            case 1 -> 6;
            case 2 -> 8;
            case 3 -> 7;
            default -> 0;
        };

        if (this.getPm() < custo) {
            System.out.println("Mana insuficiente! (" + this.getPm() + "/" + this.getPmMax() + ")");
            return;
        }

        this.gastarMana(custo);
    }
}

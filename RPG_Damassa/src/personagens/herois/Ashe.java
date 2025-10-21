package personagens.herois;

import personagens.Personagem;

import java.util.Random;
import java.util.Scanner;

public class Ashe extends Personagem {

    private int turnosBuff = 0; // controla duração de buff de defesa
    private int turnosDebuff = 0; // controla duração de debuff do inimigo

    public Ashe() {
        super("Ashe", "Arqueira", 30, 18, 7, 4, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "Ashe sente o vento cortar o gelo de Freljord.",
                "As tribos estão inquietas; os Flagelos tomam as rotas de caça.",
                "Se o trovão despertar, não restará reino para liderar."
        };
    }
    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha uma habilidade:");
        System.out.println("1 -Enchurrada de Flechas (ataque físico com 70% do ataque - Ataca 3 vezes)");
        System.out.println("2 - Flecha Congelada (Por 3 turnos, faz os ataques de Ashe terem chance de congelar o inimigo )");
        System.out.println("3 - Disparo Preciso (Dispara uma flecha gigante congelada, possui chance de congelamento maior)");
        System.out.print("Digite o número da habilidade: ");
        int escolha = scanner.nextInt();

        Random random = new Random();

        switch (escolha) {
            case 1 -> { // Rajada Gélida
                int dano = (int) (this.getAtk() * 0.7) - alvo.getDef();
                if (dano < 0) dano = 0;
                alvo.setPv(alvo.getPv() - dano);
                System.out.println(this.getNome() + " conjura uma RAJADA GÉLIDA!");
                System.out.println(alvo.getNome() + " sofre " + dano + " de dano congelante!");
            }

            case 2 -> { // Nevasca Congelante
                int dano = (int) (this.getAtk() * 1.0) - alvo.getDef();
                System.out.println(this.getNome() + " invoca uma NEVASCA CONGELANTE!");
                System.out.println(alvo.getNome() + " tem seu ataque e defesa reduzidos!");
                turnosDebuff = 2;
            }

            case 3 -> { // Escudo de Cristal
                if (turnosBuff == 0) {
                    System.out.println(this.getNome() + " envolve-se em um ESCUDO DE CRISTAL!");
                    this.setDef(this.getDef() + 5);
                    turnosBuff = 3;
                } else {
                    System.out.println("O escudo de Aurora já está ativo!");
                }
            }

            default -> System.out.println("Habilidade inválida, Ashe perde o turno!");
        }
    }
}

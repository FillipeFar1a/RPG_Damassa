package personagens.herois;

import personagens.Personagem;

import java.util.Random;
import java.util.Scanner;


public class Ashe extends Personagem {

    private boolean flechasCongeladasAtivas = false;
    private int turnosCongelamento = 0;
    private Random random = new Random();
    private int turnosBuff = 0; // controla duração de buff de defesa
    private int turnosDebuff = 0; // controla duração de debuff do inimigo

    public Ashe() {
        super("Ashe", "Arqueira", 30, 18, 7, 4, 1, 20);
    }

    @Override
    public String[] intro() {
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
        System.out.println("1 -Enxurrada de Flechas (ataque físico com 70% do ataque - Ataca 3 vezes)");
        System.out.println("2 - Flecha Congelada (Por 3 turnos, faz os ataques de Ashe terem chance de congelar o inimigo )");
        System.out.println("3 - Disparo Preciso (Dispara uma flecha gigante congelada, possui chance de congelamento maior)");
        System.out.print("Digite o número da habilidade: ");
        int escolha = scanner.nextInt();

        int custo = switch (escolha) {
            case 1 -> 4;
            case 2 -> 6;
            case 3 -> 10;
            default -> 0;
        };

        if (this.getPm() < custo) {
            System.out.println("Mana insuficiente! (" + this.getPm() + "/" + this.getPmMax() + ")");
            return;
        }

        this.gastarMana(custo);

        Random random = new Random();

        switch (escolha) {
            case 1 -> { // Enxurrada de Flechas
                System.out.println(this.getNome() + " dispara uma ENXURRADA DE FLECHAS!");

                for (int i = 1; i <= 3; i++) {
                    int dano = (int) (this.getAtk() * 0.7) - alvo.getDef();
                    if (dano < 0) dano = 0;
                    alvo.setPv(alvo.getPv() - dano);
                    System.out.println("Flecha " + i + " acerta " + alvo.getNome() + " causando " + dano + " de dano!");

                    if (flechasCongeladasAtivas) {
                        int chanceCongelar = random.nextInt(100);
                        if (chanceCongelar < 25) {
                            alvo.setCongelado(true);
                            System.out.println(alvo.getNome() + " foi CONGELADO pelas flechas gélidas!");
                            break;
                        }
                    }
                    try { Thread.sleep(400); } catch (InterruptedException ignored) {}
                }
            }


            case 2 -> { // Flechas Congeladas
                System.out.println(this.getNome() + " Encanta seu arco com gelo, agora seus ataques podem congelar inimigo!");
                flechasCongeladasAtivas = true;
                turnosCongelamento = 3;
            }

            case 3 -> { // Disparo Preciso
                int dano = (int) (this.getAtk() * 1.8) - alvo.getDef();
                if (dano < 0) dano = 0;
                alvo.setPv(alvo.getPv() - dano);
                System.out.println(this.getNome() + " Dispara uma Flecha Precisa!");
                System.out.println(alvo.getNome() + " sofreu " + dano + "de dano!");
            }
            default -> System.out.println("Ashe hesita e abaixa o arco...");
        }
        System.out.println("PM restante: " + this.getPm() + "/" + this.getPmMax());
    }
}
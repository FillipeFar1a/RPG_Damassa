package personagens.herois;

import personagens.Personagem;
import java.util.Random;
import java.util.Scanner;

public class Udyr extends Personagem {

    private boolean escudoAtivo = false;
    private int turnosEscudo = 0;
    private int turnosDebuff = 0;
    private Random random = new Random();

    public Udyr() {
        super("Udyr", "Xamã Espiritual", 36, 25, 7, 5, 1, 20);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "Udyr caminha entre o espiritual e o terreno, ouvindo o chamado dos deuses do Freljord.",
                "Sua alma é o elo entre homem e fera, entre carne e espírito.",
                "Com o poder dos deuses antigos, ele protege o equilíbrio das terras congeladas."
        };
    }

    // Atualiza buffs/debuffs por turno
    public void atualizarEfeitos() {
        if (escudoAtivo) {
            turnosEscudo--;
            if (turnosEscudo <= 0) {
                escudoAtivo = false;
                System.out.println("A proteção de Illdhaurg se dissipa.");
            }
        }
        if (turnosDebuff > 0) {
            turnosDebuff--;
            if (turnosDebuff == 0) {
                System.out.println("O ataque do inimigo volta ao normal.");
            }
        }
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha o espírito a ser canalizado por Udyr:");
        System.out.println("1 - Fúria de Ornn (160% de dano, 25% chance de atordoar)");
        System.out.println("2 - Brisa Invernal de Anivia (120% de dano mágico, reduz ataque do inimigo)");
        System.out.println("3 - Espírito de Illdhaurg (escudo + cura)");
        System.out.print("Digite o número da habilidade: ");
        int escolha = scanner.nextInt();

        int custo = switch (escolha) {
            case 1 -> 4;
            case 2 -> 8;
            case 3 -> 6;
            default -> 0;
        };

        if (this.getPm() < custo) {
            System.out.println("Mana insuficiente! (" + this.getPm() + "/" + this.getPmMax() + ")");
            return;
        }

        this.gastarMana(custo);


        switch (escolha) {
            case 1 -> { // Fúria de Ornn
                int dano = (int) (this.getAtk() * 1.6) - alvo.getDef();
                if (dano < 0) dano = 0;
                alvo.setPv(alvo.getPv() - dano);
                System.out.println(this.getNome() + " canaliza a Fúria de Ornn, o Deus da Forja!");
                System.out.println(alvo.getNome() + " é atingido por marteladas flamejantes e sofre " + dano + " de dano!");
                int chanceAtordoar = random.nextInt(100);
                if (chanceAtordoar < 25) {
                    alvo.setCongelado(true); // usa como status de "atordoado"
                    System.out.println(alvo.getNome() + " foi ATORDOADO pelo impacto divino e perde o próximo turno!");
                }
            }

            case 2 -> { // Brisa invernal de Anivia
                int dano = (int) (this.getAtk() * 1.2) - (alvo.getDef() / 2);
                if (dano < 0) dano = 0;
                alvo.setPv(alvo.getPv() - dano);
                System.out.println(this.getNome() + " invoca o Inverno de Anivia");
                System.out.println("Rajadas gélidas envolvem o campo, atingindo " + alvo.getNome() + " com " + dano + " de dano mágico!");
                alvo.setAtk(Math.max(1, alvo.getAtk() - 3));
                turnosDebuff = 2;
                System.out.println(alvo.getNome() + " está enfraquecido pelos ventos congelantes!");
            }

            case 3 -> { // Espírito de Illdhaurg
                if (!escudoAtivo) {
                    escudoAtivo = true;
                    turnosEscudo = 2;
                    int cura = (int) (this.getPvMax() * 0.1);
                    this.setPv(this.getPv() + cura);
                    System.out.println(this.getNome() + " invoca o ESPÍRITO DO JAVALI DE FERRO!");
                    System.out.println("Sua pele endurece como aço, reduzindo o dano recebido e curando " + cura + " PV!");
                } else {
                    System.out.println("A bênção do Javali de Ferro já protege Udyr!");
                }
            }

            default -> System.out.println("Udyr perde a sintonia espiritual e hesita...");
        }
    }

    @Override
    public void receberDano(int danoBruto) {
        if (escudoAtivo) {
            danoBruto /= 2;
        }
        super.receberDano(danoBruto);
    }
}

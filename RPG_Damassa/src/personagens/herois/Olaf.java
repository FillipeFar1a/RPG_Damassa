package personagens.herois;

import personagens.Personagem;
import java.util.Random;
import java.util.Scanner;

public class Olaf extends Personagem {

    private int turnosBuff = 0;
    private int turnosSangramento = 0;
    private int sangramentoDano = 0;
    private Random rand = new Random();

    public Olaf() {
        super("Olaf", "Berserker", 36, 12, 9, 3, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "Olaf ouviu que o fim se aproxima — e riu alto.",
                "Se o fim vier com trovões, melhor ainda.",
                "Ele corre em direção à tempestade como quem corre para casa."
        };
    }
    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha a habilidade de Olaf:");
        System.out.println("1 - Machado Ensanguentado (150% ATK, chance de sangramento) - Custo: 5 PM");
        System.out.println("2 -  Grito de Guerra (+3 ATK, +2 DEF por 3 turnos / dobra abaixo de 30% HP) - Custo: 6 PM");
        System.out.println("3 - Golpe Imprudente (180% ATK ignora DEF, perde 10% da vida máxima) - Custo: 4 PM");
        System.out.print("Digite o número da habilidade: ");
        int escolha = scanner.nextInt();

        int custo = switch (escolha) {
            case 1 -> 5;
            case 2 -> 6;
            case 3 -> 4;
            default -> 0;
        };

        if (this.getPm() < custo) {
            System.out.println("Mana insuficiente! (" + this.getPm() + "/" + this.getPmMax() + ")");
            return;
        }

        this.gastarMana(custo);

        switch (escolha) {
            case 1 -> { // Machado Ensanguentado
                int dano = (int) (this.getAtk() * 1.5) - alvo.getDef();
                if (dano < 0) dano = 0;
                alvo.setPv(alvo.getPv() - dano);
                System.out.println(this.getNome() + " lança o MACHADO ENSANGUENTADO!");
                System.out.println(alvo.getNome() + " sofre " + dano + " de dano!");

                if (Math.random()*100 < 25) { // 25% de chance
                    turnosSangramento = 2;
                    sangramentoDano = (int) (this.getAtk() * 0.1);
                    System.out.println("💉 " + alvo.getNome() + " está sangrando!");
                }
            }

            case 2 -> { // Grito de Guerra
                if (turnosBuff == 0) {
                    double hpPercent = (double) this.getPv() / this.getPvMax();
                    int bonusAtk = 3;
                    int bonusDef = 2;
                    if (hpPercent <= 0.3) {
                        bonusAtk *= 2;
                        bonusDef *= 2;
                        System.out.println("A fúria de Olaf aumenta! Seu Grito de Guerra dobra de poder!");
                    }
                    this.setAtk(this.getAtk() + bonusAtk);
                    this.setDef(this.getDef() + bonusDef);
                    turnosBuff = 3;
                    System.out.println(this.getNome() + " ruge e aumenta sua força por 3 turnos!");
                } else {
                    System.out.println("O grito de guerra de Olaf ainda ecoa!");
                }
            }

            case 3 -> { // Golpe Imprudente
                int dano = (int) (this.getAtk() * 1.8);
                alvo.setPv(alvo.getPv() - dano);
                int danoRecebido = (int) (this.getPvMax() * 0.1);
                this.setPv(this.getPv() - danoRecebido);
                System.out.println(this.getNome() + " desfere um GOLPE IMPRUDENTE!");
                System.out.println(alvo.getNome() + " sofre " + dano + " de dano ignorando a defesa!");
                System.out.println(this.getNome() + " perde " + danoRecebido + " PV em troca do poder bruto!");
            }

        }

    }
}

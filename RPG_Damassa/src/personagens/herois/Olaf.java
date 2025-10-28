package personagens.herois;

import itens.base.EfeitoPorTurno;
import personagens.Personagem;

import java.util.Random;
import java.util.Scanner;

public class Olaf extends Personagem {

    private final Random rand = new Random();

    public Olaf() {
        super("Olaf", "Berserker", 36, 12, 9, 3, 1, 20);
    }

    @Override
    public String[] intro() {
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
        System.out.println("1 - Machado Ensanguentado (150% ATK; 25% de Sangramento 2T) - 5 PM");
        System.out.println("2 - Grito de Guerra (+3 ATK, +2 DEF por 3T; dobra se ≤30% PV) - 6 PM");
        System.out.println("3 - Golpe Imprudente (180% ATK ignorando DEF; perde 10% PV Máx) - 4 PM");
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
            case 1 -> machadoEnsanguentado(alvo);
            case 2 -> gritoDeGuerra();
            case 3 -> golpeImprudente(alvo);
            default -> System.out.println("Olaf rosna, mas hesita por um segundo...");
        }

        System.out.println("PM restante: " + this.getPm() + "/" + this.getPmMax());
    }

    // ================== Habilidades ==================

    /**
     * 150% ATK efetivo como dano BRUTO; 25% de chance de aplicar Sangramento 2T.
     * O dano do sangramento é ~10% do ATK atual do Olaf por turno e IGNORA DEF.
     */
    private void machadoEnsanguentado(Personagem alvo) {
        int danoBruto = (int) Math.round(this.getAtkEfetivo() * 1.50);
        danoBruto += rand.nextInt(3) - 1; // leve variação
        if (danoBruto < 1) danoBruto = 1;

        System.out.println(this.getNome() + " lança o MACHADO ENSANGUENTADO!");
        alvo.receberDano(danoBruto);

        if (rand.nextInt(100) < 25) {
            final int sangramentoBase = Math.max(1, (int) Math.round(this.getAtkEfetivo() * 0.10));
            System.out.println("💉 " + alvo.getNome() + " está sangrando! (" + sangramentoBase + "/turno, 2T — ignora DEF)");
            alvo.adicionarEfeito(new EfeitoPorTurno() {
                private int restantes = 2;

                @Override
                public void aoInicioDoTurno(Personagem self, Personagem adversario) {
                    // Sangramento ignora DEF: aplica direto nos PV
                    self.setPv(self.getPv() - sangramentoBase);
                    System.out.println(self.getNome() + " sangra e perde " + sangramentoBase + " PV!");
                    restantes--;
                }

                @Override
                public String toString() {
                    return "Sangramento (" + sangramentoBase + "/T)";
                }
            }, 2);
        }
    }

    /**
     * +3 ATK e +2 DEF por 3 turnos (dobrado se Olaf estiver ≤30% do PV atual no momento do uso).
     * Restaura automaticamente ao fim.
     */
    private void gritoDeGuerra() {
        double hpPercent = (double) this.getPv() / this.getPvMax();
        int bonusAtk = 3;
        int bonusDef = 2;
        boolean furia = hpPercent <= 0.30;

        if (furia) {
            bonusAtk *= 2;
            bonusDef *= 2;
            System.out.println("A fúria de Olaf explode! O GRITO DE GUERRA dobra de poder!");
        } else {
            System.out.println(this.getNome() + " ruge — GRITO DE GUERRA!");
        }

        // aplica imediatamente
        this.setAtk(this.getAtk() + bonusAtk);
        this.addDef(+bonusDef);
        final int atkBuff = bonusAtk;
        final int defBuff = bonusDef;

        // agenda devolução
        this.adicionarEfeito(new EfeitoPorTurno() {
            private int restantes = 3;

            @Override
            public void aoInicioDoTurno(Personagem self, Personagem adversario) {
                restantes--;
                if (restantes == 0) {
                    self.setAtk(Math.max(0, self.getAtk() - atkBuff));
                    self.addDef(-defBuff);
                    System.out.println("O eco do grito se dissipa — os bônus de Olaf acabam.");
                }
            }

            @Override
            public String toString() {
                return "Grito de Guerra (+" + atkBuff + " ATK, +" + defBuff + " DEF)";
            }
        }, 3);
    }

    /**
     * 180% ATK efetivo ignorando DEF do alvo. Olaf perde 10% do PV Máx.
     * Para "ignorar DEF" mantendo a regra de receberDano subtrair DEF,
     * somamos a DEF do alvo ao dano BRUTO para compensar.
     */
    private void golpeImprudente(Personagem alvo) {
        int base = (int) Math.round(this.getAtkEfetivo() * 1.80);
        base += rand.nextInt(5) - 2;
        if (base < 1) base = 1;

        int danoBruto = base + alvo.getDef(); // compensa a subtração da DEF em receberDano -> ignora DEF
        System.out.println(this.getNome() + " desfere um GOLPE IMPRUDENTE!");
        alvo.receberDano(danoBruto);
        System.out.println(alvo.getNome() + " sofre " + base + " de dano efetivo (ignora DEF)!");

        int autoDano = Math.max(1, (int) Math.round(this.getPvMax() * 0.10));
        this.setPv(this.getPv() - autoDano);
        System.out.println(this.getNome() + " paga o preço do poder bruto e perde " + autoDano + " PV!");
    }
}

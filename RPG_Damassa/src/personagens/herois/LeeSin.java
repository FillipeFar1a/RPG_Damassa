package personagens.herois;

import personagens.Personagem;
<<<<<<< Updated upstream

public class LeeSin extends Personagem {
=======
import itens.base.EfeitoPorTurno;

import java.util.Random;
import java.util.Scanner;

public class LeeSin extends Personagem {

    // ===== Estados =====
    private boolean safeguardAtivo = false;   // redução de dano e lifesteal do Safeguard
    private boolean marcouAlvo = false;       // Onda Sônica -> habilita Golpe Resonante no próximo uso
    private final Random random = new Random();

>>>>>>> Stashed changes
    public LeeSin() {
        super("Lee Sin", "Monge Cego", 32, 16, 8, 4, 1, 20);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "Lee Sin não vê o gelo, mas sente o peso do silêncio.",
                "O mundo segura a respiração antes do trovão.",
                "Ele caminha em fé, para quebrar o ciclo da fúria."
        };
    }
<<<<<<< Updated upstream
=======

    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha a habilidade de Lee Sin:");
        System.out.println("1 - Onda Sônica / Golpe Resonante (combo) - 6 PM");
        System.out.println("2 - Safeguard (2T: -50% dano recebido + 20% vampirismo) - 8 PM");
        System.out.println("3 - Foco Interior (+3 ATK e +2 DEF por 3T) - 7 PM");
        System.out.print("Digite o número da habilidade: ");
        int escolha = scanner.nextInt();

        int custo = switch (escolha) {
            case 1 -> 6;
            case 2 -> 8;
            case 3 -> 7;
            default -> 0;
        };

        if (this.getPm() < custo) {
            System.out.println("Energia espiritual insuficiente! (" + this.getPm() + "/" + this.getPmMax() + ")");
            return;
        }

        this.gastarMana(custo);

        switch (escolha) {
            case 1 -> ondaSonicaOuGolpeResonante(alvo);
            case 2 -> safeguard();
            case 3 -> focoInterior();
            default -> System.out.println("Lee Sin respira fundo e aguarda o momento certo...");
        }

        System.out.println("PM restante: " + this.getPm() + "/" + this.getPmMax());
    }

    // ================== Habilidades ==================

    /**
     * Primeira vez: Onda Sônica (~130% ATK bruto), marca o alvo.
     * Se já marcado: Golpe Resonante (~200% ATK bruto) e consome a marca.
     * Dano é enviado como BRUTO; a DEF é aplicada em Personagem.receberDano (evita dupla DEF).
     * Enquanto Safeguard ativo, cura 20% do dano causado.
     */
    private void ondaSonicaOuGolpeResonante(Personagem alvo) {
        if (!marcouAlvo) {
            int danoBruto = (int) Math.round(this.getAtkEfetivo() * 1.30);
            // variação leve
            danoBruto += random.nextInt(3) - 1;
            if (danoBruto < 1) danoBruto = 1;

            System.out.println(this.getNome() + " lança uma ONDA SÔNICA!");
            alvo.receberDano(danoBruto);
            System.out.println("O som do inimigo ecoa — alvo MARCADO.");

            // lifesteal do safeguard (20% do dano bruto aproximado)
            if (safeguardAtivo) {
                int cura = Math.max(1, (int) Math.round(danoBruto * 0.20));
                this.setPv(this.getPv() + cura);
                System.out.println(this.getNome() + " canaliza o impacto e recupera " + cura + " PV (Safeguard).");
            }

            marcouAlvo = true;
        } else {
            int danoBruto = (int) Math.round(this.getAtkEfetivo() * 2.00);
            // variação leve
            danoBruto += random.nextInt(5) - 2;
            if (danoBruto < 1) danoBruto = 1;

            System.out.println(this.getNome() + " segue o som e executa o GOLPE RESONANTE!");
            alvo.receberDano(danoBruto);
            System.out.println("O impacto reverbera através do alvo!");

            if (safeguardAtivo) {
                int cura = Math.max(1, (int) Math.round(danoBruto * 0.20));
                this.setPv(this.getPv() + cura);
                System.out.println(this.getNome() + " converte força em vida: +" + cura + " PV (Safeguard).");
            }

            marcouAlvo = false; // consome a marca
        }
    }

    /**
     * Safeguard: por 2 turnos, -50% do dano recebido e 20% de vampirismo ofensivo.
     * Implementado com EfeitoPorTurno para ligar/desligar automaticamente.
     * Aplica também uma cura imediata leve (10% PV máx).
     */
    private void safeguard() {
        System.out.println(this.getNome() + " canaliza o SAFEGUARD! (-50% dano recebido, 20% vampirismo por 2T)");
        int cura = Math.max(1, (int) Math.round(this.getPvMax() * 0.10));
        this.setPv(this.getPv() + cura);
        System.out.println("A energia protetora cura " + cura + " PV.");

        this.safeguardAtivo = true;
        this.adicionarEfeito(new EfeitoPorTurno() {
            private int restantes = 2;

            @Override
            public void aoInicioDoTurno(Personagem self, Personagem adversario) {
                // mantém ativo enquanto durar
                ((LeeSin) self).safeguardAtivo = true;

                restantes--;
                if (restantes == 0) {
                    ((LeeSin) self).safeguardAtivo = false;
                    System.out.println("O Safeguard de Lee Sin se dissipa.");
                }
            }

            @Override
            public String toString() {
                return "Safeguard (-50% dano recebido, 20% vampirismo)";
            }
        }, 2);
    }

    /**
     * +3 ATK e +2 DEF por 3 turnos, com restauração automática.
     */
    private void focoInterior() {
        System.out.println(this.getNome() + " entra em estado de FOCO INTERIOR! (+3 ATK | +2 DEF por 3T)");

        // aplica imediatamente
        this.setAtk(this.getAtk() + 3);
        this.addDef(+2);

        this.adicionarEfeito(new EfeitoPorTurno() {
            private int restantes = 3;

            @Override
            public void aoInicioDoTurno(Personagem self, Personagem adversario) {
                restantes--;
                if (restantes == 0) {
                    // restaura
                    self.setAtk(Math.max(0, self.getAtk() - 3));
                    self.addDef(-2);
                    System.out.println("O foco interior de Lee Sin se desfaz.");
                }
            }

            @Override
            public String toString() {
                return "Foco Interior (+3 ATK | +2 DEF)";
            }
        }, 3);
    }

    // ===== Receber dano com Safeguard =====

    @Override
    public void receberDano(int danoBruto) {
        if (safeguardAtivo) {
            danoBruto = Math.max(1, danoBruto / 2); // reduz 50% do dano BRUTO
        }
        super.receberDano(danoBruto); // DEF aplicada aqui
    }
>>>>>>> Stashed changes
}

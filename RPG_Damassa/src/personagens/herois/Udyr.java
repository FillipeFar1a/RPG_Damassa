package personagens.herois;

import personagens.Personagem;
<<<<<<< Updated upstream

public class Udyr extends Personagem {
=======
import itens.base.EfeitoPorTurno;

import java.util.Random;
import java.util.Scanner;

public class Udyr extends Personagem {

    // Escudo de Illdhaurg: reduz 50% do dano recebido enquanto ativo
    private boolean escudoAtivo = false;

    private final Random random = new Random();

>>>>>>> Stashed changes
    public Udyr() {
        super("Udyr", "Druída", 34, 18, 7, 5, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "Os espíritos rugem em desespero, e Udyr os escuta a todos.",
                "Há um espírito que não fala — apenas morde o próprio rabo em fúria.",
                "Ele vai ao coração da tempestade apaziguar o urso enlouquecido."
        };
    }
<<<<<<< Updated upstream
=======

    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha o espírito a ser canalizado por Udyr:");
        System.out.println("1 - Fúria de Ornn (160% ATK; 25% de atordoar) - 4 PM");
        System.out.println("2 - Brisa Invernal de Anivia (120% ATK; -3 ATK do alvo por 2T) - 8 PM");
        System.out.println("3 - Espírito de Illdhaurg (2T: -50% dano recebido + cura 10% PV) - 6 PM");
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
            case 1 -> furiaDeOrnn(alvo);
            case 2 -> brisaDeAnivia(alvo);
            case 3 -> espiritoDeIlldhaurg();
            default -> System.out.println("Udyr perde a sintonia espiritual e hesita...");
        }

        System.out.println("PM restante: " + this.getPm() + "/" + this.getPmMax());
    }

    // ================== Habilidades ==================

    /** 160% do ATK efetivo como dano BRUTO; 25% de chance de atordoar (usa flag congelado). */
    private void furiaDeOrnn(Personagem alvo) {
        int danoBruto = (int) Math.round(this.getAtkEfetivo() * 1.60);
        danoBruto += random.nextInt(3) - 1; // leve variação
        if (danoBruto < 1) danoBruto = 1;

        System.out.println(this.getNome() + " canaliza a FÚRIA DE ORNN, o Deus da Forja!");
        alvo.receberDano(danoBruto);
        System.out.println(alvo.getNome() + " é esmagado por marteladas flamejantes!");

        if (random.nextInt(100) < 25) {
            alvo.setCongelado(true); // usamos como “atordoado”
            System.out.println(alvo.getNome() + " foi ATORDOADO e perderá o próximo turno!");
        }
    }

    /**
     * 120% do ATK efetivo como dano BRUTO.
     * Aplica -3 ATK no alvo por 2 turnos (sem mexer permanentemente em setAtk).
     * Implementado com EfeitoPorTurno que aplica modificarAtaqueTemporario(-3) a cada início de turno do alvo.
     */
    private void brisaDeAnivia(Personagem alvo) {
        int danoBruto = (int) Math.round(this.getAtkEfetivo() * 1.20);
        danoBruto += random.nextInt(3) - 1;
        if (danoBruto < 1) danoBruto = 1;

        System.out.println(this.getNome() + " invoca a BRISA INVERNAL DE ANIVIA!");
        alvo.receberDano(danoBruto);
        System.out.println("Rajadas gélidas envolvem o campo, enfraquecendo o inimigo!");

        // Debuff de ATK por 2 turnos (aplicado por turno via modificador temporário)
        alvo.adicionarEfeito(new EfeitoPorTurno() {
            private int restantes = 2;

            @Override
            public void aoInicioDoTurno(Personagem self, Personagem adversario) {
                self.modificarAtaqueTemporario(-3);
                restantes--;
                if (restantes == 0) {
                    System.out.println(self.getNome() + " sente o frio passar — seu ataque volta ao normal.");
                } else {
                    System.out.println(self.getNome() + " ainda sofre com os ventos de Anivia (-3 ATK neste turno).");
                }
            }

            @Override
            public String toString() {
                return "Brisa de Anivia (-3 ATK por turno)";
            }
        }, 2);
    }

    /**
     * Por 2 turnos, reduz 50% do dano recebido (aplicado em receberDano) e cura 10% do PV Máx agora.
     * Mantemos um EfeitoPorTurno apenas para ligar/desligar o escudo automaticamente.
     */
    private void espiritoDeIlldhaurg() {
        System.out.println(this.getNome() + " invoca o ESPÍRITO DE ILLDHAURG, o Javali de Ferro!");
        int cura = Math.max(1, (int) Math.round(this.getPvMax() * 0.10));
        this.setPv(this.getPv() + cura);
        System.out.println("Sua pele endurece como aço. Cura " + cura + " PV e reduz o dano por 2 turnos!");

        escudoAtivo = true;
        this.adicionarEfeito(new EfeitoPorTurno() {
            private int restantes = 2;

            @Override
            public void aoInicioDoTurno(Personagem self, Personagem adversario) {
                ((Udyr) self).escudoAtivo = true;
                restantes--;
                if (restantes == 0) {
                    ((Udyr) self).escudoAtivo = false;
                    System.out.println("A proteção de Illdhaurg se dissipa.");
                }
            }

            @Override
            public String toString() {
                return "Espírito de Illdhaurg (-50% dano recebido)";
            }
        }, 2);
    }

    // ================== Redução de dano do escudo ==================

    @Override
    public void receberDano(int danoBruto) {
        if (escudoAtivo) {
            danoBruto = Math.max(1, danoBruto / 2); // reduz 50% do dano BRUTO
        }
        super.receberDano(danoBruto); // DEF é aplicada aqui
    }
>>>>>>> Stashed changes
}

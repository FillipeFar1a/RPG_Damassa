package personagens.herois;

import personagens.Personagem;
<<<<<<< Updated upstream
=======
import itens.base.EfeitoPorTurno;

import java.util.Random;
import java.util.Scanner;
>>>>>>> Stashed changes

public class Gragas extends Personagem {

    private final Random random = new Random();

    public Gragas() {
        super("Gragas", "Bêbado", 38, 14, 8, 4, 1, 20);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "Gragas procurava o barril perfeito e encontrou… tempestades.",
                "Se o gelo vai quebrar, que seja com estilo — e muitos goles.",
                "Ele brinda à pancadaria e tropeça rumo ao trovão."
        };
    }
<<<<<<< Updated upstream
=======

    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha a habilidade de Gragas:");
        System.out.println("1 - Golpe de Barril (150% ATK; 30% de atordoar) - 6 PM");
        System.out.println("2 - Bebedeira (cura 20% PV e +4 DEF por 2 turnos) - 8 PM");
        System.out.println("3 - Barril Explosivo (200% ATK; 40% de atordoar; -2 DEF do alvo por 2 turnos) - 7 PM");
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

        switch (escolha) {
            case 1 -> golpeDeBarril(alvo);
            case 2 -> bebedeira();
            case 3 -> barrilExplosivo(alvo);
            default -> System.out.println("Gragas se atrapalha com o barril e perde o turno...");
        }

        System.out.println("PM restante: " + this.getPm() + "/" + this.getPmMax());
    }

    // ================== Habilidades ==================

    /** 150% do ATK efetivo como dano BRUTO; 30% de chance de atordoar (usa flag 'congelado'). */
    private void golpeDeBarril(Personagem alvo) {
        int danoBruto = (int)Math.round(this.getAtkEfetivo() * 1.50);
        danoBruto += random.nextInt(3) - 1; // variação leve
        if (danoBruto < 1) danoBruto = 1;

        System.out.println(this.getNome() + " parte pra cima com um GOLPE DE BARRIL!");
        alvo.receberDano(danoBruto);

        if (random.nextInt(100) < 30) {
            alvo.setCongelado(true); // reutilizando como “atordoado”
            System.out.println(alvo.getNome() + " ficou ATORDOADO e perderá o próximo turno!");
        }
    }

    /** Cura 20% do PV máx e concede +4 DEF por 2 turnos (restaura automaticamente ao fim). */
    private void bebedeira() {
        int cura = Math.max(1, (int)Math.round(this.getPvMax() * 0.20));
        this.setPv(this.getPv() + cura);
        System.out.println(this.getNome() + " dá um gole fundo! Recupera " + cura + " PV.");

        // aplica bônus imediato e agenda devolução
        this.addDef(+4);
        System.out.println(this.getNome() + " fica mais resistente! (+4 DEF por 2 turnos)");

        this.adicionarEfeito(new EfeitoPorTurno() {
            private int restantes = 2;

            @Override
            public void aoInicioDoTurno(Personagem self, Personagem adversario) {
                restantes--;
                if (restantes == 0) {
                    self.addDef(-4);
                    System.out.println("O efeito da bebedeira passa. A DEF extra de Gragas se dissipa.");
                }
            }

            @Override
            public String toString() {
                return "Bebedeira (+4 DEF)";
            }
        }, 2);
    }

    /**
     * 200% do ATK efetivo como dano BRUTO;
     * 40% de atordoar; -2 DEF do alvo por 2 turnos (com restauração automática).
     */
    private void barrilExplosivo(Personagem alvo) {
        int danoBruto = (int)Math.round(this.getAtkEfetivo() * 2.00);
        danoBruto += random.nextInt(5) - 2; // leve variação
        if (danoBruto < 1) danoBruto = 1;

        System.out.println(this.getNome() + " arremessa o BARRIL EXPLOSIVO!");
        alvo.receberDano(danoBruto);
        System.out.println(alvo.getNome() + " é lançado pelos ares com a explosão!");

        // 40% de atordoar
        if (random.nextInt(100) < 40) {
            alvo.setCongelado(true);
            System.out.println(alvo.getNome() + " está ATORDOADO e perderá o próximo turno!");
        }

        // -2 DEF no alvo por 2 turnos com restauração automática
        alvo.adicionarEfeito(new EfeitoPorTurno() {
            private boolean aplicado = false;
            private int restantes = 2;

            @Override
            public void aoInicioDoTurno(Personagem self, Personagem adversario) {
                if (!aplicado) {
                    self.addDef(-2);
                    aplicado = true;
                    System.out.println(self.getNome() + " ainda cambaleia da explosão (-2 DEF).");
                }
                restantes--;
                if (restantes == 0) {
                    self.addDef(+2);
                    System.out.println(self.getNome() + " se recompõe e a defesa retorna ao normal.");
                }
            }

            @Override
            public String toString() {
                return "Barril Explosivo: -2 DEF";
            }
        }, 2);
    }
>>>>>>> Stashed changes
}

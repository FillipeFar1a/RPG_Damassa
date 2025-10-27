package personagens.herois;

import personagens.EfeitoPorTurno;
import personagens.Personagem;

import java.util.Random;
import java.util.Scanner;

public class Tryndamere extends Personagem {

    private boolean furiaImortalAtiva = false; // "não pode morrer" (fica em 1 PV) enquanto ativo
    private final Random random = new Random();

    public Tryndamere() {
        super("Tryndamere", "Guerreiro", 36, 10, 9, 3, 1, 20);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "A fúria de Tryndamere não encontra descanso.",
                "Pesadelos com um rugido antigo o perseguem.",
                "Hoje, ele decidiu perseguir o trovão até o fim."
        };
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha a habilidade de Tryndamere:");
        System.out.println("1 - Corte Giratório (140% ATK; ignora 25% da DEF do alvo) - 6 PM");
        System.out.println("2 - Rasgo Implacável (120% ATK; -4 DEF no alvo por 3 turnos) - 8 PM");
        System.out.println("3 - Fúria Imortal (não pode morrer por 2 turnos) - 12 PM");
        System.out.print("Digite o número da habilidade: ");
        int escolha = scanner.nextInt();

        int custo = switch (escolha) {
            case 1 -> 6;
            case 2 -> 8;
            case 3 -> 12; // corrigido p/ bater com a descrição
            default -> 0;
        };

        if (this.getPm() < custo) {
            System.out.println("Mana insuficiente! (" + this.getPm() + "/" + this.getPmMax() + ")");
            return;
        }

        this.gastarMana(custo);

        switch (escolha) {
            case 1 -> corteGiratorio(alvo);
            case 2 -> rasgoImplacavel(alvo);
            case 3 -> furiaImortal();
            default -> System.out.println("Tryndamere hesita, consumido pela raiva...");
        }

        System.out.println("PM restante: " + this.getPm() + "/" + this.getPmMax());
    }

    // ========= Habilidades =========

    /**
     * 140% do ATK efetivo como dano BRUTO.
     * Para "ignorar 25% da DEF" mantendo a regra de Personagem.receberDano subtrair a DEF inteira,
     * somamos 25% da DEF do alvo ao dano BRUTO (compensação).
     */
    private void corteGiratorio(Personagem alvo) {
        int base = (int) Math.round(this.getAtkEfetivo() * 1.40);
        base += random.nextInt(3) - 1; // leve variação
        if (base < 1) base = 1;

        int defIgnorada = (int) Math.floor(alvo.getDef() * 0.25);
        int danoBruto = base + defIgnorada; // compensa a parte ignorada

        System.out.println(this.getNome() + " executa um CORTE GIRATÓRIO devastador!");
        alvo.receberDano(danoBruto); // DEF aplicada dentro de receberDano (efetivamente 75% da DEF)
        System.out.println("(O golpe ignora 25% da defesa de " + alvo.getNome() + ".)");
    }

    /**
     * 120% do ATK efetivo como dano BRUTO.
     * Aplica -4 DEF no alvo por 3 turnos (restaura ao final automaticamente).
     */
    private void rasgoImplacavel(Personagem alvo) {
        int danoBruto = (int) Math.round(this.getAtkEfetivo() * 1.20);
        danoBruto += random.nextInt(3) - 1;
        if (danoBruto < 1) danoBruto = 1;

        System.out.println(this.getNome() + " dilacera as defesas — RASGO IMPLACÁVEL!");
        alvo.receberDano(danoBruto);

        // Debuff de -4 DEF por 3 turnos, com restauração
        alvo.adicionarEfeito(new EfeitoPorTurno() {
            private boolean aplicado = false;
            private int restantes = 3;

            @Override
            public void aoInicioDoTurno(Personagem self, Personagem adversario) {
                if (!aplicado) {
                    self.addDef(-4);
                    aplicado = true;
                    System.out.println(self.getNome() + " sangra defesa: -4 DEF (3T).");
                }
                restantes--;
                if (restantes == 0) {
                    self.addDef(+4);
                    System.out.println(self.getNome() + " recompõe parte da armadura. A DEF retorna ao normal.");
                }
            }

            @Override
            public String toString() {
                return "Rasgo Implacável (-4 DEF)";
            }
        }, 3);
    }

    /**
     * Fúria Imortal por 2 turnos: se dano levaria a 0, fica em 1 PV.
     * Implementado com flag + efeito temporário para ligar/desligar.
     */
    private void furiaImortal() {
        if (furiaImortalAtiva) {
            System.out.println("Tryndamere já está em FÚRIA IMORTAL!");
            return;
        }

        System.out.println(this.getNome() + " entra em FÚRIA IMORTAL! (2 turnos)");
        furiaImortalAtiva = true;

        this.adicionarEfeito(new EfeitoPorTurno() {
            private int restantes = 2;

            @Override
            public void aoInicioDoTurno(Personagem self, Personagem adversario) {
                ((Tryndamere) self).furiaImortalAtiva = true; // garante ativo enquanto durar
                restantes--;
                if (restantes == 0) {
                    ((Tryndamere) self).furiaImortalAtiva = false;
                    System.out.println("A Fúria Imortal de Tryndamere se dissipa — a morte volta a ser uma ameaça.");
                }
            }

            @Override
            public String toString() {
                return "Fúria Imortal (não cai abaixo de 1 PV)";
            }
        }, 2);
    }

    // ========= Intercepta dano para aplicar o "não morrer" =========
    @Override
    public void receberDano(int danoBruto) {
        int pvAntes = this.getPv();
        super.receberDano(danoBruto);
        if (furiaImortalAtiva && pvAntes > 0 && this.getPv() <= 0) {
            this.setPv(1); // mantém 1 ponto de vida
            System.out.println(this.getNome() + " resiste à morte pela força da FÚRIA IMORTAL!");
        }
    }
}

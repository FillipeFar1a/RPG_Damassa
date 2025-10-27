package personagens.herois;

import personagens.Personagem;
<<<<<<< Updated upstream

public class Braum extends Personagem {
=======
import itens.base.EfeitoPorTurno;

import java.util.Random;
import java.util.Scanner;

public class Braum extends Personagem {

    // Escudo é aplicado reduzindo o dano BRUTO pela metade em receberDano()
    private boolean escudoAtivo = false;

    private final Random random = new Random();

>>>>>>> Stashed changes
    public Braum() {
        super("Braum", "Tank", 42, 10, 6, 7, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "Braum sorri, mas o norte não. Portas se trancam, lareiras se apagam.",
                "Ele jurou ser o escudo de Freljord — e um escudo não recua.",
                "Se o Urso de Mil Flagelos vier, encontrará uma parede inquebrável."
        };
    }
<<<<<<< Updated upstream
=======

    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha a habilidade de Braum:");
        System.out.println("1 - Pancada (150% do ATK; 20% de atordoar)");
        System.out.println("2 - O Escudo de Freljord (reduz 50% do dano recebido por 2 turnos)");
        System.out.println("3 - Coração do Norte (cura 20% PV e +5 DEF por 3 turnos)");
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
            case 1 -> pancada(alvo);
            case 2 -> escudoDeFreljord();
            case 3 -> coracaoDoNorte();
            default -> System.out.println("Habilidade inválida");
        }
        System.out.println("PM restante: " + this.getPm() + "/" + this.getPmMax());
    }

    // ===== Habilidades =====

    /** 150% do ATK efetivo como dano BRUTO; 20% de atordoar (usa o flag 'congelado' como stun). */
    private void pancada(Personagem alvo) {
        int danoBruto = (int)Math.round(this.getAtkEfetivo() * 1.50);
        // pequena variação opcional
        danoBruto += random.nextInt(3) - 1;
        if (danoBruto < 1) danoBruto = 1;

        System.out.println(this.getNome() + " desfere uma grande PANCADA!");
        alvo.receberDano(danoBruto); // DEF é aplicada em Personagem.receberDano

        if (random.nextInt(100) < 20) {
            alvo.setCongelado(true); // reutiliza como "atordoado"
            System.out.println(alvo.getNome() + " foi ATORDOADO e perderá o próximo turno!");
        }
    }

    /** Ativa escudo por 2 turnos. Redução de 50% é aplicada em receberDano(). */
    private void escudoDeFreljord() {
        if (escudoAtivo) {
            System.out.println("O escudo já está ativo!");
            return;
        }
        System.out.println(this.getNome() + " ergue o ESCUDO DE FRELJORD! (50% de redução por 2 turnos)");
        escudoAtivo = true;

        // Mantém ligado por 2 turnos e desliga ao expirar
        this.adicionarEfeito(new EfeitoPorTurno() {
            private int restantes = 2;

            @Override
            public void aoInicioDoTurno(Personagem self, Personagem adversario) {
                // enquanto existir, mantém ativo
                ((Braum)self).escudoAtivo = true;

                restantes--;
                if (restantes == 0) {
                    ((Braum)self).escudoAtivo = false;
                    System.out.println("A Muralha de Braum se dissipa, ele abaixa o escudo.");
                }
            }

            @Override
            public String toString() {
                return "Escudo de Freljord (redução 50%)";
            }
        }, 2);
    }

    /** Cura 20% do PV máx e concede +5 DEF por 3 turnos (restaura ao final). */
    private void coracaoDoNorte() {
        int cura = Math.max(1, (int)Math.round(this.getPvMax() * 0.20));
        this.setPv(this.getPv() + cura);
        System.out.println(this.getNome() + " invoca os poros de Freljord! Recupera " + cura + " PV.");

        // Aplica o bônus imediatamente e agenda a devolução
        this.addDef(+5);
        System.out.println(this.getNome() + " fortalece sua guarda! (+5 DEF por 3 turnos)");

        this.adicionarEfeito(new EfeitoPorTurno() {
            private int restantes = 3;

            @Override
            public void aoInicioDoTurno(Personagem self, Personagem adversario) {
                restantes--;
                if (restantes == 0) {
                    self.addDef(-5);
                    System.out.println("O bônus de defesa de Braum desaparece.");
                }
            }

            @Override
            public String toString() {
                return "Coração do Norte (+5 DEF)";
            }
        }, 3);
    }

    // ===== Redução de dano do escudo =====

    @Override
    public void receberDano(int danoBruto) {
        if (escudoAtivo) {
            danoBruto = Math.max(1, danoBruto / 2); // metade do dano bruto
        }
        super.receberDano(danoBruto); // DEF é aplicada aqui
    }
>>>>>>> Stashed changes
}

package personagens.herois;

import personagens.Personagem;
import itens.base.EfeitoPorTurno;

import java.util.Random;
import java.util.Scanner;

public class Aurora extends Personagem {

    private final Random random = new Random();

    public Aurora() {
        super("Aurora", "Maga", 28, 26, 999, 3, 1, 20);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "Aurora lê sinais nas nevascas — padrões que sussurram um nome proibido.",
                "A magia se agita; algo antigo desperta sob o gelo.",
                "Ela parte para selar o que nunca deveria ter sido lembrado."
        };
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha a habilidade de Aurora:");
        System.out.println("1 - Disparo Arcano (ataque mágico ~180% do ATK)");
        System.out.println("2 - Gelo Verdadeiro (alvo: -3 ATK e -2 DEF por 2 turnos)");
        System.out.println("3 - Revestimento Cristalino (+5 DEF por 3 turnos)");
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
            case 1 -> disparoArcano(alvo);
            case 2 -> geloVerdadeiro(alvo);
            case 3 -> revestimentoCristalino();
            default -> System.out.println("Habilidade inválida, Aurora perde o turno!");
        }

        System.out.println("PM restante: " + this.getPm() + "/" + this.getPmMax());
    }

    // ================== Habilidades ==================

    /** ~180% do ATK efetivo como dano BRUTO (DEF aplicada em receberDano). */
    private void disparoArcano(Personagem alvo) {
        int danoBruto = (int) Math.round(this.getAtkEfetivo() * 1.80);
        // pequena variação opcional
        danoBruto += random.nextInt(3) - 1;
        if (danoBruto < 1) danoBruto = 1;

        System.out.println(this.getNome() + " conjura um DISPARO ARCANO!");
        alvo.receberDano(danoBruto);
        System.out.println(alvo.getNome() + " sofre " + danoBruto + " de dano bruto!");

        aplicarPassiva();
    }

    /**
     * Debuff por 2 turnos no alvo:
     * -3 ATK (aplicado como modificador temporário a cada início de turno do alvo)
     * -2 DEF com restauração automática quando o efeito expira
     */
    private void geloVerdadeiro(Personagem alvo) {
        System.out.println(this.getNome() + " invoca gelos primordiais — GELO VERDADEIRO!");
        System.out.println(alvo.getNome() + " tem seu ataque e defesa reduzidos por 2 turnos!");

        alvo.adicionarEfeito(new EfeitoPorTurno() {
            private boolean defReduzidaAplicada = false;
            private int restantes = 2;

            @Override
            public void aoInicioDoTurno(Personagem self, Personagem adversario) {
                // -3 ATK temporário nesse turno
                self.modificarAtaqueTemporario(-3);

                // Reduz DEF uma única vez no início do efeito
                if (!defReduzidaAplicada) {
                    self.addDef(-2);
                    defReduzidaAplicada = true;
                }

                // Controla restauração quando o efeito expirar
                restantes--;
                if (restantes == 0) {
                    // restaura a DEF perdida
                    self.addDef(+2);
                }
            }

            @Override
            public String toString() {
                return "Debuff: Gelo Verdadeiro (-3 ATK, -2 DEF)";
            }
        }, 2);
    }

    /**
     * +5 DEF por 3 turnos com restauração automática.
     * Implementado via efeito temporário que devolve a DEF no último tick.
     */
    private void revestimentoCristalino() {
        System.out.println(this.getNome() + " se reveste em CRISTAL reluzente! (+5 DEF por 3 turnos)");

        // aplica imediatamente
        this.addDef(+5);

        this.adicionarEfeito(new EfeitoPorTurno() {
            private int restantes = 3;

            @Override
            public void aoInicioDoTurno(Personagem self, Personagem adversario) {
                // apenas controla a devolução no fim
                restantes--;
                if (restantes == 0) {
                    self.addDef(-5); // restaura
                }
            }

            @Override
            public String toString() {
                return "Buff: Revestimento Cristalino (+5 DEF)";
            }
        }, 3);
    }

    // ================== Passiva ==================

    private int ataquesConsecutivos = 0;

    private void aplicarPassiva() {
        ataquesConsecutivos++;
        if (ataquesConsecutivos >= 3) {
            ataquesConsecutivos = 0;
            int cura = Math.max(1, (int) Math.round(this.getPvMax() * 0.15));
            this.setPv(this.getPv() + cura);
            System.out.println("Coração de Gelo: Aurora canaliza o frio e recupera " + cura + " PV!");
        }
    }
}

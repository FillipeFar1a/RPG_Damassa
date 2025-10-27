package personagens.herois;

import personagens.Personagem;
import util.Combate;

import java.util.Random;
import java.util.Scanner;

// INIMIGOS invocados pelo TP
import personagens.inimigos.Garen;
import personagens.inimigos.Kindred;
import personagens.inimigos.Volibear;

public class Ryze extends Personagem {

    private boolean fluxoAtivo = false;  // marca se o alvo foi afetado por Fluxo Rúnico
    private boolean buffAtivo = false;   // controla se o buff de ataque está ativo
    private int turnosBuff = 0;
    private Random random = new Random();

    public Ryze() {
        super("Ryze", "Mago", 29, 28, 8, 4, 1, 20);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "Runas antigas sussurram um presságio: o nome do urso esquecido.",
                "Ryze conhece o preço do poder desmedido.",
                "Vai recolher os fragmentos antes que destruam o mundo."
        };
    }

    public void atualizarEfeitos() {
        if (fluxoAtivo) {
            fluxoAtivo = false; // o efeito dura apenas 1 turno
            System.out.println("A energia do Fluxo Rúnico se dissipa.");
        }
        if (buffAtivo) {
            turnosBuff--;
            if (turnosBuff <= 0) {
                buffAtivo = false;
                this.setAtk(this.getAtk() - 4);
                System.out.println("O poder rúnico de Ryze enfraquece.");
            }
        }
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha a habilidade de Ryze:");
        System.out.println("1 - Descarga Rúnica (120% dano; reage com Fluxo Rúnico) - 6 PM");
        System.out.println("2 - Fluxo Rúnico (marca o inimigo para combos) - 5 PM");
        System.out.println("3 - Poder Infinito (buff de ataque por 2 turnos) - 7 PM");
        System.out.println("4 - Portais do Mundo (teleporte instável) - 15 PM");
        System.out.print("Digite o número da habilidade: ");
        int escolha = scanner.nextInt();

        int custo = switch (escolha) {
            case 1 -> 6;
            case 2 -> 5;
            case 3 -> 7;
            case 4 -> 15;
            default -> 0;
        };

        if (this.getPm() < custo) {
            System.out.println("Mana insuficiente! (" + this.getPm() + "/" + this.getPmMax() + ")");
            return;
        }

        this.gastarMana(custo);

        switch (escolha) {
            case 1 -> { // Descarga Rúnica
                int danoBruto = (int)Math.round(this.getAtk() * 1.2);
                if (fluxoAtivo) {
                    danoBruto *= 2;
                    System.out.println("⚡ As runas reagem! Descarga Rúnica amplificada!");
                    fluxoAtivo = false;
                }
                alvo.receberDano(danoBruto); // DEF será aplicada dentro de receberDano
                System.out.println(this.getNome() + " lança uma DESCARGA RÚNICA!");
            }


            case 2 -> { // Fluxo Rúnico
                if (fluxoAtivo) {
                    System.out.println(this.getNome() + " consome a energia residual do Fluxo Rúnico!");
                    int recuperar = 6;
                    this.setPm(this.getPm() + recuperar);
                    System.out.println("Ryze recupera " + recuperar + " PM!");
                    fluxoAtivo = false;
                } else {
                    fluxoAtivo = true;
                    System.out.println(this.getNome() + " conjura FLUXO RÚNICO! O inimigo é marcado por energia arcana.");
                }
            }

            case 3 -> { // Poder Infinito (buff)
                if (!buffAtivo) {
                    buffAtivo = true;
                    turnosBuff = 2;
                    this.setAtk(this.getAtk() + 6);
                    System.out.println(this.getNome() + " ativa os Cristais Arcanos! Seu ataque aumenta temporariamente!");
                } else {
                    System.out.println("Ryze não consegue suportar mais do poder do arcano!");
                }
            }

            case 4 -> { // Portais do Mundo
                System.out.println(this.getNome() + " abre um PORTAL RÚNICO instável...");
                // Consome mana lá em cima; aqui apenas decide o efeito do portal.
                // Regra nova: SEMPRE foge do combate atual (sem XP, sem 2ª fase).
                // (Se quiser voltar a ter encontros especiais, a gente agenda fora do combate.)
                this.sinalizarPortalFuga();
                System.out.println("O espaço dobra, e você some do alcance do inimigo!");
            }
        }
    }

    /** Dispara um combate “inline” usando a engine atual. */
    private void iniciarBatalha(Personagem inimigo) {
        System.out.println("\n=== ❖ PORTAL ABERTO: NOVO ENCONTRO ❖ ===");
        Combate combate = new Combate(this, inimigo);
        combate.iniciar(); // bloqueia aqui até terminar
        System.out.println("=== ❖ PORTAL ENCERRADO ❖\n");
    }

    // ===== Flag interna para o Combate detectar fuga por portal =====
    private boolean portalFuga = false;

    public void sinalizarPortalFuga() {
        this.portalFuga = true;
    }

    public boolean consumirFlagPortalFuga() {
        boolean v = this.portalFuga;
        this.portalFuga = false;
        return v;
    }

}

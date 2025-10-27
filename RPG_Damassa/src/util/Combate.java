package util;

import personagens.Personagem;
import personagens.inimigos.Garen;
import personagens.inimigos.Kindred;
import personagens.inimigos.UrsoDeMilFlagelos;
import personagens.inimigos.Volibear;
import personagens.herois.Ryze;

import java.util.Random;
import java.util.Scanner;

public class Combate {

    /** Gancho para integrar inventário real (retorna true se usou item) */
    public interface ItemUser {
        boolean usarItem(Personagem usuario, Personagem alvo, Scanner sc);
    }

    private Personagem jogador;
    private Personagem inimigo;
    private final Random random = new Random();
    private final Scanner scanner = new Scanner(System.in);
    private final ItemUser itemUser; // pode ser null

    // === NOVO: flag de fuga (para não contar como vitória nem disparar 2ª fase) ===
    private boolean fugaPorPortalOuFugaNormal = false;

    public Combate(Personagem jogador, Personagem inimigo) {
        this(jogador, inimigo, null);
    }

    public Combate(Personagem jogador, Personagem inimigo, ItemUser itemUser) {
        this.jogador = jogador;
        this.inimigo = inimigo;
        this.itemUser = itemUser;
    }

    public void iniciar() {
        System.out.println("\n=== ⚔️ INÍCIO DA BATALHA ⚔️ ===");
        imprimirCabecalho();

        while (jogador.vivo()) {
            // ===== Turno do jogador =====
            turnoJogador();

            // Se pediu portal/fuga, encerra sem checar 2ª fase
            if (fugaPorPortalOuFugaNormal) break;

            if (!inimigo.vivo()) {
                // NÃO transformar em Urso se o término foi por fuga
                if (!fugaPorPortalOuFugaNormal && inimigo instanceof Volibear) {
                    System.out.println("\n⚡ O céu rasga em trovões! Volibear não cai — ele DESPERTA!");
                    inimigo = new UrsoDeMilFlagelos();
                    imprimirCabecalho();
                    continue; // segue para a segunda fase
                }
                break; // vitória final
            }

            // ===== Turno do inimigo =====
            turnoInimigo();
            if (!jogador.vivo()) break;
        }

        // Resultado final
        if (fugaPorPortalOuFugaNormal) {
            System.out.println("\n🏃 Você escapou do combate.");
        } else if (jogador.vivo()) {
            System.out.println("\n🏆 " + jogador.getNome() + " venceu a batalha!");
            jogador.ganharXp(10);
        } else {
            System.out.println("\n💀 " + jogador.getNome() + " foi derrotado...");
        }

        System.out.println("===============================");
    }

    private void imprimirCabecalho() {
        System.out.println(jogador.getNome() + " (" + jogador.getClasse() + ") VS " +
                inimigo.getNome() + " (" + inimigo.getClasse() + ")");
        System.out.println("-------------------------------");
    }

    private void turnoJogador() {
        // efeitos começo do turno + reset de modificadores
        jogador.processarEfeitosInicioDoTurno(inimigo);

        System.out.println("\n--- Seu turno ---");
        System.out.println(jogador);
        System.out.println("1 - Ataque básico");
        System.out.println("2 - Usar habilidade");
        System.out.println("3 - Usar item");
        System.out.println("4 - Fugir");
        System.out.print("Escolha: ");

        int escolha = lerOpcao(1, 4);

        if (jogador.isCongelado()) {
            System.out.println(jogador.getNome() + " está incapacitado e perde o turno!");
            jogador.setCongelado(false);
            return;
        }

        switch (escolha) {
            case 1 -> ataqueComDado(jogador, inimigo);
            case 2 -> {
                jogador.usarHabilidade(inimigo);
                // === NOVO: se for Ryze e ele acionou portal, tratamos como fuga bem-sucedida ===
                if (jogador instanceof Ryze ryze && ryze.consumirFlagPortalFuga()) {
                    fugaPorPortalOuFugaNormal = true;
                    System.out.println("O portal dobra o espaço — você some do confronto!");
                    return;
                }
            }
            case 3 -> {
                if (itemUser != null) {
                    boolean usou = itemUser.usarItem(jogador, inimigo, scanner);
                    if (!usou) {
                        System.out.println("Nenhum item usado. Você hesita e perde o turno...");
                    }
                } else {
                    System.out.println("Inventário não integrado ainda. (Dica: injete um ItemUser no construtor)");
                    System.out.println("Você hesita e perde o turno...");
                }
            }
            case 4 -> {
                if (tentarFugir()) {
                    System.out.println("Você conseguiu fugir!");
                    // encerra o combate imediatamente sem contar vitória
                    fugaPorPortalOuFugaNormal = true;
                    return;
                } else {
                    System.out.println("Você falhou em fugir! O inimigo ataca de oportunidade!");
                    ataqueComDado(inimigo, jogador); // ataque imediato do inimigo
                }
            }
            default -> System.out.println("Você hesitou e perdeu o turno...");
        }

        if (!fugaPorPortalOuFugaNormal && inimigo.vivo()) {
            inimigoStatus();
        }
    }

    private void turnoInimigo() {
        // efeitos começo do turno + reset de modificadores
        inimigo.processarEfeitosInicioDoTurno(jogador);

        System.out.println("\n--- Turno do inimigo ---");

        // ===== Regras especiais: KINDRED =====
        if (inimigo instanceof Kindred) {
            if (inimigo.isCongelado()) {
                inimigo.setCongelado(false);
            }
            inimigo.usarHabilidade(jogador); // IK
            if (jogador.vivo()) {
                jogadorStatus();
            }
            return;
        }

        // ===== IA específica do GAREN: se puder executar, usa ult =====
        if (inimigo instanceof Garen) {
            int limiteExec = Math.max(1, (int) Math.floor(jogador.getPvMax() * 0.10));
            if (jogador.getPv() <= limiteExec) {
                inimigo.usarHabilidade(jogador); // Justiça Demaciana (IK)
                if (jogador.vivo()) jogadorStatus();
                return;
            }
        }

        // ===== Fluxo normal para os demais inimigos =====
        if (inimigo.isCongelado()) {
            System.out.println(inimigo.getNome() + " está incapacitado e perde o turno!");
            inimigo.setCongelado(false);
            return;
        }

        // 50% habilidade | 50% ataque básico (pode ajustar)
        if (random.nextBoolean()) {
            inimigo.usarHabilidade(jogador);
        } else {
            ataqueComDado(inimigo, jogador);
        }

        if (jogador.vivo()) {
            jogadorStatus();
        }
    }

    private int lerOpcao(int min, int max) {
        int op;
        while (true) {
            while (!scanner.hasNextInt()) {
                scanner.next(); // descarta lixo
                System.out.print("Digite um número: ");
            }
            op = scanner.nextInt();
            scanner.nextLine(); // consome \n
            if (op >= min && op <= max) break;
            System.out.print("Opção inválida. Escolha entre " + min + " e " + max + ": ");
        }
        return op;
    }

    private void ataqueComDado(Personagem atacante, Personagem defensor) {
        int rolagem = random.nextInt(6) + 1; // d6
        int danoBruto = atacante.getAtkEfetivo() + rolagem; // SEM subtrair DEF aqui
        defensor.receberDano(danoBruto); // a DEF é aplicada dentro de Personagem.receberDano

        System.out.println(atacante.getNome() + " rola um d6 e tira " + rolagem + "!");
        System.out.println(atacante.getNome() + " ataca causando dano bruto " + danoBruto + " (a DEF reduz o dano).");
    }


    private boolean tentarFugir() {
        int r = random.nextInt(6) + 1; // d6
        System.out.println("Você rola um d6 para fugir... (" + r + ")");
        return r >= 4; // 4,5,6 foge
    }

    private void jogadorStatus() {
        System.out.println("PV do jogador: " + jogador.getPv() + "/" + jogador.getPvMax());
    }

    private void inimigoStatus() {
        System.out.println("PV do inimigo: " + inimigo.getPv() + "/" + inimigo.getPvMax());
    }
}

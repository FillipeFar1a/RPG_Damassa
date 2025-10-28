package util;

import personagens.Personagem;
import itens.Inventario;
import itens.Item;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Loop de combate com HUD: mostra PV/PM de jogador e inimigo o tempo todo
 * e limpa a tela após cada ação.
 */
public class Combate {
    private final Personagem jogador;
    private final Personagem inimigo;
    private final Scanner sc = new Scanner(System.in);
    private final Random rng = new Random();

    public Combate(Personagem jogador, Personagem inimigo) {
        this.jogador = jogador;
        this.inimigo = inimigo;
    }

    public void iniciar() {
        // Começo do combate
        aguardinha("Um combate começou!");

        while (jogador.vivo() && inimigo.vivo()) {
            // ===== TURNO DO JOGADOR =====
            Efeitos.limparTela();
            // efeitos de início do turno do jogador
            jogador.processarEfeitosInicioDoTurno(inimigo);
            renderHUD();

            boolean gastouTurno = turnoDoJogador();
            if (gastouTurno) {
                esperarEnterELimpar();
            }
            if (!inimigo.vivo() || !jogador.vivo()) break;

            // ===== TURNO DO INIMIGO =====
            Efeitos.limparTela();
            // efeitos de início do turno do inimigo
            inimigo.processarEfeitosInicioDoTurno(jogador);
            renderHUD();

            turnoDoInimigo();
            esperarEnterELimpar();
        }

        // Fim do combate
        if (jogador.vivo() && !inimigo.vivo()) {
            aguardinha("Você venceu!");
        } else if (!jogador.vivo() && inimigo.vivo()) {
            aguardinha("Você foi derrotado...");
        } else {
            aguardinha("O combate terminou.");
        }
    }

    // =================== LOOPS DE TURNO ===================

    /** Retorna true se o jogador consumiu o turno (ex.: atacou, usou item etc.). */
    private boolean turnoDoJogador() {
        while (true) {
            System.out.println("\nSua vez, escolha uma ação:");
            System.out.println("[1] Atacar");
            System.out.println("[2] Habilidade");
            System.out.println("[3] Usar Item");
            System.out.println("[4] Passar Turno");
            System.out.print("> ");
            String op = sc.nextLine().trim();

            switch (op) {
                case "1" -> {
                    int dano = jogador.getAtkEfetivo();
                    inimigo.receberDano(dano);
                    System.out.printf("%s ataca e causa %d de dano!\n", jogador.getNome(), Math.max(1, dano - inimigo.getDef()));
                    return true;
                }
                case "2" -> {
                    jogador.usarHabilidade(inimigo);
                    // Supõe que a habilidade consome o turno
                    return true;
                }
                case "3" -> {
                    if (menuUsarItem(jogador)) {
                        return true; // usar item consome turno
                    } else {
                        // volta ao menu sem consumir turno
                        renderHUD();
                    }
                }
                case "4" -> {
                    System.out.println("Você observa o inimigo e prepara sua guarda...");
                    return true;
                }
                default -> {
                    System.out.println("Opção inválida.");
                }
            }
        }
    }

    private void turnoDoInimigo() {
        // IA simples: 35% tentar habilidade, senão ataque básico
        if (rng.nextDouble() < 0.35) {
            System.out.printf("%s prepara uma técnica!\n", inimigo.getNome());
            inimigo.usarHabilidade(jogador);
        } else {
            int dano = inimigo.getAtkEfetivo();
            jogador.receberDano(dano);
            System.out.printf("%s ataca e causa %d de dano!\n", inimigo.getNome(), Math.max(1, dano - jogador.getDef()));
        }
    }

    // =================== UI / HUD / ITENS ===================

    private void renderHUD() {
        String barraJog = barra(jogador.getPv(), jogador.getPvMax(), 20);
        String barraIni = barra(inimigo.getPv(), inimigo.getPvMax(), 20);

        System.out.println("==================================================");
        System.out.printf(" Herói: %-15s  PV %3d/%-3d %s  PM %3d/%-3d\n",
                jogador.getNome(), jogador.getPv(), jogador.getPvMax(), barraJog, jogador.getPm(), jogador.getPmMax());
        System.out.printf(" Inim.: %-15s  PV %3d/%-3d %s  PM %3d/%-3d\n",
                inimigo.getNome(), inimigo.getPv(), inimigo.getPvMax(), barraIni, inimigo.getPm(), inimigo.getPmMax());
        System.out.println("==================================================");
    }

    private String barra(int atual, int max, int largura) {
        if (max <= 0) max = 1;
        double pct = Math.max(0, Math.min(1.0, (double) atual / max));
        int cheios = (int) Math.round(pct * largura);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < largura; i++) {
            sb.append(i < cheios ? "#" : "-");
        }
        sb.append("]");
        return sb.toString();
    }

    /** Menu simples de uso de item (inventário do jogador). Retorna true se usou algo. */
    private boolean menuUsarItem(Personagem p) {
        Inventario inv = p.getInventario();
        List<Item> lista = inv.listarOrdenado();
        if (lista.isEmpty()) {
            System.out.println("\nInventário vazio!");
            return false;
        }

        System.out.println("\n=== INVENTÁRIO ===");
        for (int i = 0; i < lista.size(); i++) {
            Item it = lista.get(i);
            System.out.printf("[%d] %s x%d — %s (%s)\n", i + 1, it.getNome(), it.getQuantidade(), it.getDescricao(), it.getEfeito());
        }
        System.out.println("[0] Voltar");
        System.out.print("> ");
        String s = sc.nextLine().trim();

        int escolha;
        try { escolha = Integer.parseInt(s); }
        catch (NumberFormatException e) { System.out.println("Entrada inválida."); return false; }

        if (escolha == 0) return false;
        int idx = escolha - 1;
        if (idx < 0 || idx >= lista.size()) { System.out.println("Opção inválida."); return false; }

        Item escolhido = lista.get(idx);
        System.out.printf("Usar '%s'? (s/N) ", escolhido.getNome());
        String conf = sc.nextLine().trim().toLowerCase();
        if (!"s".equals(conf)) return false;

        boolean ok = p.usarItem(escolhido.getNome(), escolhido.getEfeito());
        if (ok) {
            System.out.println("Item usado!");
            return true;
        } else {
            System.out.println("Não foi possível usar o item.");
            return false;
        }
    }

    private void aguardinha(String msg) {
        System.out.println(msg);
        Efeitos.esperar(600);
    }

    private void esperarEnterELimpar() {
        System.out.println("\n[Pressione Enter para continuar]");
        sc.nextLine();
        Efeitos.limparTela();
    }
}

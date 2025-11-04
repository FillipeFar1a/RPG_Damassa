package jogo;

import util.Efeitos;
import util.Combate;
import personagens.Personagem;
import personagens.herois.FabricaDePersonagens;

// INIMIGOS
import personagens.inimigos.Darius;
import personagens.inimigos.Trundle;
import personagens.inimigos.Sylas;
import personagens.inimigos.Lissandra;
import personagens.inimigos.Volibear;
import personagens.inimigos.FlageloGuerreiro;
import personagens.inimigos.FlageloArqueiro;
import personagens.inimigos.FlageloMago;
import personagens.inimigos.FlageloGigante;
import personagens.inimigos.FlageloSupremo;

import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

// MUNDO
import mundo.WorldProgress;

// INVENTÁRIO / ITENS
import itens.Item;
import itens.Inventario;

/**
 * - Áreas / Mundo (WorldProgress por nível, sem limite de exploração)
 * - Save/Load retrocompatível (jogador + mundo)
 * - Encontros aleatórios (comum / mini-boss) e Boss ao avançar
 * - Inventário com itens (cura/mana)
 * - Drop de poções pós-combate (aleatório)
 * - XP vem do inimigo derrotado (xpDrop)
 */
public class Jogo {

    // =================== PATHS ===================
    private static final String SAVE_PATH = "src/data/saves/save.dat";
    private static final String RANK_PATH = "src/data/rank/rank.txt";

    // =================== ITENS INICIAIS ===================
    private static final int START_QTD_POTION_CURA = 5;
    private static final int START_QTD_POTION_MANA = 3;

    // =================== ENCONTROS ===================
    private static final double ENCONTRO_CHANCE = 0.70; // 70% ter encontro
    private static final double MINI_BOSS_CHANCE_DENTRO_ENCONTRO = 0.10; // 10% do encontro virar mini-boss

    // =================== LOOT DE POÇÕES ===================
    private static final double POTION_DROP_CHANCE = 0.75; // 75% chance de dropar
    private static final int POTION_MIN = 1;
    private static final int POTION_MAX = 3;

    // =================== ESTADO ===================
    private static WorldProgress mundo;        // Mundo vai junto no save
    private static final Random RNG = new Random();

    // =================== SAVE PACKAGE ===================
    private static class SaveData implements Serializable {
        private final Personagem jogador;
        private final WorldProgress world;
        SaveData(Personagem jogador, WorldProgress world) {
            this.jogador = jogador; this.world = world;
        }
        Personagem getJogador() { return jogador; }
        WorldProgress getWorld() { return world; }
    }
    // =====================================================
    // ENTRADA
    // =====================================================
    public static void iniciar() {
        Scanner sc = new Scanner(System.in);
        boolean rodando = true;

        while (rodando) {
            Efeitos.limparTela();
            banner();

            System.out.print("\nEscolha uma opção: ");
            String opcao = sc.nextLine().trim();

            switch (opcao) {
                case "1" -> comecarJogo(sc);
                case "2" -> carregarJogo(sc);
                case "3" -> mostrarRank(sc);       // Rank no menu inicial
                case "4" -> configuracoes(sc);
                case "5" -> {
                    Efeitos.limparTela();
                    System.out.println("Saindo do jogo...");
                    Efeitos.esperar(800);
                    rodando = false;
                }
                default -> {
                    System.out.println("Opção inválida!");
                    Efeitos.esperar(900);
                }
            }
        }
        sc.close();
    }

    private static void banner() {
        System.out.println("======= RPG: MIL FLAGELOS =======");
        System.out.println("Uma história de League of Legends");
        System.out.println();
        System.out.println("        - Feito por Fillipe e Hugo");
        System.out.println("=================================");
        System.out.println();
        System.out.println("[1] Começar");
        System.out.println("[2] Carregar");
        System.out.println("[3] Rank");              // <- aqui é Rank
        System.out.println("[4] Configurações");
        System.out.println("[5] Sair");
    }

    // =====================================================
    // NOVO JOGO
    // =====================================================
    private static void comecarJogo(Scanner sc) {
        Personagem jogador = selecionarHeroi(sc);
        darItensIniciais(jogador);

        // Intro do herói
        Efeitos.limparTela();
        System.out.println("Você escolheu: " + jogador.getNome() + " — " + jogador.getClasse() + "\n");

        Efeitos.limparTela();
        String[] falaIntro = jogador.intro();       // cada herói retorna seu texto de intro
        Efeitos.textoDigitando(falaIntro, 30, 600); // (velocidade, pausaEntreLinhas)
        System.out.println("\n[Pressione Enter para continuar]");
        sc.nextLine();

        Efeitos.limparTela();
        String[] cena2 = {
                "Volibear está desperto. E sua ira ameaça engolir o mundo",
                "E tudo que habita nele",
                "..."
        };
        Efeitos.textoDigitando(cena2, 35, 800);
        pausar(sc);

        Efeitos.limparTela();
        String[] cena3 = {
                "Agora não tem mais volta...",
                "A sua jornada para deter o Urso de Mil Flagelos...",
                "Começa agora!"
        };
        Efeitos.textoDigitando(cena3, 25, 650);
        pausar(sc);

        mundo = new WorldProgress();
        loopPrincipal(sc, jogador);
    }

    private static Personagem selecionarHeroi(Scanner sc) {
        Efeitos.limparTela();
        System.out.println("=== ESCOLHA SEU PERSONAGEM ===\n");

        int total = FabricaDePersonagens.total();
        for (int i = 1; i <= total; i++) {
            System.out.printf("[%d] %s%n", i, FabricaDePersonagens.rotuloPorIndice(i));
        }
        System.out.print("\nDigite o número: ");
        int escolha = lerOpcao(sc, 1, total);

        Personagem jogador = FabricaDePersonagens.criarPorIndice(escolha);
        if (jogador == null) {
            System.out.println("Falha ao criar o personagem. Tente novamente.");
            Efeitos.esperar(1000);
            return selecionarHeroi(sc);
        }
        return jogador;
    }

    // =====================================================
    // LOOP PRINCIPAL
    // =====================================================
    private static void loopPrincipal(Scanner sc, Personagem jogador) {
        boolean jogando = true;

        while (jogando && jogador.vivo()) {
            Efeitos.limparTela();
            exibirStatusEInventario(jogador);

            System.out.println("\nAções:");
            System.out.println("[1] Explorar (área atual)");
            System.out.println("[2] Explorar em outra área");
            System.out.println("[3] Ver Mapa/Áreas");
            System.out.println("[4] Avançar para próxima área");
            System.out.println("[5] Usar Item");
            System.out.println("[6] Salvar Jogo");
            System.out.println("[7] Pausar");
            System.out.println("[8] Voltar ao Menu");
            System.out.print("> ");

            String op = sc.nextLine().trim();
            switch (op) {
                case "1" -> {
                    int idx = mundo.getAreaAtualIndex();
                    if (mundo.explorarNaArea(idx)) {
                        System.out.println("Você explora a área: " + mundo.getAreaAtualNome());
                        tentarEncontro(sc, jogador, idx);
                    } else {
                        System.out.println("Não foi possível explorar aqui.");
                    }
                    pausar(sc);
                }
                case "2" -> {
                    // Mostra mapa ASCII antes de escolher
                    Efeitos.limparTela();
                    System.out.println(renderMapaAscii(jogador));
                    System.out.println("\nDigite o número da área para explorar:");
                    try {
                        int num = Integer.parseInt(sc.nextLine());
                        int idx = num - 1;

                        if (idx < 0 || idx >= mundo.getUnlockedCount(jogador)) {
                            System.out.println("Área inválida. Selecione uma das áreas liberadas exibidas no mapa.");
                        } else {
                            // troca o foco da área atual
                            mundo.setAreaAtual(idx);

                            Efeitos.limparTela();
                            System.out.println(renderMiniMapaLinha(jogador)); // mini header bonitinho
                            if (mundo.explorarNaArea(idx)) {
                                System.out.println("\nVocê explora: " + mundo.getAreaNome(idx));
                                tentarEncontro(sc, jogador, idx);
                            } else {
                                System.out.println("Não foi possível explorar aqui.");
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Entrada inválida.");
                    }
                    pausar(sc);
                }
                case "3" -> {
                    // Ver mapa dentro do jogo
                    Efeitos.limparTela();
                    System.out.println(renderMapaAscii(jogador));
                    pausar(sc);
                }
                case "4" -> {
                    if (mundo.podeAvancar(jogador)) {
                        int idxAtual = mundo.getAreaAtualIndex();
                        Personagem boss = bossDaArea(idxAtual, mundo.getAreasCount());
                        System.out.println("\n⚠ Você sente uma presença poderosa bloqueando seu caminho...");
                        iniciarCombate(sc, jogador, boss);
                        if (!jogador.vivo()) break;

                        mundo.avancarArea(jogador);
                        System.out.println("Você avançou para: " + mundo.getAreaAtualNome());
                    } else {
                        System.out.println("Ainda não liberou o avanço. Suba de nível ou libere mais áreas.");
                    }
                    pausar(sc);
                }
                case "5" -> menuUsarItem(sc, jogador);
                case "6" -> {
                    salvarJogoPacote(jogador, mundo);
                    pausar(sc);
                }
                case "7" -> {
                    System.out.println("\n[PAUSE] Pressione Enter para continuar...");
                    sc.nextLine();
                }
                case "8" -> jogando = false;
                default -> {
                    System.out.println("Opção inválida!");
                    Efeitos.esperar(800);
                }
            }
        }

        if (!jogador.vivo()) {
            registrarPontuacao(jogador.getNome(), jogador.getNivel());
            System.out.println("\nGame Over. Nível alcançado: " + jogador.getNivel());
            pausar(sc);
        }
    }

    private static void exibirStatusEInventario(Personagem jogador) {
        System.out.println("=== STATUS ===");
        System.out.println(jogador);
        System.out.println("\nInventário:");
        System.out.println(jogador.getInventario());
    }

    // =====================================================
    // MAPA ASCII — estilo candy box (somente in-game)
    // =====================================================
    private static String renderMapaAscii(Personagem jogador) {
        // Arte base da montanha
        String[] montanha = new String[]{
                "                        ,sdPBbs.",
                "                      ,d$$$$$$$$b.",
                "                     d$P'`Y'`Y'`?$b",
                "                    d'    `  '  \\ `b",
                "                   /    |        \\  \\",
                "                  /    / \\\\       |   \\",
                "             _,--'        |      \\    |",
                "           /' _/          \\   |        \\",
                "        _/' /'             |   \\        `-.__",
                "    __/'       ,-'    /    |    |     \\      `--...__",
                "  /'          /      |    / \\     \\     `-.           `\\",
                " /    /;;,,__-'      /   /    \\            \\            `-.",
                "/    |;;;;;;;\\                                             \\",
                "------------------------------------------------------------"
        };

        int n = mundo.getAreasCount();
        int atual = mundo.getAreaAtualIndex();
        int unlocked = mundo.getUnlockedCount(jogador);

        // Nomes das áreas
        String[] nomes = new String[n];
        for (int i = 0; i < n; i++) nomes[i] = mundo.getAreaNome(i);

        // Distribui linhas para rótulos das áreas
        int[] linhasArea;
        if (n == 5) {
            linhasArea = new int[]{12, 10, 8, 6, 4};
        } else {
            linhasArea = new int[n];
            int start = 3;
            int end = montanha.length - 2;
            double step = (end - start) / Math.max(1.0, (n - 1));
            for (int i = 0; i < n; i++) linhasArea[i] = (int) Math.round(start + i * step);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== MAPA DE FRELJORD ===\n\n");
        for (int i = 0; i < montanha.length; i++) {
            int idxArea = -1;
            for (int k = 0; k < n; k++) {
                if (linhasArea[k] == i) { idxArea = k; break; }
            }

            String linha = montanha[i];
            if (idxArea >= 0) {
                boolean desbloqueada = idxArea < unlocked;
                boolean ehAtual = idxArea == atual;

                String nome = nomes[idxArea];
                String tag = (desbloqueada ? (ehAtual ? ">> " + nome + " <<" : nome) : "[BLOQUEADA] " + nome);

                String padded = String.format("%-60s  %d) %s", linha, (idxArea + 1), tag);
                sb.append(padded);
            } else {
                sb.append(linha);
            }
            sb.append('\n');
        }

        sb.append("\nLegenda: ");
        sb.append(">> ÁREA ATUAL <<   |   [BLOQUEADA] = ainda indisponível\n");
        sb.append(renderMiniMapaLinha(jogador));
        return sb.toString();
    }

    private static String renderMiniMapaLinha(Personagem jogador) {
        String nome = mundo.getAreaAtualNome();
        String who = (jogador == null) ? "" :
                String.format(" | Herói: %s (Nv %d)", jogador.getNome(), jogador.getNivel());
        return String.format("\n[Área atual: %s]%s\n", nome, who);
    }

    // =====================================================
    // ENCONTROS / COMBATE / LOOT / XP
    // =====================================================
    private static void tentarEncontro(Scanner sc, Personagem jogador, int idxArea) {
        double roll = RNG.nextDouble();
        if (roll <= ENCONTRO_CHANCE) {
            Personagem inimigo;
            if (roll <= ENCONTRO_CHANCE * MINI_BOSS_CHANCE_DENTRO_ENCONTRO) {
                inimigo = new FlageloSupremo(idxArea + 1);
                System.out.println("\n⚠ Você encontrou um Mini-Boss: " + inimigo.getNome() + "!");
            } else {
                inimigo = inimigoAleatorio(idxArea);
                System.out.println("\nUm inimigo aparece: " + inimigo.getNome() + "!");
            }
            iniciarCombate(sc, jogador, inimigo);
        } else {
            System.out.println("Você não encontrou resistência... por enquanto.");
        }
    }

    private static void iniciarCombate(Scanner sc, Personagem jogador, Personagem inimigo) {
        Combate combate = new Combate(jogador, inimigo);
        combate.iniciar();

        // XP & Loot se o inimigo morreu e o jogador sobreviveu
        if (jogador.vivo() && inimigo != null && !inimigo.vivo()) {
            int xp = xpDrop(inimigo);
            if (xp > 0) {
                jogador.ganharXp(xp);
                System.out.println("💥 Vitória! Você ganhou " + xp + " XP.");
            }
            tentarDropPotions(jogador, inimigo);
        }
    }

    /** Regra de XP por tipo + nível (ajuste à vontade): */
    private static int xpDrop(Personagem inimigo) {
        int nv = Math.max(1, inimigo.getNivel());

        // Bosses principais
        if (inimigo instanceof Volibear) return 100 + nv * 15;
        if (inimigo instanceof Darius || inimigo instanceof Trundle ||
                inimigo instanceof Sylas  || inimigo instanceof Lissandra) {
            return 40 + nv * 10;
        }

        // Mini-boss
        if (inimigo instanceof FlageloSupremo) return 25 + nv * 8;

        // Mobs comuns (escalonados)
        if (inimigo instanceof FlageloGigante)  return 18 + nv * 6;
        if (inimigo instanceof FlageloMago)     return 14 + nv * 5;
        if (inimigo instanceof FlageloArqueiro) return 12 + nv * 4;
        if (inimigo instanceof FlageloGuerreiro)return 12 + nv * 4;

        // Fallback genérico
        return 10 + nv * 3;
    }

    /** Sorteia 1..3 poções com tipo aleatório (CURA/MANA) por unidade. */
    private static void tentarDropPotions(Personagem jogador, Personagem inimigo) {
        if (RNG.nextDouble() > POTION_DROP_CHANCE) return;

        int qtd = POTION_MIN + RNG.nextInt(POTION_MAX - POTION_MIN + 1); // 1..3
        int cura = 0, mana = 0;
        for (int i = 0; i < qtd; i++) {
            if (RNG.nextBoolean()) cura++; else mana++;
        }

        if (cura > 0) {
            jogador.adicionarItem("Poção de Cura", "Recupera uma quantidade de PV.", Item.Efeito.CURA, cura);
        }
        if (mana > 0) {
            jogador.adicionarItem("Poção de Mana", "Recupera uma quantidade de PM.", Item.Efeito.MANA, mana);
        }

        System.out.print("\n📦 Saque: ");
        if (cura > 0) System.out.print(cura + "x Poção de Cura ");
        if (mana > 0) System.out.print((cura > 0 ? "+ " : "") + mana + "x Poção de Mana");
        System.out.println();
    }

    /** Inimigo comum randômico, escalado pela área. */
    private static Personagem inimigoAleatorio(int idxArea) {
        int nivel = Math.max(1, idxArea + 1);
        int pick = RNG.nextInt(4); // 0..3
        return switch (pick) {
            case 0 -> new FlageloGuerreiro(nivel);
            case 1 -> new FlageloArqueiro(nivel);
            case 2 -> new FlageloMago(nivel);
            default -> new FlageloGigante(nivel);
        };
    }

    /**
     * Boss por área (índice):
     * 0: Darius | 1: Trundle | 2: Sylas | 3: Lissandra | último índice (size-1): Volibear
     */
    private static Personagem bossDaArea(int idxArea, int totalAreas) {
        int ultimoIndice = Math.max(0, totalAreas - 1);
        if (idxArea >= ultimoIndice) return new Volibear();
        return switch (idxArea) {
            case 0 -> new Darius();
            case 1 -> new Trundle();
            case 2 -> new Sylas();
            case 3 -> new Lissandra();
            default -> new Volibear();
        };
    }

    // =====================================================
    // INVENTÁRIO
    // =====================================================
    private static void darItensIniciais(Personagem jogador) {
        jogador.adicionarItem("Poção de Cura", "Recupera uma quantidade de PV.", Item.Efeito.CURA, START_QTD_POTION_CURA);
        jogador.adicionarItem("Poção de Mana", "Recupera uma quantidade de PM.", Item.Efeito.MANA, START_QTD_POTION_MANA);

        // Hooks prontos p/ futuro:
        // jogador.adicionarItem("Tônico de Fúria", "Aumenta ATK neste turno.", Item.Efeito.ATAQUE, 1);
        // jogador.adicionarItem("Tônico de Aço", "Aumenta DEF neste turno.", Item.Efeito.DEFESA, 1);
        // jogador.adicionarItem("Elixir do Herói", "Cura e concede pequenos bônus temporários.", Item.Efeito.BUFF_GERAL, 1);
    }

    private static void menuUsarItem(Scanner sc, Personagem jogador) {
        Inventario inv = jogador.getInventario();
        List<Item> lista = inv.listarOrdenado();

        if (lista.isEmpty()) {
            System.out.println("\nInventário vazio, gordão!");
            pausar(sc);
            return;
        }

        System.out.println("\n=== USAR ITEM ===");
        for (int i = 0; i < lista.size(); i++) {
            Item it = lista.get(i);
            System.out.printf("[%d] %s x%d — %s (%s)%n",
                    i + 1, it.getNome(), it.getQuantidade(), it.getDescricao(), it.getEfeito());
        }
        System.out.println("[0] Voltar");
        System.out.print("> ");

        int escolha;
        try {
            escolha = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            pausar(sc);
            return;
        }
        if (escolha == 0) return;

        int idx = escolha - 1;
        if (idx < 0 || idx >= lista.size()) {
            System.out.println("Opção inválida.");
            pausar(sc);
            return;
        }

        Item escolhido = lista.get(idx);
        System.out.printf("Usar '%s'? (s/N) ", escolhido.getNome());
        String conf = sc.nextLine().trim().toLowerCase();
        if (!conf.equals("s")) return;

        boolean ok = jogador.usarItem(escolhido.getNome(), escolhido.getEfeito());
        System.out.println(ok ? "Item usado!" : "Não foi possível usar o item.");
        pausar(sc);
    }

    // =====================================================
    // SAVE / LOAD / RANK / CONFIG
    // =====================================================
    private static void carregarJogo(Scanner sc) {
        Efeitos.limparTela();
        Carregado c = lerSaveCompat();
        if (c == null || c.jogador == null) {
            System.out.println("Nenhum save válido encontrado.");
            pausar(sc);
            return;
        }
        mundo = (c.mundo != null) ? c.mundo : new WorldProgress();

        System.out.println("Save carregado: " + c.jogador.getNome() + " — Nível " + c.jogador.getNivel());
        pausar(sc);

        loopPrincipal(sc, c.jogador);

        if (!c.jogador.vivo()) {
            registrarPontuacao(c.jogador.getNome(), c.jogador.getNivel());
            System.out.println("\nGame Over. Nível alcançado: " + c.jogador.getNivel());
            pausar(sc);
        }
    }

    private static void mostrarRank(Scanner sc) {
        Efeitos.limparTela();
        System.out.println("=== RANK DOS HERÓIS ===\n");

        File f = new File(RANK_PATH);
        if (!f.exists()) {
            System.out.println("Sem registros ainda.");
            pausar(sc);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            List<String[]> registros = new java.util.ArrayList<>();
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] t = linha.split(";");
                if (t.length >= 2) registros.add(t);
            }

            // ordena do MAIOR para o MENOR nível
            registros.sort((a, b) -> {
                try {
                    int n1 = Integer.parseInt(a[0]);
                    int n2 = Integer.parseInt(b[0]);
                    return Integer.compare(n2, n1); // ordem decrescente
                } catch (NumberFormatException e) {
                    return 0;
                }
            });

            int pos = 1;
            for (String[] t : registros) {
                if (t.length >= 2) {
                    System.out.printf("%dº  Nível %-3s  %s%n", pos++, t[0], t[1]);
                }
                if (pos > 10) break; // mostra top 10
            }

        } catch (IOException e) {
            System.out.println("Erro ao ler rank: " + e.getMessage());
        }

        System.out.println("\n[Pressione Enter para voltar]");
        sc.nextLine();
    }


    private static void configuracoes(Scanner sc) {
        Efeitos.limparTela();
        System.out.println("=== CONFIGURAÇÕES ===");
        System.out.println("Som: ON");
        System.out.println("Dificuldade: Complicado");
        System.out.println("\n[Pressione Enter para voltar]");
        sc.nextLine();
    }

    // ---- salvar/carregar compatível
    private static void salvarJogoPacote(Personagem p, WorldProgress w) {
        try {
            File arquivo = new File(SAVE_PATH);
            arquivo.getParentFile().mkdirs();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(arquivo))) {
                out.writeObject(new SaveData(p, w));
            }
            System.out.println("Jogo salvo com sucesso.");
        } catch (Exception e) {
            System.out.println("Falha ao salvar: " + e.getMessage());
        }
    }

    private static class Carregado { Personagem jogador; WorldProgress mundo; }

    private static Carregado lerSaveCompat() {
        File arquivo = new File(SAVE_PATH);
        if (!arquivo.exists()) return null;

        // formato novo
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            Object o = in.readObject();
            if (o instanceof SaveData sd) {
                Carregado c = new Carregado();
                c.jogador = sd.getJogador();
                c.mundo = sd.getWorld();
                return c;
            }
        } catch (Exception ignored) {}

        // formato antigo (só Personagem)
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            Object o = in.readObject();
            if (o instanceof Personagem p) {
                Carregado c = new Carregado();
                c.jogador = p;
                c.mundo = new WorldProgress();
                return c;
            }
        } catch (Exception ignored) {}

        return null;
    }

    private static void registrarPontuacao(String nome, int nivel) {
        try {
            File arquivo = new File(RANK_PATH);
            arquivo.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(arquivo, true)) {
                fw.write(nivel + ";" + nome + ";" + System.currentTimeMillis() + "\n");
            }
        } catch (IOException ignored) {}
    }

    // =====================================================
    // UTIL
    // =====================================================
    private static int lerOpcao(Scanner sc, int min, int max) {
        while (true) {
            String s = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) {}
            System.out.print("Opção inválida. Digite um número entre " + min + " e " + max + ": ");
        }
    }

    private static void pausar(Scanner sc) {
        System.out.println("\n[Pressione Enter para continuar]");
        sc.nextLine();
    }
}

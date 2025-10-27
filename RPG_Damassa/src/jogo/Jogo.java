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

// IMPORTA O MUNDO
import mundo.WorldProgress;

// INVENTÁRIO / ITENS
import itens.Item;
import itens.Inventario;

/**
 * Jogo.java integrado com:
 * - Sistema de Áreas/Mundo (WorldProgress)
 * - Salvamento de pacote (Personagem + Mundo) com retrocompatibilidade
 * - Menu de exploração por área (mínimo p/ avançar + limite de salas)
 * - Encontros aleatórios (comum/mini-boss) e Boss ao concluir área
 * - Inventário com uso de itens (cura/mana e pronto para buffs de ATK/DEF)
 */
public class Jogo {

    private static final String SAVE_PATH = "src/data/saves/save.dat";
    private static final String RANK_PATH = "src/data/rank/rank.txt";

    // ===== Config de itens iniciais =====
    private static final int START_QTD_POTION_CURA = 5;
    private static final int START_QTD_POTION_MANA = 3;

    // Estado do mundo atual (vai junto no save)
    private static WorldProgress mundo;

    // RNG único para encontros
    private static final Random RNG = new Random();

    // Pacote de save (classe interna para evitar conflito de arquivo)
    private static class SaveData implements Serializable {
        private final Personagem jogador;
        private final WorldProgress world;
        SaveData(Personagem jogador, WorldProgress world) {
            this.jogador = jogador;
            this.world = world;
        }
        Personagem getJogador() { return jogador; }
        WorldProgress getWorld() { return world; }
    }

    public static void iniciar() {
        Scanner sc = new Scanner(System.in);
        boolean rodando = true;

        while (rodando) {
            Efeitos.limparTela();

            System.out.println("======= RPG: MIL FLAGELOS =======");
            System.out.println("Uma história de League of Legends");
            System.out.println();
            System.out.println("        -Feito por Fillipe e Hugo");
            System.out.println("=================================");
            System.out.println();
            System.out.println();
            System.out.println("[1] Começar");
            System.out.println("[2] Carregar");
            System.out.println("[3] Rank");
            System.out.println("[4] Configurações");
            System.out.println("[5] Sair");
            System.out.print("\nEscolha uma opção: ");
            String opcao = sc.nextLine().trim();

            switch (opcao) {
                case "1" -> comecarJogo(sc);
                case "2" -> carregarJogo(sc);
                case "3" -> mostrarRank(sc);
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

    // ───────────────────────────────
    // 1) Fluxo de "Começar": seleção do herói + intro + loop com MUNDO
    private static void comecarJogo(Scanner sc) {
        Personagem jogador = selecionarHeroi(sc);

        // Itens iniciais (pronto pra buffs de ATK/DEF futuramente)
        darItensIniciais(jogador);

        // Intro personalizada com efeito digitando (TEXTOS MANTIDOS)
        Efeitos.limparTela();
        System.out.println("Você escolheu: " + jogador.getNome() + " — " + jogador.getClasse() + "\n");
        Efeitos.limparTela();
        String[] cena2 = {
                "Volibear está desperto. E sua ira ameaça engolir o mundo",
                "E tudo que habíta nele",
                "..."
        };
        Efeitos.textoDigitando(cena2, 35, 800);
        System.out.println("\n[Pressione Enter para continuar]");
        sc.nextLine();
        Efeitos.limparTela();
        String[] cena3 = {
                "Agora não tem mais volta...",
                "A sua jornada para deter o Urso de Mil Flagelos...",
                "Começa agora!"
        };
        Efeitos.textoDigitando(cena3, 25, 650);
        System.out.println("\n[Pressione Enter para iniciar a jornada]");
        sc.nextLine();

        // Inicializa o mundo novo
        mundo = new WorldProgress();

        // Loop principal com mundo integrado
        loopPrincipal(sc, jogador);
    }

    private static void loopPrincipal(Scanner sc, Personagem jogador) {
        boolean jogando = true;
        while (jogando && jogador.vivo()) {
            Efeitos.limparTela();
            System.out.println("=== STATUS ===");
            System.out.println(jogador);

            System.out.println("\nInventário:");
            System.out.println(jogador.getInventario());

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
                        System.out.println("Você explora a área: " + mundo.getAreaAtual().def().getNome());
                        // Encontro aleatório
                        tentarEncontro(sc, jogador, idx);
                        // ganho de xp demo
                        jogador.ganharXp(5);
                        System.out.println("+5 XP!");
                    } else {
                        System.out.println("Esta área esgotou suas salas (limite atingido).");
                    }
                    aguardarEnter(sc);
                }
                case "2" -> {
                    System.out.println(mundo.mapa()); // mostra só liberadas
                    System.out.print("Qual área deseja explorar? (número) ");
                    try {
                        int num = Integer.parseInt(sc.nextLine());
                        int idx = num - 1;

                        if (idx < 0 || idx >= mundo.getUnlockedCount()) {
                            System.out.println("Área inválida. Selecione uma das áreas liberadas exibidas no mapa.");
                        } else {
                            // troca o foco da área atual
                            mundo.setAreaAtual(idx);

                            if (mundo.explorarNaArea(idx)) {
                                System.out.println("Você explora a área: " + mundo.getAreas().get(idx).def().getNome());
                                // Encontro aleatório
                                tentarEncontro(sc, jogador, idx);
                                jogador.ganharXp(5);
                                System.out.println("+5 XP!");
                            } else {
                                System.out.println("Não foi possível (esta área já esgotou as salas).");
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Entrada inválida.");
                    }
                    aguardarEnter(sc);
                }

                case "3" -> {
                    System.out.println(mundo.mapa()); // ou mundo.mapaCurto();
                    aguardarEnter(sc);
                }

                case "4" -> {
                    // Antes de avançar, se a área puder avançar, enfrenta o BOSS da área atual
                    if (mundo.podeAvancar()) {
                        int idxAtual = mundo.getAreaAtualIndex();
                        // Usa total de áreas reais do mundo
                        Personagem boss = bossDaArea(idxAtual, mundo.getAreas().size());
                        System.out.println("\n⚠ Você sente uma presença poderosa bloqueando seu caminho...");
                        iniciarCombate(sc, jogador, boss);
                        if (!jogador.vivo()) {
                            // derrota do jogador encerra
                            break;
                        }
                        // Se venceu, avança a área
                        mundo.avancarArea();
                        System.out.println("Você avançou para: " + mundo.getAreaAtual().def().getNome());
                    } else {
                        System.out.println("Ainda não liberou o avanço. Explore mais salas na área atual.");
                    }
                    aguardarEnter(sc);
                }
                case "5" -> {
                    menuUsarItem(sc, jogador);
                }
                case "6" -> {
                    salvarJogoPacote(jogador, mundo);
                    aguardarEnter(sc);
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
            aguardarEnter(sc);
        }
    }

    /** 70% de chance de encontro; dentro disso, 10% vira mini-boss (FlageloSupremo). */
    private static void tentarEncontro(Scanner sc, Personagem jogador, int idxArea) {
        double roll = RNG.nextDouble();
        if (roll <= 0.70) {
            Personagem inimigo;
            if (roll <= 0.07) {
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

    /** Inicia o combate com o inimigo indicado. */
    private static void iniciarCombate(Scanner sc, Personagem jogador, Personagem inimigo) {
        Combate combate = new Combate(jogador, inimigo);
        combate.iniciar();
    }

    /** Retorna um inimigo comum aleatório, escalado à área. */
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
        if (idxArea >= ultimoIndice) {
            return new Volibear();
        }
        return switch (idxArea) {
            case 0 -> new Darius();
            case 1 -> new Trundle();
            case 2 -> new Sylas();
            case 3 -> new Lissandra();
            default -> new Volibear();
        };
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

    // ===== Inventário: seed inicial =====
    private static void darItensIniciais(Personagem jogador) {
        jogador.adicionarItem("Poção de Cura", "Recupera uma quantidade de PV.", Item.Efeito.CURA, START_QTD_POTION_CURA);
        jogador.adicionarItem("Poção de Mana", "Recupera uma quantidade de PM.", Item.Efeito.MANA, START_QTD_POTION_MANA);

        // Hooks prontos para futuro:
        // jogador.adicionarItem("Tônico de Fúria", "Aumenta ATK neste turno.", Item.Efeito.ATAQUE, 1);
        // jogador.adicionarItem("Tônico de Aço", "Aumenta DEF neste turno.", Item.Efeito.DEFESA, 1);
        // jogador.adicionarItem("Elixir do Herói", "Cura e concede pequenos bônus temporários.", Item.Efeito.BUFF_GERAL, 1);
    }

    // ===== Menu Usar Item =====
    private static void menuUsarItem(Scanner sc, Personagem jogador) {
        Inventario inv = jogador.getInventario();
        List<Item> lista = inv.listarOrdenado();

        if (lista.isEmpty()) {
            System.out.println("\nInventário vazio, gordão!");
            aguardarEnter(sc);
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

        String s = sc.nextLine().trim();
        int escolha;
        try {
            escolha = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            aguardarEnter(sc);
            return;
        }
        if (escolha == 0) return;

        int idx = escolha - 1;
        if (idx < 0 || idx >= lista.size()) {
            System.out.println("Opção inválida.");
            aguardarEnter(sc);
            return;
        }

        Item escolhido = lista.get(idx);
        System.out.printf("Usar '%s'? (s/N) ", escolhido.getNome());
        String conf = sc.nextLine().trim().toLowerCase();
        if (!conf.equals("s")) return;

        boolean ok = jogador.usarItem(escolhido.getNome(), escolhido.getEfeito());
        if (ok) {
            System.out.println("Item usado!");
        } else {
            System.out.println("Não foi possível usar o item.");
        }
        aguardarEnter(sc);
    }

    // ───────────────────────────────
    // 2) Carregar / Salvar / Rank / Config
    private static void carregarJogo(Scanner sc) {
        Efeitos.limparTela();
        Carregado c = lerSaveCompat();
        if (c == null || c.jogador == null) {
            System.out.println("Nenhum save válido encontrado.");
            aguardarEnter(sc);
            return;
        }
        // seta o mundo atual
        mundo = (c.mundo != null) ? c.mundo : new WorldProgress();

        System.out.println("Save carregado: " + c.jogador.getNome() + " — Nível " + c.jogador.getNivel());
        aguardarEnter(sc);

        loopPrincipal(sc, c.jogador);

        if (!c.jogador.vivo()) {
            registrarPontuacao(c.jogador.getNome(), c.jogador.getNivel());
            System.out.println("\nGame Over. Nível alcançado: " + c.jogador.getNivel());
            aguardarEnter(sc);
        }
    }

    private static void mostrarRank(Scanner sc) {
        Efeitos.limparTela();
        System.out.println("=== RANK DOS HERÓIS ===\n");
        File f = new File(RANK_PATH);
        if (!f.exists()) {
            System.out.println("Sem registros ainda.");
            aguardarEnter(sc);
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha;
            int pos = 1;
            while ((linha = br.readLine()) != null) {
                // formato: nivel;nome;timestamp
                String[] t = linha.split(";");
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
        System.out.println("Dificuldade: Normal");
        System.out.println("\n[Pressione Enter para voltar]");
        sc.nextLine();
    }

    // ───────────────────────────────
    // Utilidades de entrada/saída
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

    private static void aguardarEnter(Scanner sc) {
        System.out.println("\n[Pressione Enter para continuar]");
        sc.nextLine();
    }

    // ───────────────────────────────
    // SALVAR / CARREGAR — novo formato (jogador+mundo) com retrocompat
    private static void salvarJogoPacote(Personagem p, WorldProgress w) {
        try {
            File arquivo = new File(SAVE_PATH);
            arquivo.getParentFile().mkdirs(); // garante pastas
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(arquivo))) {
                out.writeObject(new SaveData(p, w));
            }
            System.out.println("Jogo salvo com sucesso.");
        } catch (Exception e) {
            System.out.println("Falha ao salvar: " + e.getMessage());
        }
    }

    /** Estrutura de retorno para carregamento compatível. */
    private static class Carregado {
        Personagem jogador;
        WorldProgress mundo;
    }

    /**
     * Tenta ler no formato novo (SaveData). Se falhar, tenta o formato antigo (só Personagem).
     * Se conseguir o antigo, instancia um WorldProgress novo para não quebrar o fluxo.
     */
    private static Carregado lerSaveCompat() {
        File arquivo = new File(SAVE_PATH);
        if (!arquivo.exists()) return null;

        // 1) tenta formato novo
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            Object o = in.readObject();
            if (o instanceof SaveData sd) {
                Carregado c = new Carregado();
                c.jogador = sd.getJogador();
                c.mundo = sd.getWorld();
                return c;
            }
        } catch (Exception ignored) {}

        // 2) tenta formato antigo (só Personagem)
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            Object o = in.readObject();
            if (o instanceof Personagem p) {
                Carregado c = new Carregado();
                c.jogador = p;
                c.mundo = new WorldProgress(); // cria um mundo do zero
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
}

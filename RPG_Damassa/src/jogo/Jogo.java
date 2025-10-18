package jogo;

import util.Efeitos;
import personagens.Personagem;
import personagens.herois.FabricaDePersonagens;

import java.io.*;
import java.util.Scanner;

// IMPORTA O MUNDO
import mundo.WorldProgress;

/**
 * Jogo.java integrado com:
 * - Sistema de Áreas/Mundo (WorldProgress)
 * - Salvamento de pacote (Personagem + Mundo) com retrocompatibilidade
 * - Menu de exploração por área (mínimo p/ avançar + limite de salas)
 * - "Usar item (demo)" mantido como placeholder
 *
 * IMPORTANTE: textos da história NÃO foram alterados.
 */
public class Jogo {

    private static final String SAVE_PATH = "src/data/saves/save.dat";
    private static final String RANK_PATH = "src/data/rank/rank.txt";

    // Estado do mundo atual (vai junto no save)
    private static WorldProgress mundo;

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

            System.out.println("===== RPG: THOUSAND PIERCED =====");
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

        // Intro personalizada com efeito digitando (TEXTOS MANTIDOS)
        Efeitos.limparTela();
        System.out.println("Você escolheu: " + jogador.getNome() + " — " + jogador.getClasse() + "\n");
        Efeitos.textoDigitando(jogador.intro(), 28, 650);
        System.out.println("\n[Pressione Enter para começar a jornada]");
        sc.nextLine();

        // Cena inicial (TEXTOS MANTIDOS)
        Efeitos.limparTela();
        String[] cena1 = {
                "Ao chegar em Freljord, você sente o solo tremer sob seus pés.",
                "Ao horizonte é possivel ver as Montanhas Tempestuosas onde Volibear habíta",
        };
        Efeitos.textoDigitando(cena1, 35, 650);
        System.out.println("\n[Pressione Enter para continuar]");
        sc.nextLine();

        Efeitos.limparTela();
        String[] cena2 = {
                "CENÁRIO: ",
                "Você ainda está longe de seu objetivo.",
                "O ar é denso, pesado, a cada segundo a neve cai mais forte,",
                "o horizonte se parte com trovões e cada trovoada traz o eco",
                "de um rugido de fúria...",
                "",
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

            System.out.println("\nAções:");
            System.out.println("[1] Explorar (área atual)");
            System.out.println("[2] Explorar em outra área");
            System.out.println("[3] Ver Mapa/Áreas");
            System.out.println("[4] Avançar para próxima área");
            System.out.println("[5] Usar Item (demo)");
            System.out.println("[6] Salvar Jogo");
            System.out.println("[7] Pausar");
            System.out.println("[8] Voltar ao Menu");
            System.out.print("> ");

            String op = sc.nextLine().trim();
            switch (op) {
                case "1" -> {
                    int idx = mundo.getAreaAtualIndex();
                    if (mundo.explorarNaArea(idx)) {
                        // Aqui você pode disparar o "evento de exploração" (inimigo/armadilha/loot)
                        System.out.println("Você explora a área: " + mundo.getAreaAtual().def().getNome());
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
                            // >>> troca o foco da área atual <<<
                            mundo.setAreaAtual(idx);

                            if (mundo.explorarNaArea(idx)) {
                                System.out.println("Você explora a área: " + mundo.getAreas().get(idx).def().getNome());
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
                    if (mundo.podeAvancar()) {
                        mundo.avancarArea();
                        System.out.println("Você avançou para: " + mundo.getAreaAtual().def().getNome());
                    } else {
                        System.out.println("Ainda não liberou o avanço. Explore mais salas na área atual.");
                    }
                    aguardarEnter(sc);
                }
                case "5" -> {
                    // DEMO: usar item sem mexer na tua estrutura (integre com Inventario real depois)
                    jogador.curar(5);
                    System.out.println(jogador.getNome() + " recuperou 5 PV.");
                    aguardarEnter(sc);
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
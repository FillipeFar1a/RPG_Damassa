package jogo;

import util.Efeitos;
import personagens.Personagem;
import personagens.herois.FabricaDePersonagens;

import java.io.*;
import java.util.Scanner;

public class Jogo {

    private static final String SAVE_PATH = "src/data/saves/save.dat";
    private static final String RANK_PATH = "src/data/rank/rank.txt";

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
    // 1) Fluxo de "Começar": seleção do herói + intro + loop básico
    private static void comecarJogo(Scanner sc) {
        Personagem jogador = selecionarHeroi(sc);

        // Intro personalizada com efeito digitando
        Efeitos.limparTela();
        System.out.println("Você escolheu: " + jogador.getNome() + " — " + jogador.getClasse() + "\n");
        Efeitos.textoDigitando(jogador.intro(), 28, 650);
        System.out.println("\n[Pressione Enter para começar a jornada]");
        sc.nextLine();

        // Cena inicial após a introdução do herói
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

        // Loop básico de gameplay (placeholder)
        boolean jogando = true;
        while (jogando && jogador.vivo()) {
            Efeitos.limparTela();
            System.out.println("=== STATUS ===");
            System.out.println(jogador);

            System.out.println("\nAções:");
            System.out.println("[1] Explorar");
            System.out.println("[2] Usar Item (demo)");
            System.out.println("[3] Salvar Jogo");
            System.out.println("[4] Pausar");
            System.out.println("[5] Voltar ao Menu");
            System.out.print("> ");

            String op = sc.nextLine().trim();
            switch (op) {
                case "1" -> {
                    // TODO: integrar combate/eventos
                    System.out.println("Você explora as planícies geladas de Freljord...");
                    System.out.println("Sente o ar ficar pesado — o trovão observa.");
                    // ganho de xp de demo
                    jogador.ganharXp(5);
                    System.out.println("+5 XP!");
                    aguardarEnter(sc);
                }
                case "2" -> {
                    // TODO: integrar inventário real
                    System.out.println(jogador.getNome() + " recuperou 5 PV.");
                    aguardarEnter(sc);
                }
                case "3" -> {
                    salvarJogo(jogador);
                    aguardarEnter(sc);
                }
                case "4" -> {
                    System.out.println("\n[PAUSE] Pressione Enter para continuar...");
                    sc.nextLine();
                }
                case "5" -> jogando = false;
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
        Personagem p = lerSave();
        if (p == null) {
            System.out.println("Nenhum save válido encontrado.");
            aguardarEnter(sc);
            return;
        }
        System.out.println("Save carregado: " + p.getNome() + " — Nível " + p.getNivel());
        aguardarEnter(sc);

        // Pequeno loop para continuar jogando com o personagem carregado
        boolean jogando = true;
        while (jogando && p.vivo()) {
            Efeitos.limparTela();
            System.out.println("=== STATUS (Carregado) ===");
            System.out.println(p);

            System.out.println("\nAções:");
            System.out.println("[1] Explorar");
            System.out.println("[2] Usar Item (demo)");
            System.out.println("[3] Salvar Jogo");
            System.out.println("[4] Pausar");
            System.out.println("[5] Voltar ao Menu");
            System.out.print("> ");

            String op = sc.nextLine().trim();
            switch (op) {
                case "1" -> {
                    System.out.println("Você explora com cautela… o vento traz um rugido distante.");
                    p.ganharXp(5);
                    System.out.println("+5 XP!");
                    aguardarEnter(sc);
                }
                case "2" -> {
                    p.curar(5);
                    System.out.println(p.getNome() + " recuperou 5 PV.");
                    aguardarEnter(sc);
                }
                case "3" -> {
                    salvarJogo(p);
                    aguardarEnter(sc);
                }
                case "4" -> {
                    System.out.println("\n[PAUSE] Pressione Enter para continuar...");
                    sc.nextLine();
                }
                case "5" -> jogando = false;
                default -> {
                    System.out.println("Opção inválida!");
                    Efeitos.esperar(800);
                }
            }
        }

        if (!p.vivo()) {
            registrarPontuacao(p.getNome(), p.getNivel());
            System.out.println("\nGame Over. Nível alcançado: " + p.getNivel());
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

    private static void salvarJogo(Personagem p) {
        try {
            File arquivo = new File(SAVE_PATH);
            arquivo.getParentFile().mkdirs(); // garante pastas
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(arquivo))) {
                out.writeObject(p);
            }
            System.out.println("Jogo salvo com sucesso.");
        } catch (Exception e) {
            System.out.println("Falha ao salvar: " + e.getMessage());
        }
    }

    private static Personagem lerSave() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_PATH))) {
            return (Personagem) in.readObject();
        } catch (Exception e) {
            return null;
        }
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

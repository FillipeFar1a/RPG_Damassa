package util;

import personagens.Personagem;
import java.util.Random;
import java.util.Scanner;

public class Combate {

    private Personagem jogador;
    private Personagem inimigo;
    private Random random = new Random();
    private Scanner scanner = new Scanner(System.in);

    public Combate(Personagem jogador, Personagem inimigo) {
        this.jogador = jogador;
        this.inimigo = inimigo;
    }

    public void iniciar() {
        System.out.println("\n=== ⚔️ INÍCIO DA BATALHA ⚔️ ===");
        System.out.println(jogador.getNome() + " (" + jogador.getClasse() + ") VS " + inimigo.getNome() + " (" + inimigo.getClasse() + ")");
        System.out.println("-------------------------------");

        while (jogador.vivo() && inimigo.vivo()) {
            turnoJogador();
            if (!inimigo.vivo()) break;

            turnoInimigo();
            if (!jogador.vivo()) break;
        }

        if (jogador.vivo()) {
            System.out.println("\n🏆 " + jogador.getNome() + " venceu a batalha!");
            jogador.ganharXp(10);
        } else {
            System.out.println("\n💀 " + jogador.getNome() + " foi derrotado...");
        }

        System.out.println("===============================");
    }

    private void turnoJogador() {
        System.out.println("\n--- Seu turno ---");
        System.out.println(jogador);
        System.out.println("1 - Ataque básico");
        System.out.println("2 - Usar habilidade");
        System.out.print("Escolha: ");
        int escolha = scanner.nextInt();

        switch (escolha) {
            case 1 -> ataqueComDado(jogador, inimigo);
            case 2 -> jogador.usarHabilidade(inimigo);
            default -> System.out.println("Você hesitou e perdeu o turno...");
        }

        if (inimigo.vivo()) {
            inimigoStatus();
        }
    }

    private void turnoInimigo() {
        System.out.println("\n--- Turno do inimigo ---");
        if (inimigo.isCongelado()) {
            System.out.println(inimigo.getNome() + " está incapacitado e perde o turno!");
            inimigo.setCongelado(false);
            return;
        }

        // 50% de chance de usar habilidade ou ataque básico
        if (random.nextBoolean()) {
            inimigo.usarHabilidade(jogador);
        } else {
            ataqueComDado(inimigo, jogador);
        }

        if (jogador.vivo()) {
            jogadorStatus();
        }
    }

    private void ataqueComDado(Personagem atacante, Personagem defensor) {
        int rolagem = random.nextInt(6) + 1; // d6
        int danoBase = atacante.getAtk() + rolagem;
        int danoFinal = Math.max(1, danoBase - defensor.getDef());

        defensor.receberDano(danoFinal);

        System.out.println(atacante.getNome() + " rola um d6 e tira " + rolagem + "!");
        System.out.println(atacante.getNome() + " ataca causando " + danoFinal + " de dano!");
    }

    private void jogadorStatus() {
        System.out.println("PV do jogador: " + jogador.getPv() + "/" + jogador.getPvMax());
    }

    private void inimigoStatus() {
        System.out.println("PV do inimigo: " + inimigo.getPv() + "/" + inimigo.getPvMax());
    }
}

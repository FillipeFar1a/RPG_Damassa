package personagens.inimigos;

import personagens.Personagem;
import util.Combate;

import java.util.concurrent.ThreadLocalRandom;

public class Volibear extends Personagem {
    private boolean segundaFaseAtivada = false;

    public Volibear() {
        super("Volibear", "Pré-Boss", 260, 50, 30, 20, 11, 24);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "O céu ruge. Volibear caminha como uma tempestade.",
                "Relâmpagos dançam ao seu redor."
        };
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        // Ira da Tempestade: dano com chance de atordoar via 'congelado' (representa stun)
        int dano = this.getAtkEfetivo() + ThreadLocalRandom.current().nextInt(3, 8);
        alvo.receberDano(dano);
        boolean atordoar = ThreadLocalRandom.current().nextInt(100) < 25; // 25%
        if (atordoar) alvo.setCongelado(true);
    }

    @Override
    public void receberDano(int dano) {
        super.receberDano(dano);

        // Quando Volibear é derrotado pela primeira vez
        if (!this.vivo() && !segundaFaseAtivada) {
            segundaFaseAtivada = true;

            System.out.println("O corpo de Volibear fraqueja... mas a tempestade não se dissipou.");
            System.out.println("Raios caem do céu, e o rugido ecoa: 'VOCÊ OUSA ENFRENTAR A TEMPESTADE!'");
            System.out.println("Volibear mostra seu verdadeiro poder e aparência");
            System.out.println("A fúria encarnada dos trovões desperta");
            System.out.println("Pressione ENTER para enfrentar o Urso de Mil Flagelos...");
            try { System.in.read(); } catch (Exception ignored) {}

            // Inicia o novo combate com o Urso de Mil Flagelos
            UrsoDeMilFlagelos segundaForma = new UrsoDeMilFlagelos();
            Combate novoCombate = new Combate(util.Efeitos.getJogadorAtual(), segundaForma);
            novoCombate.iniciar();
        }
    }
}

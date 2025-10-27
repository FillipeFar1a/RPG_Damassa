package personagens.inimigos;

import personagens.Personagem;

public class Kindred extends Personagem {

    public Kindred(int nivelJogador) {
        super(
                "Kindred",
                "A Morte",
                180 + 8 * Math.max(1, nivelJogador),
                80  + 4 * Math.max(1, nivelJogador),
                28  + 2 * Math.max(1, nivelJogador),
                14  + 2 * Math.max(1, nivelJogador),
                Math.max(1, nivelJogador + 2),
                50
        );
    }

    @Override
    public String[] intro() {
        return new String[] {
                "— \"Toda caçada precisa de um fim.\"",
                "Você sente um arrepio: o Cordeiro aponta, o Lobo sorri.",
                "A Morte não chegou cedo. Chegou na hora."
        };
    }

    /**
     * IK garantido: ceifa imediatamente o alvo (PV → 0), ignorando DEF, PV atual etc.
     * Dica: se quiser “contrajogo”, trate imunidades específicas no Combate (ex.: fase de invulnerabilidade).
     */
    @Override
    public void usarHabilidade(Personagem alvo) {
        System.out.println(getNome() + " marca " + alvo.getNome() + "...");
        System.out.println("— Cordeiro: \"Agora.\"  Lobo: *rosna*");
        alvo.setPv(0); // IK
    }
}

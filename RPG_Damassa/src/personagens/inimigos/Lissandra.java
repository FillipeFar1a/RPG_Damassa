package personagens.inimigos;

import personagens.Personagem;
import java.util.concurrent.ThreadLocalRandom;

public class Lissandra extends Personagem {
    public Lissandra() {
        super("Lissandra", "Boss", 200, 100, 25, 17, 10, 22);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "O frio antigo desperta. Lissandra observa — implacável.",
                "Estátuas de gelo contam histórias de derrotas passadas."
        };
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        // Túmulo de Gelo (simplificado): dano e chance de congelar 1 turno
        int dano = this.getAtkEfetivo() + ThreadLocalRandom.current().nextInt(2, 7) + 3;
        alvo.receberDano(dano);
        boolean congelar = ThreadLocalRandom.current().nextInt(100) < 30; // 30%
        if (congelar) alvo.setCongelado(true);
    }
}

package personagens.inimigos;

import personagens.Personagem;
import java.util.concurrent.ThreadLocalRandom;

public class FlageloArqueiro extends Personagem {
    public FlageloArqueiro(int nivelArea) {
        super("Flagelo Arqueiro", "Inimigo",
                15 + 3*nivelArea, 0,
                3 + 2*nivelArea, 2 + 2*nivelArea,
                Math.max(1, nivelArea), 12);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "Um Flagelo Arqueiro mira sem hesitar.",
                "O vento corta — a flecha também."
        };
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        // Tiro Preciso: chance de crítico leve
        int base = this.getAtkEfetivo() + ThreadLocalRandom.current().nextInt(1, 7);
        boolean critico = ThreadLocalRandom.current().nextInt(100) < 20; // 20%
        int danoBruto = critico ? base + 4 : base;
        alvo.receberDano(danoBruto);
    }
}

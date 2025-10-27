package personagens.inimigos;

import personagens.Personagem;
import java.util.concurrent.ThreadLocalRandom;

public class FlageloGigante extends Personagem {
    public FlageloGigante(int nivelArea) {
        super("Flagelo Gigante", "Inimigo",
                30 + 5*nivelArea, 0,
                6 + 2*nivelArea, 5 + 3*nivelArea,
                Math.max(1, nivelArea), 30);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "Um colosso coberto de gelo emerge.",
                "Cada passo é um terremoto."
        };
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        // Esmagar: dano bruto alto mas rolagem mais instável
        int rolagem = ThreadLocalRandom.current().nextInt(0, 8); // 0..7
        int dano = this.getAtkEfetivo() + rolagem + 2;
        alvo.receberDano(dano);
    }
}

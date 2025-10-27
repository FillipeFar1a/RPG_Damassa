package personagens.inimigos;

import personagens.Personagem;
import java.util.concurrent.ThreadLocalRandom;

public class FlageloGigante extends Personagem {
    public FlageloGigante(int nivelArea) {
        super("Flagelo Gigante", "Inimigo",
                70 + 10*nivelArea, 0,
                14 + 2*nivelArea, 10 + 3*nivelArea,
                Math.max(1, nivelArea), 12);
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

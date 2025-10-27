package personagens.inimigos;

import personagens.Personagem;
import java.util.concurrent.ThreadLocalRandom;

public class FlageloMago extends Personagem {
    public FlageloMago(int nivelArea) {
        super("Flagelo Mago", "Inimigo",
                 10 + 5*nivelArea, 30 + 5*nivelArea,
                5 + 3*nivelArea, 5 + 2*nivelArea,
                Math.max(1, nivelArea), 10);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "Runas antigas brilham no ar gélido.",
                "O Flagelo Mago entoa palavras proibidas."
        };
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        // Raio Gélido: dano moderado; se PM suficiente, +dano
        int dano = this.getAtkEfetivo() + ThreadLocalRandom.current().nextInt(2, 7);
        if (this.getPm() >= 5) {
            dano += 3;
            this.gastarMana(5);
        }
        alvo.receberDano(dano);
    }
}

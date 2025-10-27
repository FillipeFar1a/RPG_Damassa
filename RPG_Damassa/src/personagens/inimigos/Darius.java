package personagens.inimigos;

import personagens.Personagem;
import java.util.concurrent.ThreadLocalRandom;

public class Darius extends Personagem {
    public Darius() {
        super("Darius", "Boss", 50, 20, 25, 10, 8, 120);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "Darius gira o machado e avança com autoridade.",
                "Não há piedade em seus olhos."
        };
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        // Guilhotina Noxiana (simplificada): mais dano com o alvo ferido
        int bonus = (alvo.getPv() < alvo.getPvMax() / 2) ? 6 : 2;
        int dano = this.getAtkEfetivo() + ThreadLocalRandom.current().nextInt(2, 7) + bonus;
        alvo.receberDano(dano);
    }
}

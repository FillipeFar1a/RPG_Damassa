package personagens.inimigos;

import personagens.Personagem;
import java.util.concurrent.ThreadLocalRandom;

public class FlageloSupremo extends Personagem {
    public FlageloSupremo(int nivelArea) {
        super("Flagelo Supremo (Mini-Boss)", "Mini-Boss",
                90 + 12*nivelArea, 20 + 4*nivelArea,
                18 + 3*nivelArea, 12 + 3*nivelArea,
                Math.max(1, nivelArea + 1), 15);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "O ar pesa. Você sente que isso não é um simples flagelo.",
                "O Mini-Boss ergue os braços — o combate começa."
        };
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        // Ruptura: pequeno buff e dano consistente
        this.modificarAtaqueTemporario(2);
        int dano = this.getAtkEfetivo() + ThreadLocalRandom.current().nextInt(2, 7);
        alvo.receberDano(dano);
    }
}

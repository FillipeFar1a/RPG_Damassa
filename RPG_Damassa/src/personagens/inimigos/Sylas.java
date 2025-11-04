package personagens.inimigos;

import personagens.Personagem;
import java.util.concurrent.ThreadLocalRandom;

public class Sylas extends Personagem {
    public Sylas() {
        super("Sylas", "Boss", 210, 60, 27, 15, 9, 20);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "Sylas sorri — correntes arcanas tilintam.",
                "Ele está faminto por magia."
        };
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        // Correntes Silenciantes (fantasia): ataque e leve roubo de ATK por um turno
        int dano = this.getAtkEfetivo() + ThreadLocalRandom.current().nextInt(1, 7) + 2;
        alvo.receberDano(dano);
        this.modificarAtaqueTemporario(2);
        alvo.modificarAtaqueTemporario(-2);
    }
}

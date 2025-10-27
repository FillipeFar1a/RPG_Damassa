package personagens.inimigos;

import personagens.Personagem;
import java.util.concurrent.ThreadLocalRandom;

public class Volibear extends Personagem {
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
    // Observação: ao chegar a 0 PV, no fluxo do jogo, iniciar a 2ª fase com new UrsoDeMilFlagelos()
}

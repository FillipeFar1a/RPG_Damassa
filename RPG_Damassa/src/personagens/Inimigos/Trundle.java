package personagens.inimigos;

import personagens.Personagem;
import java.util.concurrent.ThreadLocalRandom;

public class Trundle extends Personagem {
    public Trundle() {
        super("Trundle", "Boss", 240, 20, 26, 18, 9, 20);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "Trundle bate o cajado no gelo e ri.",
                "A força do troll ecoa na tundra."
        };
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        // Subjugar (simplificado): drena PV e enfraquece a defesa do alvo por um turno
        int dreno = 8 + ThreadLocalRandom.current().nextInt(0, 5);
        alvo.receberDano(this.getAtkEfetivo() + dreno);
        this.curar(4); // se cura um pouco
        alvo.setDef(Math.max(0, alvo.getDef() - 1)); // leve debuff momentâneo
    }
}

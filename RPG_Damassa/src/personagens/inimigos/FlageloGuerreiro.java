package personagens.inimigos;

import personagens.Personagem;
import java.util.concurrent.ThreadLocalRandom;

public class FlageloGuerreiro extends Personagem {
    public FlageloGuerreiro(int nivelArea) {
        super("Flagelo Guerreiro", "Inimigo",
                20 + 4*nivelArea, 0,
                3 + 2*nivelArea, 4 + 2*nivelArea,
                Math.max(1, nivelArea), 15);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "O estrondo das botas no gelo anuncia um Flagelo Guerreiro.",
                "Ele ergue a lâmina com frieza e avança."
        };
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        // Golpe Pesado: bônus temporário e ataque
        int bonus = 1 + ThreadLocalRandom.current().nextInt(0, 2); // +1 ou +2
        this.modificarAtaqueTemporario(bonus);
        int rolagem = ThreadLocalRandom.current().nextInt(1, 7);
        int danoBruto = this.getAtkEfetivo() + rolagem;
        alvo.receberDano(danoBruto);
    }
}

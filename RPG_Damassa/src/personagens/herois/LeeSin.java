package personagens.herois;

import personagens.Personagem;
import java.util.Random;
import java.util.Scanner;

public class LeeSin extends Personagem {
    public LeeSin() {
        super("Lee Sin", "Monge Cego", 32, 16, 8, 4, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "Lee Sin não vê o gelo, mas sente o peso do silêncio.",
                "O mundo segura a respiração antes do trovão.",
                "Ele caminha em fé, para quebrar o ciclo da fúria."
        };
    }
}

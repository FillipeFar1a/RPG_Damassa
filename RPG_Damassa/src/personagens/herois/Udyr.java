package personagens.herois;

import personagens.Personagem;

public class Udyr extends Personagem {
    public Udyr() {
        super("Udyr", "Druída", 34, 18, 7, 5, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "Os espíritos rugem em desespero, e Udyr os escuta a todos.",
                "Há um espírito que não fala — apenas morde o próprio rabo em fúria.",
                "Ele vai ao coração da tempestade apaziguar o urso enlouquecido."
        };
    }
}

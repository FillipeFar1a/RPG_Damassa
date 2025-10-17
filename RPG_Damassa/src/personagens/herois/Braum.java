package personagens.herois;

import personagens.Personagem;

public class Braum extends Personagem {
    public Braum() {
        super("Braum", "Tank", 42, 10, 6, 7, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "Braum sorri, mas o norte não. Portas se trancam, lareiras se apagam.",
                "Ele jurou ser o escudo de Freljord — e um escudo não recua.",
                "Se o Urso de Mil Flagelos vier, encontrará uma parede inquebrável."
        };
    }
}

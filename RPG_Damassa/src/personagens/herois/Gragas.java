package personagens.herois;

import personagens.Personagem;
import java.util.Random;
import java.util.Scanner;

public class Gragas extends Personagem {
    public Gragas() {
        super("Gragas", "Bêbado", 38, 14, 8, 4, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "Gragas procurava o barril perfeito e encontrou… tempestades.",
                "Se o gelo vai quebrar, que seja com estilo — e muitos goles.",
                "Ele brinda à pancadaria e tropeça rumo ao trovão."
        };
    }
}

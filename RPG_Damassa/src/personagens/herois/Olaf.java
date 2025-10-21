package personagens.herois;

import personagens.Personagem;
import java.util.Random;
import java.util.Scanner;

public class Olaf extends Personagem {
    public Olaf() {
        super("Olaf", "Berserker", 36, 12, 9, 3, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "Olaf ouviu que o fim se aproxima — e riu alto.",
                "Se o fim vier com trovões, melhor ainda.",
                "Ele corre em direção à tempestade como quem corre para casa."
        };
    }
}

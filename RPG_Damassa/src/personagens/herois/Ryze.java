package personagens.herois;

import personagens.Personagem;

public class Ryze extends Personagem {
    public Ryze() {
        super("Ryze", "Mago", 29, 28, 8, 3, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "Runas antigas sussurram um presságio: o nome do urso esquecido.",
                "Ryze conhece o preço do poder desmedido.",
                "Vai recolher os fragmentos antes que destruam o mundo."
        };
    }
}

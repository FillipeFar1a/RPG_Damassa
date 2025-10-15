package personagens.herois;

import personagens.Personagem;

public class Tryndamere extends Personagem {
    public Tryndamere() {
        super("Tryndamere", "Guerreiro", 36, 10, 9, 3, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "A fúria de Tryndamere não encontra descanso.",
                "Pesadelos com um rugido antigo o perseguem.",
                "Hoje, ele decidiu perseguir o trovão até o fim."
        };
    }
}

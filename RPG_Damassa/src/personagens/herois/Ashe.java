package personagens.herois;

import personagens.Personagem;

public class Ashe extends Personagem {
    public Ashe() {
        super("Ashe", "Arqueira", 30, 18, 7, 4, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "Ashe sente o vento cortar o gelo de Freljord.",
                "As tribos estão inquietas; os Flagelos tomam as rotas de caça.",
                "Se o trovão despertar, não restará reino para liderar."
        };
    }
}

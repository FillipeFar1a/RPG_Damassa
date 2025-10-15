package personagens.herois;

import personagens.Personagem;

public class Aurora extends Personagem {
    public Aurora() {
        super("Aurora", "Maga", 28, 26, 8, 3, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "Aurora lê sinais nas nevascas — padrões que sussurram um nome proibido.",
                "A magia se agita; algo antigo desperta sob o gelo.",
                "Ela parte para selar o que nunca deveria ter sido lembrado."
        };
    }
}

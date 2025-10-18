package itens.consumiveis;

import itens.Item;
import itens.base.Usavel;
import personagens.Personagem;

public class PocaoCuraSuprema extends Item implements Usavel {
    public static final int CURA = 50;

    public PocaoCuraSuprema(int quantidade) {
        super("Poção de Cura Suprema", "Restaura " + CURA + " PV", "cura", quantidade);
    }

    @Override
    public boolean usarEm(Personagem alvo) {
        alvo.curar(CURA);
        return true;
    }
}

package itens.consumiveis;

import itens.Item;
import itens.base.Usavel;
import personagens.Personagem;

public class PocaoCuraPequena extends Item implements Usavel {
    public static final int CURA = 5;

    public PocaoCuraPequena(int quantidade) {
        super("Poção de Cura Pequena", "Cura " + CURA + " PV", "cura", quantidade);
    }

    @Override
    public boolean usarEm(Personagem alvo) {
        // Requer em Personagem: curar(int qtd)
        alvo.curar(CURA);
        return true;
    }
}

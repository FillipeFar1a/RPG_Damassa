package itens.base;

import personagens.Personagem;

/** Itens consumíveis que executam um efeito imediato (ex.: poções). */
public interface Usavel {
    /**
     * Aplica o efeito no alvo e consome 1 unidade do item (quem controla o consumo é o inventário/chamador).
     * Retorna true se aplicou com sucesso.
     */
    boolean usarEm(Personagem alvo);
}

package itens.base;

import personagens.Personagem;

/**
 * Contrato para efeitos que disparam a cada turno.
 * A engine de combate deve chamar ao início do turno do portador.
 */
public interface EfeitoPorTurno {
    /**
     * Chamado no início do turno do portador.
     * @param portador   Quem está com o efeito aplicado/equipado.
     * @param adversario Oponente atual (pode ser null se não houver combate).
     */
    void aoInicioDoTurno(Personagem portador, Personagem adversario);
}

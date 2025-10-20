package itens.consumiveis;

import itens.Item;
import itens.base.Usavel;
import itens.base.EfeitoPorTurno;
import personagens.Personagem;

/**
 * Ao usar: aplica um debuff no inimigo que reduz o dano dele por N turnos.
 * Integração mínima: Personagem deve permitir registrar efeitos por turno.
 */
public class GeloVerdadeiro extends Item implements Usavel {
    public static final int REDUCAO_POR_TURNO = 2; // ex.: -2 dano base
    public static final int DURACAO_TURNOS = 3;

    public GeloVerdadeiro(int quantidade) {
        super("Gelo Verdadeiro", "Reduz o dano do inimigo por " + DURACAO_TURNOS + " turnos", "debuff-dano", quantidade);
    }

    @Override
    public boolean usarEm(Personagem inimigo) {
        // Você pode implementar no Personagem: addEfeito(EfeitoPorTurno e, int turnos)
        inimigo.adicionarEfeito(new DebuffReducaoDano(), DURACAO_TURNOS);
        return true;
    }

    /** Implementação do efeito por turno (classe interna simples). */
    public static class DebuffReducaoDano implements EfeitoPorTurno {
        @Override
        public void aoInicioDoTurno(Personagem portador, Personagem adversario) {
            // Requer: diminuirDanoTemporario(int qtd) OU set/getAtk com lógica temporária
            portador.modificarAtaqueTemporario(-REDUCAO_POR_TURNO);
        }
    }
}

package personagens.inimigos;

import personagens.Personagem;

public class UrsoDeMilFlagelos extends Personagem {

    public UrsoDeMilFlagelos() {
        super(
                "Urso de Mil Flagelos",   // nome
                "Boss Final",             // classe
                320,                      // pvMax
                80,                       // pmMax
                36,                       // atk
                22,                       // def
                12,                       // nivel
                50                        // xpMax (pode ajustar conforme seu balanceamento)
        );
    }

    @Override
    public String[] intro() {
        return new String[]{
                "O céu ruge com mil trovões.",
                "O gelo se parte e o Urso de Mil Flagelos desperta.",
                "A tempestade atende ao seu chamado."
        };
    }

    /**
     * Habilidade do Boss Final:
     * - Se tiver ≥ 20 PM: "Tempestade Flagelante" — consome 20 PM, ganha +6 ATK temporário
     *   e desfere um golpe pesado (ATK efetivo + 12).
     * - Caso contrário: "Trovão Uivante" — golpe menor (ATK efetivo + 4), 33% de chance de congelar o alvo
     *   e recupera 5 PM (respiro da tempestade).
     */
    @Override
    public void usarHabilidade(Personagem alvo) {
        if (getPm() >= 20) {
            // Tempestade Flagelante
            gastarMana(20);
            modificarAtaqueTemporario(6);
            int dano = getAtkEfetivo() + 12;
            alvo.receberDano(dano);
        } else {
            // Trovão Uivante
            int dano = getAtkEfetivo() + 4;
            alvo.receberDano(dano);

            // 33% de chance de congelar o alvo
            if (Math.random() < (1.0 / 3.0)) {
                alvo.setCongelado(true);
            }

            // Recupera um pouco de mana
            recuperarMana(5);
        }
    }
}

package personagens.inimigos;

import personagens.Personagem;

public class Garen extends Personagem {

    public Garen() {
        super(
                "Garen",
                "Demacia",
                260, 20, 30, 18, 10, 50
        );
    }

    @Override
    public String[] intro() {
        return new String[]{
                "— \"Pela Demacia!\"",
                "O aço brande no ar enquanto Garen avança sem medo.",
                "Você sente o peso da justiça implacável."
        };
    }

    /**
     * IA da habilidade:
     * - Se o alvo estiver com PV <= 10% do PV máximo: EXECUTA (IK) — Justiça Demaciana.
     * - Caso contrário: Julgamento — golpe pesado (ATK efetivo + 10) ignorando 30% da DEF do alvo.
     *
     * Observação: como Personagem.receberDano já subtrai a DEF inteira,
     * para simular "ignorar 30% da DEF" somamos essa parte ao dano BRUTO.
     */
    @Override
    public void usarHabilidade(Personagem alvo) {
        int limiteExec = Math.max(1, (int) Math.floor(alvo.getPvMax() * 0.10));

        if (alvo.getPv() <= limiteExec) {
            System.out.println(getNome() + " ergue a espada: \"Justiça Demaciana!\"");
            alvo.setPv(0); // IK
            return;
        }

        // Julgamento (spin) — ignora 30% da DEF do alvo
        int defIgnorada = (int) Math.floor(alvo.getDef() * 0.30);
        int danoBruto = getAtkEfetivo() + 10 + defIgnorada; // compensa a DEF ignorada

        System.out.println(getNome() + " gira em fúria — JULGAMENTO!");
        alvo.receberDano(danoBruto); // DEF será aplicada aqui (efetivamente 70% da DEF)
    }
}

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
     */
    @Override
    public void usarHabilidade(Personagem alvo) {
        int limiteExec = Math.max(1, (int)Math.floor(alvo.getPvMax() * 0.10));

        if (alvo.getPv() <= limiteExec) {
            System.out.println(getNome() + " ergue a espada: \"Justiça Demaciana!\"");
            alvo.setPv(0); // IK
            return;
        }

        // Julgamento (spin)
        int defIgnorada = (int)Math.floor(alvo.getDef() * 0.30);
        int defEfetiva = Math.max(0, alvo.getDef() - defIgnorada);
        int danoBruto = getAtkEfetivo() + 10;
        int danoFinal = Math.max(1, danoBruto - defEfetiva);

        System.out.println(getNome() + " gira em fúria — JULGAMENTO!");
        alvo.receberDano(danoFinal);
    }
}

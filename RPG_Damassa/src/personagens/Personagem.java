package personagens;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import itens.base.EfeitoPorTurno;

public abstract class Personagem implements Serializable {
    protected String nome;
    protected String classe;
    protected int pvMax, pv;
    protected int pmMax, pm;
    protected int atk, def;
    protected int nivel, xp, xpMax;

    // --- modificadores temporários de turno (ex.: debuffs/buffs de ataque) ---
    private int bonusAtkTemporario = 0;

    // --- efeitos por turno ---
    private final List<EfeitoPorTurno> efeitosPermanentes = new ArrayList<>(); // ex.: equipamentos
    private final List<EfeitoTemporario> efeitosTemporarios = new ArrayList<>(); // ex.: consumíveis com duração

    // Classe de apoio para efeitos temporários (com duração)
    private static class EfeitoTemporario implements Serializable {
        final EfeitoPorTurno efeito;
        int turnosRestantes;
        EfeitoTemporario(EfeitoPorTurno efeito, int turnos) {
            this.efeito = efeito;
            this.turnosRestantes = Math.max(1, turnos);
        }
    }

    protected Personagem(String nome, String classe, int pvMax, int pmMax, int atk, int def, int nivel, int xpMax) {
        this.nome = nome;
        this.classe = classe;
        this.pvMax = pvMax; this.pv = pvMax;
        this.pmMax = pmMax; this.pm = pmMax;
        this.atk = atk; this.def = def;
        this.nivel = nivel; this.xp = 0; this.xpMax = xpMax;
    }

    public boolean vivo() { return pv > 0; }

    public void receberDano(int danoBruto) {
        int dano = Math.max(1, danoBruto - this.def);
        this.pv = Math.max(0, this.pv - dano);
    }

    public void curar(int valor) { this.pv = Math.min(pvMax, pv + valor); }

    // novo utilitário para itens que regeneram mana por rodada
    public void recuperarMana(int valor) { this.pm = Math.min(pmMax, pm + Math.max(0, valor)); }

    public void gastarMana(int custo) { this.pm = Math.max(0, pm - custo); }

    public void ganharXp(int valor) {
        xp += valor;
        while (xp >= xpMax) {
            xp -= xpMax;
            subirNivel();
        }
    }

    protected void subirNivel() {
        nivel++;
        pvMax += 5; pmMax += 3; atk += 1; def += 1;
        pv = pvMax; pm = pmMax; xpMax += 5;
    }

    // ====== modificadores de ataque de turno ======
    public void modificarAtaqueTemporario(int delta) { this.bonusAtkTemporario += delta; }
    public void resetModificadoresDeTurno() { this.bonusAtkTemporario = 0; }
    public int getAtkEfetivo() { return Math.max(0, this.atk + this.bonusAtkTemporario); }

    // ====== API de efeitos por turno ======
    /** Efeitos "permanentes" (ex.: equipamentos enquanto equipados). */
    public void registrarEfeitoPorTurno(EfeitoPorTurno e) {
        if (e != null) efeitosPermanentes.add(e);
    }

    public void removerEfeitoPorTurno(EfeitoPorTurno e) {
        efeitosPermanentes.remove(e);
    }

    /** Efeitos temporários (ex.: consumíveis como Gelo Verdadeiro). */
    public void adicionarEfeito(EfeitoPorTurno e, int turnos) {
        if (e != null && turnos > 0) efeitosTemporarios.add(new EfeitoTemporario(e, turnos));
    }

    /**
     * Chamar no INÍCIO do turno deste personagem.
     * Aplica efeitos permanentes e temporários e decrementa a duração dos temporários.
     */
    public void processarEfeitosInicioDoTurno(Personagem adversario) {
        // zera modificadores de turno antes de aplicar efeitos
        resetModificadoresDeTurno();

        // permanentes
        for (EfeitoPorTurno e : efeitosPermanentes) {
            e.aoInicioDoTurno(this, adversario);
        }

        // temporários (com duração)
        Iterator<EfeitoTemporario> it = efeitosTemporarios.iterator();
        while (it.hasNext()) {
            EfeitoTemporario et = it.next();
            et.efeito.aoInicioDoTurno(this, adversario);
            et.turnosRestantes--;
            if (et.turnosRestantes <= 0) it.remove();
        }
    }

    // ====== pequenos utilitários para equipamentos (sem quebrar nada) ======
    public void addDef(int delta) { this.def = Math.max(0, this.def + delta); }

    // ---- gancho para intro por herói
    public abstract String[] intro();

    // ---- getters úteis
    public String getNome() { return nome; }
    public String getClasse() { return classe; }
    public int getPv() { return pv; }
    public int getPvMax() { return pvMax; }
    public int getPm() { return pm; }
    public int getPmMax() { return pmMax; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public int getNivel() { return nivel; }
    public int getXp() { return xp; }
    public int getXpMax() { return xpMax; }

    @Override
    public String toString() {
        return String.format(
                "%s — %s | Nível %d | PV %d/%d | PM %d/%d | ATK %d | DEF %d | XP %d/%d",
                nome, classe, nivel, pv, pvMax, pm, pmMax, atk, def, xp, xpMax
        );
    }
}

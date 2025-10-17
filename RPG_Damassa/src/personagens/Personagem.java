package personagens;

import java.io.Serializable;

public abstract class Personagem implements Serializable {
    protected String nome;
    protected String classe;
    protected int pvMax, pv;
    protected int pmMax, pm;
    protected int atk, def;
    protected int nivel, xp, xpMax;

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

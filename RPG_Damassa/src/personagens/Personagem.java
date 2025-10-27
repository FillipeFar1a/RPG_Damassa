package personagens;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import itens.Inventario;
import itens.Item;
// usa a interface no mesmo pacote "personagens"
import personagens.EfeitoPorTurno;

public abstract class Personagem implements Serializable {
    protected String nome;
    protected String classe;
    protected int pvMax, pv;
    protected int pmMax, pm;
    protected int atk, def;
    protected int nivel, xp, xpMax;

    // ===== INVENTÁRIO =====
    protected Inventario inventario = new Inventario();

    // ===== MODIFICADORES TEMPORÁRIOS (resetar no começo de cada turno) =====
    private int bonusAtkTemporario = 0;

    // ===== EFEITOS POR TURNO =====
    private final List<EfeitoPorTurno> efeitosPermanentes = new ArrayList<>();
    private final List<EfeitoTemporario> efeitosTemporarios = new ArrayList<>();

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

    // ===== Inventário
    public Inventario getInventario() { return inventario; }
    public void adicionarItem(Item item) { if (item != null) inventario.adicionar(item); }
    public void adicionarItem(String nome, String desc, Item.Efeito efeito, int qtd) {
        inventario.adicionar(nome, desc, efeito, qtd);
    }

    /** Usa 1 unidade do item pelo nome+efeito e aplica efeito imediato. */
    public boolean usarItem(String nomeItem, Item.Efeito efeito) {
        Item usado = inventario.usarUm(nomeItem, efeito);
        if (usado == null) return false;
        aplicarItem(usado);
        return true;
    }

    /** Aplica o item (valores simples; ajuste depois no balance). */
    protected void aplicarItem(Item item) {
        if (item == null) return;
        switch (item.getEfeito()) {
            case CURA       -> curar(20);
            case MANA       -> recuperarMana(15);
            case ATAQUE     -> this.bonusAtkTemporario += 5;  // dura até fim do turno (reset no início)
            case DEFESA     -> this.addDef(+5);               // simples; se quiser com duração, use efeito temporário
            case BUFF_GERAL -> {
                curar(10);
                recuperarMana(10);
                this.bonusAtkTemporario += 3;
                this.addDef(+3);
            }
        }
    }

    // ===== Buff de turno
    public void modificarAtaqueTemporario(int delta) { this.bonusAtkTemporario += delta; }
    /** Chame no INÍCIO do turno deste personagem (ex.: no Combate). */
    public void resetModificadoresDeTurno() { this.bonusAtkTemporario = 0; }
    public int getAtkEfetivo() { return Math.max(0, this.atk + this.bonusAtkTemporario); }

    // ===== Efeitos por turno
    public void registrarEfeitoPorTurno(EfeitoPorTurno e) { if (e != null) efeitosPermanentes.add(e); }
    public void removerEfeitoPorTurno(EfeitoPorTurno e) { efeitosPermanentes.remove(e); }
    public void adicionarEfeito(EfeitoPorTurno e, int turnos) {
        if (e != null && turnos > 0) efeitosTemporarios.add(new EfeitoTemporario(e, turnos));
    }

    /** Início do turno: aplica permanentes e temporários; decrementar duração. */
    public void processarEfeitosInicioDoTurno(Personagem adversario) {
        resetModificadoresDeTurno();
        for (EfeitoPorTurno e : efeitosPermanentes) e.aoInicioDoTurno(this, adversario);
        Iterator<EfeitoTemporario> it = efeitosTemporarios.iterator();
        while (it.hasNext()) {
            EfeitoTemporario et = it.next();
            et.efeito.aoInicioDoTurno(this, adversario);
            if (--et.turnosRestantes <= 0) it.remove();
        }
    }

    // ===== utilitário que os heróis já usam
    public void addDef(int delta) { this.def = Math.max(0, this.def + delta); }

    // ===== Regras básicas
    public boolean vivo() { return pv > 0; }

    public void receberDano(int danoBruto) {
        int dano = Math.max(1, danoBruto - this.def);
        this.pv = Math.max(0, this.pv - dano);
    }

    public void curar(int valor) { this.pv = Math.min(pvMax, pv + Math.max(0, valor)); }
    public void recuperarMana(int valor) { this.pm = Math.min(pmMax, pm + Math.max(0, valor)); }
    public void gastarMana(int custo) { this.pm = Math.max(0, pm - Math.max(0, custo)); }

    public void ganharXp(int valor) {
        xp += Math.max(0, valor);
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

    public abstract String[] intro();

    // getters
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

    // setters simples
    public void setPv(int pv) { this.pv = Math.max(0, Math.min(pv, pvMax)); }
    public void setPm(int pm) { this.pm = Math.max(0, Math.min(pm, pmMax)); }
    public void setAtk(int atk) { this.atk = Math.max(0, atk); }
    public void setDef(int def) { this.def = Math.max(0, def); }
    public void setNivel(int nivel) { this.nivel = Math.max(1, nivel); }
    public void setXp(int xp) { this.xp = Math.max(0, xp); }

    @Override
    public String toString() {
        return String.format(
                "%s — %s | Nível %d | PV %d/%d | PM %d/%d | ATK %d | DEF %d | XP %d/%d",
                nome, classe, nivel, pv, pvMax, pm, pmMax, atk, def, xp, xpMax
        );
    }

    public abstract void usarHabilidade(Personagem alvo);

    // congelado (se tua lógica usa)
    protected boolean congelado = false;
    public boolean isCongelado() { return congelado; }
    public void setCongelado(boolean congelado) { this.congelado = congelado; }
}

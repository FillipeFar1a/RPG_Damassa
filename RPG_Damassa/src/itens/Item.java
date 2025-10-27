package itens;

import java.io.Serializable;
import java.util.Objects;

public class Item implements Comparable<Item>, Cloneable, Serializable {

    public enum Efeito {
        CURA, MANA, ATAQUE, DEFESA, BUFF_GERAL
    }

    private String nome;
    private String descricao;
    private Efeito efeito;
    private int quantidade;

    public Item() { }

    public Item(String nome, String descricao, Efeito efeito, int quantidade) {
        this.nome = nome;
        this.descricao = descricao;
        this.efeito = efeito;
        this.quantidade = Math.max(0, quantidade);
    }

    // Construtor de cópia
    public Item(Item other) {
        this(other.nome, other.descricao, other.efeito, other.quantidade);
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public Efeito getEfeito() { return efeito; }
    public int getQuantidade() { return quantidade; }

    public void setQuantidade(int q) { this.quantidade = Math.max(0, q); }
    public void incrementar(int q) { this.quantidade = Math.max(0, this.quantidade + q); }

    public boolean decrementarUm() {
        if (quantidade > 0) { quantidade--; return true; }
        return false;
    }

    // Itens iguais: mesmo nome (case-insensitive) + mesmo efeito
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item i)) return false;
        return efeito == i.efeito &&
                nome != null && i.nome != null &&
                nome.equalsIgnoreCase(i.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(efeito, nome == null ? null : nome.toLowerCase());
    }

    // Ordena por nome e depois por efeito
    @Override
    public int compareTo(Item o) {
        int byNome = String.CASE_INSENSITIVE_ORDER.compare(
                this.nome == null ? "" : this.nome,
                o.nome == null ? "" : o.nome
        );
        if (byNome != 0) return byNome;
        return this.efeito.compareTo(o.efeito);
    }

    @Override
    public Item clone() {
        try { return (Item) super.clone(); }
        catch (CloneNotSupportedException e) { return new Item(this); }
    }

    @Override
    public String toString() {
        return String.format("%s x%d (%s) — %s", nome, quantidade, efeito, descricao);
    }
}

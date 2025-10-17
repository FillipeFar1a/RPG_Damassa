package itens;

import java.util.Objects;

public class Item implements Comparable<Item>, Cloneable {
    private final String nome;
    private final String descricao;
    private final String efeito; // "cura", "atk+", etc.
    private int quantidade;

    public Item(String nome, String descricao, String efeito, int quantidade) {
        this.nome = nome;
        this.descricao = descricao;
        this.efeito = efeito;
        this.quantidade = Math.max(0, quantidade);
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getEfeito() { return efeito; }
    public int getQuantidade() { return quantidade; }

    public void adicionar(int q) { this.quantidade += Math.max(0, q); }
    public boolean consumir(int q) {
        if (quantidade >= q) { quantidade -= q; return true; }
        return false;
    }

    // Mesmo item = mesmo nome + efeito (padrão didático)
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item i)) return false;
        return nome.equalsIgnoreCase(i.nome) && efeito.equalsIgnoreCase(i.efeito);
    }
    @Override public int hashCode() {
        return Objects.hash(nome.toLowerCase(), efeito.toLowerCase());
    }

    // Ordena por nome
    @Override public int compareTo(Item i) {
        return this.nome.compareToIgnoreCase(i.nome);
    }

    @Override public Item clone() {
        return new Item(nome, descricao, efeito, quantidade);
    }

    @Override public String toString() {
        return nome + " (" + efeito + ") x" + quantidade;
    }
}

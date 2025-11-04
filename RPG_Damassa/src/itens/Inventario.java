package itens;

import java.io.Serializable;
import java.util.*;

public class Inventario implements Cloneable, Serializable {

    // Chave lógica: nome lowercase + efeito
    private static final class Chave {
        final String nomeKey;
        final Item.Efeito efeito;
        Chave(String nome, Item.Efeito efeito) {
            this.nomeKey = nome == null ? "" : nome.toLowerCase();
            this.efeito = efeito;
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Chave c)) return false;
            return Objects.equals(nomeKey, c.nomeKey) && efeito == c.efeito;
        }
        @Override public int hashCode() { return Objects.hash(nomeKey, efeito); }
    }

    private final Map<Chave, Item> mapa = new HashMap<>();
    private final TreeSet<Item> ordenado = new TreeSet<>();

    public Inventario() { }

    // Construtor de cópia (deep copy)
    public Inventario(Inventario other) {
        for (Item it : other.ordenado) adicionar(it.clone());
    }

    public boolean vazio() { return mapa.isEmpty(); }

    public void limpar() {
        mapa.clear();
        ordenado.clear();
    }

    // ===== Adicionar (mescla quantidades)
    public void adicionar(Item item) {
        if (item == null || item.getQuantidade() <= 0) return;
        Chave k = new Chave(item.getNome(), item.getEfeito());
        Item existente = mapa.get(k);
        if (existente == null) {
            Item novo = item.clone();
            mapa.put(k, novo);
            ordenado.add(novo);
        } else {
            ordenado.remove(existente);
            existente.incrementar(item.getQuantidade());
            ordenado.add(existente);
        }
    }

    public void adicionar(String nome, String desc, Item.Efeito efeito, int qtd) {
        adicionar(new Item(nome, desc, efeito, qtd));
    }

    // ===== Remover/decrementar quantidade
    public boolean remover(String nome, Item.Efeito efeito, int qtd) {
        if (qtd <= 0) return false;
        Chave k = new Chave(nome, efeito);
        Item existente = mapa.get(k);
        if (existente == null) return false;

        ordenado.remove(existente);
        int novaQtd = Math.max(0, existente.getQuantidade() - qtd);
        existente.setQuantidade(novaQtd);

        if (novaQtd == 0) mapa.remove(k);
        else ordenado.add(existente);

        return true;
    }

    // ===== Usar 1 unidade
    public Item usarUm(String nome, Item.Efeito efeito) {
        Chave k = new Chave(nome, efeito);
        Item existente = mapa.get(k);
        if (existente == null || existente.getQuantidade() <= 0) return null;

        ordenado.remove(existente);
        if (!existente.decrementarUm()) return null;

        if (existente.getQuantidade() == 0) mapa.remove(k);
        else ordenado.add(existente);

        Item usado = existente.clone();
        usado.setQuantidade(1);
        return usado;
    }

    public List<Item> listarOrdenado() {
        return Collections.unmodifiableList(new ArrayList<>(ordenado));
    }

    @Override
    public Inventario clone() { return new Inventario(this); }

    @Override
    public String toString() {
        if (ordenado.isEmpty()) return "(vazio)";
        StringBuilder sb = new StringBuilder();
        for (Item it : ordenado) sb.append("- ").append(it).append("\n");
        return sb.toString();
    }
}

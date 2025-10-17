package itens;

import java.util.*;

public class Inventario implements Cloneable {
    private final Map<Item, Item> itens = new HashMap<>();

    public void adicionar(Item item) {
        Item existente = itens.get(item);
        if (existente != null) existente.adicionar(item.getQuantidade());
        else itens.put(item, item.clone());
    }

    public boolean remover(Item item, int qtd) {
        Item existente = itens.get(item);
        if (existente == null) return false;
        if (existente.consumir(qtd)) {
            if (existente.getQuantidade() == 0) itens.remove(item);
            return true;
        }
        return false;
    }

    public List<Item> listarOrdenado() {
        List<Item> lista = new ArrayList<>(itens.values());
        Collections.sort(lista); // usa compareTo por nome
        return lista;
    }

    @Override public Inventario clone() {
        Inventario inv = new Inventario();
        for (Item i : itens.values()) inv.adicionar(i.clone());
        return inv;
    }

    @Override public String toString() {
        return listarOrdenado().toString();
    }
}

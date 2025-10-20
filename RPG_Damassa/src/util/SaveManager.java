package util;

import java.io.*;
import jogo.SaveData;

public final class SaveManager {
    private SaveManager() {}

    public static boolean salvar(String path, SaveData data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static SaveData carregar(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (SaveData) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}

package work.lclpnet.mmo.data.dialog;

public class DialogTransactions {

    private static int nextId = 0;

    public static int nextId() {
        return nextId++;
    }
}

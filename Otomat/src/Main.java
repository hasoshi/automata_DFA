public class Main {
    private static Otomat<String, Integer> oto;

    public static void main(String[] args) {
        oto = new Otomat<>();
        oto.init_otomat("dfa_1.txt");
        System.out.println("DFA_Min:");
        oto.DFA_Min();
    }
}


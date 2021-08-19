import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;

public class Otomat<E extends Comparable<E>, W extends Comparable<W>> {
    private ArrayList<Transision<String, W>> arr_Trans_min;
    private ArrayList<Transision<E, W>> arr_Trans;
    private ArrayList<State<E>> arr_States, start_State, finish_States;
    private ArrayList<W> arr_Words;

    private BufferedReader br;

    public Otomat() {
        arr_Trans_min = new ArrayList<Transision<String, W>>();
        arr_Trans = new ArrayList<Transision<E, W>>();
        arr_States = new ArrayList<State<E>>();
        arr_Words = new ArrayList<W>();
        start_State = new ArrayList<>();
        finish_States = new ArrayList<>();
    }

    public void push(Transision<E, W> tran) {
        if (tran != null && arr_Trans.contains(tran) == false) {
            arr_Trans.add(tran);
            State<E> left = tran.getLeft();
            State<E> right = tran.getRight();
            W w = tran.getWord();
            if (arr_States.contains(left) == false)
                arr_States.add(left);

            if (arr_States.contains(right) == false)
                arr_States.add(right);

            if (arr_Words.contains(w) == false)
                arr_Words.add(w);

            // find Start state;
            if (left.getIs_Finish() == -1)
                if (start_State.contains(left) == false)
                    start_State.add(left);
            if (right.getIs_Finish() == -1)
                if (start_State.contains(right) == false)
                    start_State.add(right);

            // find Finish state;
            if (left.getIndex() == 0)
                if (finish_States.contains(left) == false)
                    finish_States.add(left);
            if (right.getIndex() == 0)
                if (finish_States.contains(right) == false)
                    finish_States.add(right);
        }
    }

    @SuppressWarnings("unchecked")
    public void init_otomat(String filename) {
        String line = "";

        try {
            br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null) {
                if (line.compareTo("") != 0) {
                    // lay ra Transision
                    String transision_temp[] = line.split(" ");

                    State<E> left, right;
                    Transision<E, W> tran;
                    W word;

                    left = create_State(transision_temp[0]);
                    word = (W) transision_temp[1];
                    right = create_State(transision_temp[2]);

                    tran = new Transision<E, W>(left, right, word);
                    push(tran);
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Show input otomat
        System.out.println("Start state:\t" + start_State);
        System.out.println("Finish states:\t" + finish_States);

        System.out.println("Transisions: ");
        System.out.println(arr_Trans);
    }

    @SuppressWarnings("unchecked")
    private State<E> create_State(String src) {
        State<E> state = new State<>();
        String[] state_raw = src.split("_");
        state.setValue((E) state_raw[0]);
        state.setIs_Finish(Integer.parseInt(state_raw[1]));
        state.setIndex(Integer.parseInt(state_raw[2]));
        return state;
    }

    public void DFA_Min() {
        remove_State();
        sort_State_by_Name(arr_States);

        ArrayList<State<E>> clone = new ArrayList<State<E>>();
        for (State<E> s : arr_States)
            clone.add(new State<E>(s));

        while (compare_Arr(clone, _DFA_Min()) == false) {
            clone.clear();
            for (State<E> s : arr_States)
                clone.add(new State<E>(s));
        }

        int i, j, n, m;
        n = arr_States.size();
        m = arr_Words.size();

        for (i = 0; i < n; i++) {
            for (j = 0; j < m; j++) {
                State<E> s_out = find_State(arr_States.get(i), arr_Words.get(j));
                if (s_out != null) {
                    State<String> left, right;
                    W w = arr_Words.get(j);

                    left = new State<String>("S" + arr_States.get(i).getIndex(), 0, 1);
                    right = new State<String>("S" + s_out.getIndex(), 0, 1);

                    Transision<String, W> tr = new Transision<String, W>(left, right, w);

                    if (!arr_Trans_min.contains(tr))
                        arr_Trans_min.add(tr);
                }
            }
        }
        System.out.println(arr_Trans_min);

        // Define states after minimization
        int max_index = maxIndex();
        for (int k = 0; k <= max_index; k++) {
            System.out.print("S" + k + ": ");
            for (State<E> s : arr_States)
                if (s.getIndex() == k) {
                    System.out.print(s);
                    if (start_State.contains(s))
                        System.out.print("- START ");
                    if (finish_States.contains(s))
                        System.out.print("- FINISH ; ");
                }
            System.out.println();
        }
    }

    private ArrayList<State<E>> _DFA_Min() {
        sort_State(arr_States);
        ArrayList<State<E>> clone = new ArrayList<State<E>>();

        int i = 0, n = arr_States.size();
        while (i < n) {
            int L, index, max, count_State = 0;
            State<E> s;

            L = i;
            s = arr_States.get(L);
            index = s.getIndex();
            max = maxIndex();

            while (L < n && arr_States.get(L).getIndex() == index) {
                count_State++;
                if (count_State >= 2) {
                    State<E> temp = arr_States.get(L);

                    for (W w : arr_Words) {
                        State<E> s1, s2;
                        s1 = find_State(s, w);
                        s2 = find_State(temp, w);

                        if (s1 == null || s2 == null)
                            temp.setIndex(max + 1);
                        else if (s1.getIndex() != s2.getIndex())
                            temp.setIndex(max + 1);
                        else
                            temp.setIndex(s.getIndex());
                    }
                    arr_States.set(L, temp);
                    update_tran(temp); // update Transisions after changed Index
                    // of State
                }
                L++;
            }
            i = L;
        }
        sort_State_by_Name(arr_States);
        for (State<E> s : arr_States)
            clone.add(new State<E>(s));
        return clone;
    }


    private void update_tran(State<E> temp) {
        int index = temp.getIndex();
        for (Transision<E, W> tr : arr_Trans) {
            if (tr.getLeft().equals(temp) == true)
                tr.getLeft().setIndex(index);
            if (tr.getRight().equals(temp) == true)
                tr.getRight().setIndex(index);
        }
    }

    private int maxIndex() {
        int i, max = 0, n = arr_States.size();
        for (i = 0; i < n; i++) {
            State<E> temp = arr_States.get(i);
            if (temp.getIndex() >= max)
                max = temp.getIndex();
        }
        return max;
    }

    private boolean compare_Arr(ArrayList<State<E>> a, ArrayList<State<E>> b) {
        int i, n = a.size();
        for (i = 0; i < n; i++)
            if (a.get(i).getIndex() != b.get(i).getIndex())
                return false;
        return true;
    }

    private void remove_State() {
        SamplePredicate<State<E>> filter_State;
        SamplePredicate<Transision<E, W>> filter_Trans;

        filter_State = new SamplePredicate<State<E>>();
        filter_Trans = new SamplePredicate<Transision<E, W>>();

        int i, j, n = arr_Trans.size();
        ArrayList<Transision<E, W>> temp = new ArrayList<>();

        for (i = 0; i < n; i++) {
            boolean del = true;
            for (j = 0; j < n; j++) {
                if (arr_Trans.get(i).getLeft().equals(arr_Trans.get(j).getRight()) == true)
                    del = false;
            }
            if (del == true) {
                filter_State.value = arr_Trans.get(i).getLeft();
                if (filter_State.value.getIs_Finish() != -1 && filter_State.value.getIs_Finish() != 1) {
                    temp.add(arr_Trans.get(i));

                    if (arr_States.contains(filter_State.value))
                        arr_States.removeIf(filter_State);
                }
            }
        }
        for (Transision<E, W> k : temp) {
            filter_Trans.value = k;
            arr_Trans.removeIf(filter_Trans);
        }
    }

    private void sort_State(ArrayList<State<E>> a) {
        Collections.sort(a, new State<>());
    }

    private void sort_State_by_Name(ArrayList<State<E>> a) {
        Collections.sort(a);
    }

    private State<E> find_State(State<E> s, W w) {
        int i, n = arr_Trans.size();

        for (i = 0; i < n; i++) {
            Transision<E, W> temp = arr_Trans.get(i);
            if (temp.getLeft().equals(s) == true && temp.getWord().equals(w) == true)
                return temp.getRight();
        }
        return null;
    }

    class SamplePredicate<T> implements Predicate<T> {
        T value;

        public boolean test(T o) {
            if (value.equals(o)) {
                return true;
            }
            return false;
        }
    }
}
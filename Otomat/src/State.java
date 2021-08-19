import java.util.Comparator;

public class State<E extends Comparable<E>> implements Comparable<State>, Comparator<State> {
    private E value;
    private int is_Finish;
    private int index;

    public State() {
    }

    public State(E value, int is_finish, int index) {
        this.value = value;
        this.is_Finish = is_finish;
        this.index = index;
    }

    public State(State<E> state) {
        this.value = state.value;
        this.is_Finish = state.is_Finish;
        this.index = state.index;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public int getIs_Finish() {
        return is_Finish;
    }

    public void setIs_Finish(int is_Finish) {
        this.is_Finish = is_Finish;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(State o) {
        return value.compareTo((E) o.getValue());
    }

    @Override
    public String toString() {
        return "(" + value + ")";
    }

    @Override
    public int compare(State o1, State o2) {
        // TODO Auto-generated method stub
        return o1.index - o2.index;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        result = prime * result + is_Finish;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        State other = (State) obj;
        return (value.equals(other.getValue()));
    }
}

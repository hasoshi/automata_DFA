public class Transision<E extends Comparable<E>, W extends Comparable<W>> {
    private State<E> left, right;
    private W word;

    public Transision(State<E> left, State<E> right, W word) {
        super();
        this.left = left;
        this.right = right;
        this.word = word;
    }

    public State<E> getLeft() {
        return left;
    }

    public void setLeft(State<E> left) {
        this.left = left;
    }

    public State<E> getRight() {
        return right;
    }

    public void setRight(State<E> right) {
        this.right = right;
    }

    public W getWord() {
        return word;
    }

    public void setWord(W word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "\t" + left + " - " + word + " - " + right + "\n";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
        result = prime * result + ((word == null) ? 0 : word.hashCode());
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
        @SuppressWarnings("unchecked")
        Transision<E, W> o = (Transision<E, W>) obj;
        return left.equals(o.getLeft()) && right.equals(o.getRight()) && word.equals(o.getWord());
    }
}
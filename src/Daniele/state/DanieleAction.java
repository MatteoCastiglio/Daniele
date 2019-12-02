package Daniele.state;

public class DanieleAction {

    private int rowFrom;

    public int getRowFrom() {
        return rowFrom;
    }


    public int getRowTo() {
        return rowTo;
    }


    public int getColumnFrom() {
        return columnFrom;
    }

    public void setColumnFrom(int columnFrom) {
        this.columnFrom = columnFrom;
    }

    public int getColumnTo() {
        return columnTo;
    }

    public void setColumnTo(int columnTo) {
        this.columnTo = columnTo;
    }

    private int rowTo;
    private int columnFrom;
    private int columnTo;


    public DanieleAction(int rowFrom, int columnFrom, int rowTo, int columnTo) {
        this.rowFrom = rowFrom;
        this.rowTo = rowTo;
        this.columnFrom = columnFrom;
        this.columnTo = columnTo;
    }

    public static String coord(int i, int j) {
        return "" + (char) (j + 97) + (i + 1);
    }

    public String toString() {
        return "Pawn from " + coord(rowFrom, columnFrom) + " to " + coord(rowTo, columnTo);
    }
}

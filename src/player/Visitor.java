package player;

import parser.Chord;
import parser.Measure;
import parser.Note;
import parser.Piece;
import parser.Tuplet;

/**
 * Interface for the visitor pattern.
 * Visits nodes in the AST and performs operations on them.
 */
public interface Visitor<T> {
    public T visit(Piece p);
    public T visit(Measure m);
    public T visit(Tuplet t);
    public T visit(Chord c);
    public T visit(Note n);
}

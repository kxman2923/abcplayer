package parser;

import player.Visitor;

/**
 * Represents a Node in the AST. All nodes derive from this base class.
 */
public abstract class Node {
    /**
     * Calls v.visit(this). Used for implementing the visitor pattern.
     */
    public abstract <T> T accept(Visitor<T> v);
}

package gonearewe;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ASTTree {
    ASTTree parent;
    ArrayList<ASTTree> lst;
    Token atom;
    int integerVal; // used when type is INTEGER
    private TreeNodeType type;

    public ASTTree() {
        super();
        this.parent = this; // avoid null, self-pointing indicates root node
        type = TreeNodeType.S_EXPRESSION;
    }

    public ASTTree(@NotNull Token atom) {
        super();
        if (atom.isNil()) {
            setType(TreeNodeType.S_EXPRESSION);
        } else if (atom.isInteger()) {
            integerVal = atom.integerValue();
            setType(TreeNodeType.INTEGER);
        } else {
            this.atom = atom;
            setType(TreeNodeType.ATOM);
        }
    }

    public ASTTree(ASTTree parent) {
        super();
        this.parent = parent;
        setType(TreeNodeType.S_EXPRESSION);
    }

    public String getAtomName() {
        return atom.toString();
    }

    // getPosition returns position info(row and column index) of the token at current node,
    // if exists, to help generate debug info of the interpreter.
    public String getPosition() {
        return String.format("%d:%d:", atom.getRow(), atom.getColumn());
    }

    public boolean isAtom() {
        return type == TreeNodeType.ATOM;
    }

    // equal tells if token is str literally when the tree node IS AN ATOM.
    public boolean equals(String str) {
        return atom.toString().equals(str);
    }

    public void add(ASTTree child) {
        child.parent = this;
        this.lst.add(child);
    }

    private void setType(TreeNodeType type) {
        this.type = type;
    }
}
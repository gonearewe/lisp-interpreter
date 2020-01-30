package gonearewe;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ASTree {
    ASTree parent;
    ArrayList<ASTree> lst = new ArrayList<>();
    Token atom;
    int integerVal; // used when type is INTEGER
    Closure closure;
    private TreeNodeType type;

    public ASTree() {
        super();
        this.parent = this; // NOTE: avoid null, self-pointing indicates root node
        type = TreeNodeType.S_EXPRESSION;
    }

    public ASTree(@NotNull Token atom) {
        super();
        if (atom.isNil()) {
            this.atom = atom;
            setType(TreeNodeType.S_EXPRESSION);
        } else if (atom.isInteger()) {
            this.atom = atom;
            integerVal = atom.integerValue();
            setType(TreeNodeType.INTEGER);
        } else {
            this.atom = atom;
            setType(TreeNodeType.ATOM);
        }
    }

    public ASTree(ASTree parent) {
        super();
        this.parent = parent;
        setType(TreeNodeType.S_EXPRESSION);
    }

    public ASTree(ArrayList<ASTree> lst) {
        super();
        this.parent = this;
        this.lst = lst;
        setType(TreeNodeType.S_EXPRESSION);
    }

    public ASTree(Closure closure) {
        this.closure = closure;
        setType(TreeNodeType.LAMBDA);
    }

    // Generate a new ASTree from the template ASTree(ATOM type) but owns a different token name.
    public ASTree(@NotNull ASTree template, String newTokenName) {
        // this constructor is meant for inheriting template's token position
        super();
        var validTemplate = template;
        while (validTemplate.atom == null) {
            validTemplate = validTemplate.lst.get(0);
        }
        atom = Token.positionToken(validTemplate.atom.getRow(), validTemplate.atom.getColumn());
        parent = this;
        type = TreeNodeType.ATOM;
        atom.setToken(newTokenName);
        if (atom.isInteger()) {
            type = TreeNodeType.INTEGER;
            integerVal = atom.integerValue();
        } else {
            integerVal = template.integerVal;
        }
    }

    public String getAtomName() {
        if (atom != null) {
            return atom.toString();
        } else {
            return "notATOM";
        }
    }

    // getPosition returns position info(row and column index) of the token at current node,
    // if exists, to help generate debug info of the interpreter.
    public String getPosition() {
        return String.format("%d:%d:", atom.getRow(), atom.getColumn());
    }

    public boolean isAtom() {
        return type == TreeNodeType.ATOM;
    }

    public boolean isInteger() {
        return type == TreeNodeType.INTEGER;
    }

    public boolean isSExpression() {
        return type == TreeNodeType.S_EXPRESSION;
    }

    public boolean isNil() {
        return type == TreeNodeType.S_EXPRESSION && lst.size() == 0;
    }

    public boolean isLambda() {
        return type == TreeNodeType.LAMBDA;
    }

    // hasHead tells if current node which is expected to be a S-Expression has head atom.
    public boolean hasHead() {
        return lst.size() > 0;
    }

    public ASTree getHead() {
        return lst.get(0);
    }

    public ArrayList<ASTree> getTail() {
        lst = new ArrayList<>(this.lst);
        lst.remove(0);
        return lst;
    }

    // equal tells if token is str literally when the tree node IS AN ATOM.
    public boolean equals(String str) {
        return atom.toString().equals(str);
    }

    public void add(@NotNull ASTree child) {
        child.parent = this;
        this.lst.add(child);
    }

    public void add(int index, @NotNull ASTree child) {
        child.parent = this;
        this.lst.add(index, child);
    }

    public ArrayList<String> toStringList() {
        var list = new ArrayList<String>();
        for (var e : lst) {
            list.add(e.getAtomName());
        }

        return list;
    }

    @Override
    public String toString() {
        switch (type) {
            case ATOM:
                return getAtomName();
            case INTEGER:
                return String.valueOf(integerVal);
            case S_EXPRESSION:
                if (lst.size() == 0) {
                    return "NIL";
                }
                return "(" + lst.stream().map(ASTree::getAtomName).collect(Collectors.joining(" ")) + ")";
            case LAMBDA:
                return closure.toString();
        }

        return "turn ASTTree into String: you shouldn't end up reaching here !!!";
    }

    private void setType(TreeNodeType type) {
        this.type = type;
    }
}
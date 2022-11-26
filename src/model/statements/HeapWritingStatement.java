package model.statements;

import model.exceptions.InterpreterException;
import model.exceptions.StatementExecutionException;
import model.expressions.IExpression;
import model.programState.ProgramState;
import model.values.ReferenceValue;

public class HeapWritingStatement implements IStatement {

    private final String _identifier;
    private final IExpression _expression;

    public HeapWritingStatement(String identifier, IExpression expression) {
        _identifier = identifier;
        _expression = expression;
    }

    @Override
    public String toString() {
        return "writeHeap(" + _identifier + ", " + _expression.toString() + ")";
    }

    @Override
    public ProgramState execute(ProgramState state) throws InterpreterException {
        var symbolTable = state.getSymbolTable();
        var heapTable = state.getHeapTable();

        if (!symbolTable.containsKey(_identifier))
            throw new StatementExecutionException("Identifier is not defined.");

        var val = symbolTable.get(_identifier);

        if (!(val instanceof ReferenceValue refVal))
            throw new StatementExecutionException("The given value is not a reference value.");

        if (!heapTable.containsKey(refVal.getAddress()))
            throw new StatementExecutionException("Address not valid.");

        val = _expression.evaluate(symbolTable, heapTable);

        if (!val.getType().equals(refVal.getType().getInnerType()))
            throw new StatementExecutionException("Type of expression is not the type from the given address.");

        heapTable.put(refVal.getAddress(), val);

        return state;
    }

    @Override
    public IStatement deepCopy() {
        return new HeapWritingStatement(_identifier, _expression.deepCopy());
    }
}

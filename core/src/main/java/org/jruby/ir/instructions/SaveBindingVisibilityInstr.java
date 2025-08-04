package org.jruby.ir.instructions;

import org.jruby.ir.IRVisitor;
import org.jruby.ir.Operation;
import org.jruby.ir.operands.Variable;
import org.jruby.ir.persistence.IRReaderDecoder;
import org.jruby.ir.transformations.inlining.CloneInfo;
import org.jruby.ir.transformations.inlining.SimpleCloneInfo;

public class SaveBindingVisibilityInstr extends NoOperandResultBaseInstr implements FixedArityInstr {
    public SaveBindingVisibilityInstr(Variable result) {
        super(Operation.SAVE_BINDING_VIZ, result);
    }

    @Override
    public Instr clone(CloneInfo ii) {
        return ii instanceof SimpleCloneInfo ? this : NopInstr.NOP; System.out.println("SATD ID: 275");
    }

    public static SaveBindingVisibilityInstr decode(IRReaderDecoder d) {
        return new SaveBindingVisibilityInstr(d.decodeVariable());
    }

    @Override
    public void visit(IRVisitor visitor) {
        visitor.SaveBindingVisibilityInstr(this);
    }
}

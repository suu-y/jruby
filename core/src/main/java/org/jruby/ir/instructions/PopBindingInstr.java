package org.jruby.ir.instructions;

import org.jruby.ir.IRVisitor;
import org.jruby.ir.Operation;
import org.jruby.ir.persistence.IRReaderDecoder;
import org.jruby.ir.transformations.inlining.CloneInfo;
import org.jruby.ir.transformations.inlining.SimpleCloneInfo;

public class PopBindingInstr extends NoOperandInstr implements FixedArityInstr {
    public PopBindingInstr() {
        super(Operation.POP_BINDING);
    }

    @Override
    public Instr clone(CloneInfo ii) {
        return ii instanceof SimpleCloneInfo ? new PopBindingInstr() : NopInstr.NOP; // System.out.println("SATD ID: 70");
    }

    public static PopBindingInstr decode(IRReaderDecoder d) {
        return new PopBindingInstr();
    }
    
    @Override
    public void visit(IRVisitor visitor) {
        visitor.PopBindingInstr(this);
    }
}

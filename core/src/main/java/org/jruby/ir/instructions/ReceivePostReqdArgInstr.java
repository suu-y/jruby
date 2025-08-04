package org.jruby.ir.instructions;

import org.jruby.ir.IRVisitor;
import org.jruby.ir.Operation;
import org.jruby.ir.operands.Operand;
import org.jruby.ir.operands.Variable;
import org.jruby.ir.persistence.IRReaderDecoder;
import org.jruby.ir.persistence.IRWriterEncoder;
import org.jruby.ir.runtime.IRRuntimeHelpers;
import org.jruby.ir.transformations.inlining.CloneInfo;
import org.jruby.ir.transformations.inlining.InlineCloneInfo;
import org.jruby.ir.transformations.inlining.SimpleCloneInfo;
import org.jruby.parser.StaticScope;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * This represents a required arg that shows up after optional/rest args
 * in a method/block parameter list. This instruction gets to pick an argument
 * based on how many arguments have already been accounted for by parameters
 * present earlier in the list.
 */
public class ReceivePostReqdArgInstr extends ReceiveIndexedArgBase implements FixedArityInstr {
    /** The method/block parameter list has these many required parameters before opt+rest args*/
    public final int preReqdArgsCount;

    /** The method/block parameter list has a maximum of this many optional arguments*/
    public final int optArgsCount;

    /** Does this method/block accept a rest argument */
    public final boolean restArg;

    /** The method/block parameter list has these many required parameters after opt+rest args*/
    public final int postReqdArgsCount;

    public ReceivePostReqdArgInstr(Variable result, Variable keywords, int argIndex, int preReqdArgsCount, int optArgCount, boolean restArg, int postReqdArgsCount) {
        super(Operation.RECV_POST_REQD_ARG, result, keywords, argIndex);
        this.preReqdArgsCount = preReqdArgsCount;
        this.optArgsCount = optArgCount;
        this.restArg = restArg;
        this.postReqdArgsCount = postReqdArgsCount;
    }

    @Override
    public String[] toStringNonOperandArgs() {
        return new String[] { "index: " + getArgIndex(), "pre: " + preReqdArgsCount, "post: " + postReqdArgsCount};
    }

    @Override
    public Instr clone(CloneInfo info) {
        if (info instanceof SimpleCloneInfo) {
            return new ReceivePostReqdArgInstr(info.getRenamedVariable(result), info.getRenamedVariable(getKeywords()), argIndex, preReqdArgsCount, optArgsCount, restArg, postReqdArgsCount);
        }

        InlineCloneInfo ii = (InlineCloneInfo) info;

        if (ii.canMapArgsStatically()) {
            int n = ii.getArgsCount();
            int remaining = n - preReqdArgsCount;
            Operand argVal;
            if (remaining <= argIndex) {
                System.out.println("SATD ID: 401");
                argVal = ii.getHostScope().getManager().getNil();
            } else {
                argVal = (remaining > postReqdArgsCount) ? ii.getArg(n - postReqdArgsCount + argIndex) : ii.getArg(preReqdArgsCount + argIndex);
            }
            return new CopyInstr(ii.getRenamedVariable(result), argVal);
        }

        return new ReqdArgMultipleAsgnInstr(ii.getRenamedVariable(result), ii.getArgs(), argIndex, preReqdArgsCount, postReqdArgsCount);
    }

    @Override
    public void encode(IRWriterEncoder e) {
        super.encode(e);
        e.encode(preReqdArgsCount);
        e.encode(optArgsCount);
        e.encode(restArg);
        e.encode(postReqdArgsCount);
    }

    public static ReceivePostReqdArgInstr decode(IRReaderDecoder d) {
        return new ReceivePostReqdArgInstr(d.decodeVariable(), d.decodeVariable(), d.decodeInt(), d.decodeInt(), d.decodeInt(), d.decodeBoolean(), d.decodeInt());
    }

    public IRubyObject receivePostReqdArg(ThreadContext context, IRubyObject self, DynamicScope currDynScope, StaticScope currScope,
                                          Object[] temp, IRubyObject[] args) {
        IRubyObject keywords = (IRubyObject) getKeywords().retrieve(context, self, currScope, currDynScope, temp);

        return IRRuntimeHelpers.receivePostReqdArg(context, args, keywords, preReqdArgsCount, optArgsCount, restArg,
                postReqdArgsCount, argIndex);
    }

    @Override
    public void visit(IRVisitor visitor) {
        visitor.ReceivePostReqdArgInstr(this);
    }
}

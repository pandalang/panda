package org.panda_lang.panda.core.syntax.block;

import org.panda_lang.panda.core.Particle;
import org.panda_lang.panda.core.parser.Atom;
import org.panda_lang.panda.core.parser.essential.BlockCenter;
import org.panda_lang.panda.core.parser.essential.ParameterParser;
import org.panda_lang.panda.core.parser.essential.util.BlockInitializer;
import org.panda_lang.panda.core.parser.essential.util.BlockLayout;
import org.panda_lang.panda.core.syntax.Block;
import org.panda_lang.panda.core.syntax.Essence;
import org.panda_lang.panda.lang.PBoolean;

public class IfThenBlock extends Block {

    static {
        BlockCenter.registerBlock(new BlockLayout(IfThenBlock.class, "if").initializer(new BlockInitializer() {
            @Override
            public Block initialize(Atom atom) {
                Block current = new IfThenBlock();
                current.setFactors(new ParameterParser().parse(atom, atom.getBlockInfo().getParameters()));
                return current;
            }
        }));
    }

    private Block elseThenBlock;

    public IfThenBlock() {
        super.setName("if-then::" + System.nanoTime());
    }

    @Override
    public Essence run(Particle particle) {
        PBoolean flag = factors[0].getValue().cast(PBoolean.class);
        if (flag != null && flag.isTrue()) {
            return super.run(particle);
        } else if (elseThenBlock != null) {
            return elseThenBlock.run(particle);
        }
        return null;
    }

    public void setElseThenBlock(Block block) {
        this.elseThenBlock = block;
    }

}

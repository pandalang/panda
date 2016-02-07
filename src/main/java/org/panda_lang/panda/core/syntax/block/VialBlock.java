package org.panda_lang.panda.core.syntax.block;

import org.panda_lang.panda.core.Particle;
import org.panda_lang.panda.core.parser.Atom;
import org.panda_lang.panda.core.parser.essential.BlockCenter;
import org.panda_lang.panda.core.parser.essential.util.BlockInitializer;
import org.panda_lang.panda.core.parser.essential.util.BlockLayout;
import org.panda_lang.panda.core.syntax.*;

import java.util.List;

public class VialBlock extends Block
{

    static
    {
        BlockCenter.registerBlock(new BlockLayout(VialBlock.class, false, "vial", "class").initializer(new BlockInitializer()
        {
            @Override
            public Block initialize(Atom atom)
            {
                Group group = atom.getPandaParser().getPandaBlock().getGroup();
                return new VialBlock(group, atom.getBlockInfo().getSpecifiers());
            }
        }));
    }

    private final Vial vial;

    public VialBlock(Group group, List<String> specifiers)
    {
        this.vial = new Vial(specifiers.get(0));
        this.vial.setVialBlock(this);

        if (group != null)
        {
            this.vial.group(group);
        }
        else
        {
            this.vial.group("default");
        }

        if (specifiers.size() > 2 && specifiers.get(1).equals("extends"))
        {
            vial.extension(specifiers.get(2));
        }

        super.setName(vial.getName());
    }

    public Particle initializeFields(Essence essence)
    {
        Particle particle = new Particle();
        particle.setMemory(essence.getMemory());
        for (NamedExecutable executable : getExecutables())
        {
            if (executable instanceof Field)
            {
                executable.run(particle);
            }
        }
        return particle;
    }

    @Override
    public void addExecutable(NamedExecutable executable)
    {
        if (executable instanceof Field)
        {
            vial.getFields().put(executable.getName(), (Field) executable);
        }
        else if (executable instanceof MethodBlock)
        {
            vial.method(new Method(executable));
        }
        else
        {
            System.out.println("Cannot add " + executable.getName() + " (" + executable.getClass().getSimpleName() + ") to vial (class)");
        }
    }

    public Vial getVial()
    {
        return vial;
    }

}

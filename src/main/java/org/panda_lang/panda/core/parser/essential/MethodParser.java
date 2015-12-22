package org.panda_lang.panda.core.parser.essential;

import org.panda_lang.panda.core.ElementsBucket;
import org.panda_lang.panda.core.Particle;
import org.panda_lang.panda.core.VialCenter;
import org.panda_lang.panda.core.parser.Atom;
import org.panda_lang.panda.core.parser.Parser;
import org.panda_lang.panda.core.parser.essential.assistant.MethodAssistant;
import org.panda_lang.panda.core.parser.essential.util.MethodInfo;
import org.panda_lang.panda.core.scheme.ParserScheme;
import org.panda_lang.panda.core.syntax.*;
import org.panda_lang.panda.core.syntax.Runtime;

public class MethodParser implements Parser {

    static {
        ParserScheme parserScheme = new ParserScheme(new MethodParser(), "*(*);", EssentialPriority.METHOD.getPriority());
        ElementsBucket.registerParser(parserScheme);
    }

    @Override
    public Runtime parse(Atom atom) {
        final Block parent = atom.getParent();
        final String source = atom.getSourcesDivider().getLine();
        final MethodInfo mi = MethodAssistant.getMethodIndication(atom, source);

        if (mi == null) {
            System.out.println("[MethodParser] Indication failed");
            return null;
        }

        System.out.println("Source: " + source);
        System.out.println("Info: " + mi.isStatic() + ", " + mi.isExternal() + ", " + mi.getVial() + ", " + mi.getMethodName());

        if (mi.isStatic()) {
            System.out.println("Oh, I'm static");
            final Vial vial = mi.getVial();
            Method method = new Method(mi.getMethodName(), new Executable() {
                @Override
                public Essence run(Particle particle) {
                    particle = new Particle(mi.getParameters());
                    return vial.getMethod(mi.getMethodName()).run(particle);
                }
            });
            return new Runtime(method);

        } else if (mi.isExternal()) {
            Parameter instance = mi.getInstance();
            String instanceOf = instance.getDataType();
            if (instanceOf != null) {
                System.out.println("Oh, I'm instance -> method");

                Vial vial = VialCenter.getVial(instanceOf);
                final Method method = vial.getMethod(mi.getMethodName());

                if (method != null) {
                    return new Runtime(instance, new Executable() {
                        @Override
                        public Essence run(Particle particle) {
                            return method.run(particle);
                        }
                    }, mi.getParameters());
                }
            } else {
                System.out.println("Oh no..., I don't have instance");
            }
        } else {

        }

        return null;

        //System.out.println(mi.getMethod() + " | " + mi.getClassName() + " | " + mi.getInstance() + " | " + mi.getParameters().toString());
        /*
        if (mi.isExternal()) {
            if (mi.isStatic()) {
                for (ObjectScheme os : ElementsBucket.getObjects()) {
                    if (!os.getName().equals(mi.getClassName())) {
                        continue;
                    }
                    for (MethodScheme ms : os.getMethods()) {
                        if (!ms.getName().equals(mi.getMethod())) {
                            continue;
                        }
                        return new Method(null, parent, mi.getMethod(), ms.getExecutable(), mi.getParameters());
                    }
                }
            } else {
                Parameter instance = mi.getInstance();
                String type = instance.getDataType();
                if (type != null) {
                    for (ObjectScheme os : ElementsBucket.getObjects()) {
                        if (!type.equals(os.getName())) {
                            continue;
                        }
                        for (MethodScheme ms : os.getMethods()) {
                            if (!ms.getName().equals(mi.getMethod())) {
                                continue;
                            }
                            return new Method(mi.getInstance(), parent, mi.getMethod(), ms.getExecutable(), mi.getParameters());
                        }
                    }
                }
                return new Method(mi.getInstance(), parent, mi.getMethod(), null, mi.getParameters());
            }
        } else {
            return new Method(atom.getPandaScript(), parent, mi.getMethod(), mi.getParameters());
        }
        return null;
        */
    }

}

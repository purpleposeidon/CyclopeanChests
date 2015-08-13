package io.github.purpleposeidon.oldchests.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class TransformChests implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformedName.equals("net.minecraft.block.Block")) {
            return basicClass;
        }
        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        for (MethodNode method : cn.methods) {
            if (!method.name.equals("registerBlocks")) continue;
            ListIterator<AbstractInsnNode> it = method.instructions.iterator();
            while (it.hasNext()) {
                AbstractInsnNode insn = it.next();
                final String chest_orig = "net/minecraft/block/BlockChest";
                final String chest_repl = "io/github/purpleposeidon/oldchests/ChestReplacement";
                final String ender_orig = "net/minecraft/block/BlockEnderChest";
                final String ender_repl = "io/github/purpleposeidon/oldchests/EnderChestReplacement";
                if (insn.getType() == AbstractInsnNode.TYPE_INSN) {
                    TypeInsnNode type = (TypeInsnNode) insn;
                    if (type.desc.equals(chest_orig)) {
                        type.desc = chest_repl;
                    } else if (type.desc.equals(ender_orig)) {
                        type.desc = ender_repl;
                    }
                } else if (insn.getType() == AbstractInsnNode.METHOD_INSN) {
                    MethodInsnNode call = (MethodInsnNode) insn;
                    if (call.owner.equals(chest_orig)) {
                        call.owner = chest_repl;
                    } else if (call.owner.equals(ender_orig)) {
                        call.owner = ender_repl;
                    }
                }
            }
            break;
        }

        int flags = 0; // FIXME: Troubles running with intellij? Different JVM or something?
        if (OldChestLoadingPlugin.deobfuscatedEnvironment) flags |= ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES;
        ClassWriter cw = new ClassWriter(cr, flags);
        cn.accept(cw);
        return cw.toByteArray();
    }

}

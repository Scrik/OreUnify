package org.prok.oreunify;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class OreUnifyTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String deobfName, byte[] bytes) {
        if ("add".equals(name)) {
            return transformItemStack(bytes, true);
        } else if ("net.minecraft.item.ItemStack".equals(name)) {
            return transformItemStack(bytes, false);
        }
        return bytes;
    }

    private byte[] transformItemStack(byte[] bytes, boolean obfuscated) {
        final String ITEM_SIGNATURE = obfuscated ? "adb" : "net/minecraft/item/Item";
        final String ITEM_STACK_SIGNATURE = obfuscated ? "add" : "net/minecraft/item/ItemStack";
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor classVisitor = new ClassVisitor(ASM5, classWriter) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
                if ("<init>".equals(name) && ("(L" + ITEM_SIGNATURE + ";II)V").equals(desc)
                        || "readFromNBT".equals(name)) {
                    return new MethodVisitor(api, methodVisitor) {
                        @Override
                        public void visitInsn(int opcode) {
                            if (opcode == RETURN) {
                                super.visitVarInsn(ALOAD, 0);
                                super.visitMethodInsn(INVOKESTATIC, "org/prok/oreunify/OreUnifyDictionary", "hookItemStack", "(L" + ITEM_STACK_SIGNATURE + ";)V", false);
                            }
                            super.visitInsn(opcode);
                        }
                    };
                }
                return methodVisitor;
            }
        };
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }
}

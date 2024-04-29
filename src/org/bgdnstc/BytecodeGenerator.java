package org.bgdnstc;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.io.PrintStream;

import static org.objectweb.asm.Opcodes.*;

public class BytecodeGenerator {
    private static ClassWriter cw;
    private static MethodVisitor mv;

    static void createClass(String className) {
        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V21, ACC_PUBLIC, className, null, Type.getInternalName(Object.class), null);
        mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "(" + Type.getDescriptor(String[].class) + ")V", null, null);
        mv.visitCode();
    }

    static void pushByteInt(int constant) {
        mv.visitIntInsn(BIPUSH, constant);
    }

    static void pushShort(int constant) {
        mv.visitIntInsn(SIPUSH, constant);
    }

    static void pushConstantLdc(Object constant) {
        mv.visitLdcInsn(constant);
    }

    static void storeInt(int index) {
        mv.visitVarInsn(ISTORE, index);
    }

    static void addIntegers(int firstOperand, int secondOperand) {
        pushConstantLdc(firstOperand);
        pushConstantLdc(secondOperand);
        mv.visitInsn(IADD);
    }

    static void addFloats(float firstOperand, float secondOperand) {
        mv.visitLdcInsn(firstOperand);
        mv.visitLdcInsn(secondOperand);
        mv.visitInsn(FADD);
    }

    static void printGetStatic() {
        mv.visitFieldInsn(GETSTATIC, Type.getInternalName(System.class), "out", Type.getDescriptor(PrintStream.class));
    }

    static void printInvokeVirtualInt() {
        try {
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(PrintStream.class), "println", Type.getMethodDescriptor(PrintStream.class.getMethod("println", int.class)), false);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static byte[] closeClass() {
        mv.visitInsn(RETURN);
        mv.visitEnd();
        mv.visitMaxs(-1, -1);
        cw.visitEnd();
        return cw.toByteArray();
    }
}

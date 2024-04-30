package org.bgdnstc;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

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

    static void createServerSocket(Integer socket, int index) {
        mv.visitTypeInsn(NEW, Type.getInternalName(DatagramSocket.class));
        mv.visitInsn(DUP);
        try {
            if(socket != null) {
                mv.visitIntInsn(SIPUSH, socket);
                mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(DatagramSocket.class), "<init>", Type.getConstructorDescriptor(DatagramSocket.class.getConstructor(int.class)), false);
            } else {
                mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(DatagramSocket.class), "<init>", Type.getConstructorDescriptor(DatagramSocket.class.getConstructor()), false);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        mv.visitVarInsn(ASTORE, index);
    }

    static void createClientSocket(Integer socket, int index) {
        mv.visitTypeInsn(NEW, Type.getInternalName(DatagramSocket.class));
        mv.visitInsn(DUP);
        try {
            if(socket != null) {
                mv.visitIntInsn(SIPUSH, socket);
                mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(DatagramSocket.class), "<init>", Type.getConstructorDescriptor(DatagramSocket.class.getConstructor(int.class)), false);
            } else {
                mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(DatagramSocket.class), "<init>", Type.getConstructorDescriptor(DatagramSocket.class.getConstructor()), false);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        mv.visitVarInsn(ASTORE, index);
//        mv.visitTypeInsn(NEW, Type.getInternalName(DatagramPacket.class));
//        mv.visitInsn(DUP);
//        mv.visitIntInsn(SIPUSH, 256);
//        mv.visitIntInsn(NEWARRAY, T_BYTE);
//        mv.visitIntInsn(SIPUSH, 256);
//        try {
//            mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(DatagramPacket.class), "<init>", Type.getConstructorDescriptor(DatagramPacket.class.getConstructor(byte[].class, int.class)), false);
//            mv.visitVarInsn(ASTORE, index + 1);
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
    }

    static void sendUDP() {

    }

    static void receiveUDP(int identifierIndex, int socketIndex) {
        mv.visitTypeInsn(NEW, Type.getInternalName(DatagramPacket.class));
        mv.visitInsn(DUP);
        mv.visitIntInsn(SIPUSH, 256);
        mv.visitIntInsn(NEWARRAY, T_BYTE);
        mv.visitIntInsn(SIPUSH, 256);
        try {
            mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(DatagramPacket.class), "<init>", Type.getConstructorDescriptor(DatagramPacket.class.getConstructor(byte[].class, int.class)), false);
            mv.visitVarInsn(ASTORE, identifierIndex);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        mv.visitVarInsn(ALOAD, socketIndex);
        mv.visitVarInsn(ALOAD, identifierIndex);
        try {
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(DatagramSocket.class), "receive", Type.getMethodDescriptor(DatagramSocket.class.getMethod("receive", DatagramPacket.class)), false);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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

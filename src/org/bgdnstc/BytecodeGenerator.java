package org.bgdnstc;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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

    static void storeFloat(int index) {
        mv.visitVarInsn(FSTORE, index);
    }

    static void storeString(int index) {
        mv.visitVarInsn(ASTORE, index);
    }

    static void addIntegers(int firstOperand, int secondOperand) {
        pushConstantLdc(firstOperand);
        pushConstantLdc(secondOperand);
        mv.visitInsn(IADD);
    }

    static void subtractIntegers(int firstOperand, int secondOperand) {
        pushConstantLdc(firstOperand);
        pushConstantLdc(secondOperand);
        mv.visitInsn(ISUB);
    }

    static void multiplyIntegers(int firstOperand, int secondOperand) {
        pushConstantLdc(firstOperand);
        pushConstantLdc(secondOperand);
        mv.visitInsn(IMUL);
    }

    static void divideIntegers(int firstOperand, int secondOperand) {
        pushConstantLdc(firstOperand);
        pushConstantLdc(secondOperand);
        mv.visitInsn(IDIV);
    }

    static void addFloats(float firstOperand, float secondOperand) {
        mv.visitLdcInsn(firstOperand);
        mv.visitLdcInsn(secondOperand);
        mv.visitInsn(FADD);
    }

    static void subtractFloats(float firstOperand, float secondOperand) {
        pushConstantLdc(firstOperand);
        pushConstantLdc(secondOperand);
        mv.visitInsn(FSUB);
    }

    static void multiplyFloats(float firstOperand, float secondOperand) {
        pushConstantLdc(firstOperand);
        pushConstantLdc(secondOperand);
        mv.visitInsn(FMUL);
    }

    static void divideFloats(float firstOperand, float secondOperand) {
        pushConstantLdc(firstOperand);
        pushConstantLdc(secondOperand);
        mv.visitInsn(FDIV);
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
//                mv.visitIntInsn(SIPUSH, socket);
                mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(DatagramSocket.class), "<init>", Type.getConstructorDescriptor(DatagramSocket.class.getConstructor()), false);
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

    static void sendUDP(int identifierIndex, int socketIndex, String message, int port) {
        try {
            mv.visitTypeInsn(NEW, Type.getInternalName(DatagramPacket.class));
            mv.visitInsn(DUP);
            pushConstantLdc(message);
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(String.class), "getBytes", Type.getMethodDescriptor(String.class.getMethod("getBytes")), false);
            pushConstantLdc(message);
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(String.class), "getBytes", Type.getMethodDescriptor(String.class.getMethod("getBytes")), false);
            mv.visitInsn(ARRAYLENGTH);
            pushConstantLdc("localhost");
            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(InetAddress.class), "getByName", Type.getMethodDescriptor(InetAddress.class.getMethod("getByName", String.class)), false);
            mv.visitIntInsn(SIPUSH, port);
            mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(DatagramPacket.class), "<init>", Type.getConstructorDescriptor(DatagramPacket.class.getConstructor(byte[].class, int.class, InetAddress.class, int.class)), false);
            mv.visitVarInsn(ASTORE, identifierIndex);
            mv.visitVarInsn(ALOAD, socketIndex);
            mv.visitVarInsn(ALOAD, identifierIndex);
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(DatagramSocket.class), "send", Type.getMethodDescriptor(DatagramSocket.class.getMethod("send", DatagramPacket.class)), false);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static void sendIdentifierUDP(int identifierIndex, int socketIndex, int messageIndex, int port) {
        try {
            mv.visitTypeInsn(NEW, Type.getInternalName(DatagramPacket.class));
            mv.visitInsn(DUP);
            loadReference(messageIndex);
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(String.class), "getBytes", Type.getMethodDescriptor(String.class.getMethod("getBytes")), false);
            loadReference(messageIndex);
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(String.class), "getBytes", Type.getMethodDescriptor(String.class.getMethod("getBytes")), false);
            mv.visitInsn(ARRAYLENGTH);
            pushConstantLdc("localhost");
            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(InetAddress.class), "getByName", Type.getMethodDescriptor(InetAddress.class.getMethod("getByName", String.class)), false);
            mv.visitIntInsn(SIPUSH, port);
            mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(DatagramPacket.class), "<init>", Type.getConstructorDescriptor(DatagramPacket.class.getConstructor(byte[].class, int.class, InetAddress.class, int.class)), false);
            mv.visitVarInsn(ASTORE, identifierIndex);
            mv.visitVarInsn(ALOAD, socketIndex);
            mv.visitVarInsn(ALOAD, identifierIndex);
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(DatagramSocket.class), "send", Type.getMethodDescriptor(DatagramSocket.class.getMethod("send", DatagramPacket.class)), false);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static void receiveUDP(int identifierIndex, int socketIndex) {
        try {
            mv.visitTypeInsn(NEW, Type.getInternalName(DatagramPacket.class));
            mv.visitInsn(DUP);
            mv.visitIntInsn(SIPUSH, 256);
            mv.visitIntInsn(NEWARRAY, T_BYTE);
            mv.visitIntInsn(SIPUSH, 256);
            mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(DatagramPacket.class), "<init>", Type.getConstructorDescriptor(DatagramPacket.class.getConstructor(byte[].class, int.class)), false);
            mv.visitVarInsn(ASTORE, identifierIndex);
            mv.visitVarInsn(ALOAD, socketIndex);
            mv.visitVarInsn(ALOAD, identifierIndex);
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

    static void printInvokeVirtualFloat() {
        try {
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(PrintStream.class), "println", Type.getMethodDescriptor(PrintStream.class.getMethod("println", float.class)), false);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static void printInvokeVirtualString() {
        try {
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(PrintStream.class), "println", Type.getMethodDescriptor(PrintStream.class.getMethod("println", String.class)), false);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static void loadInteger(int identifierIndex) {
        mv.visitVarInsn(ILOAD, identifierIndex);
    }

    static void loadFloat(int identifierIndex) {
        mv.visitVarInsn(FLOAD, identifierIndex);
    }

    static void loadReference(int identifierIndex) {
        mv.visitVarInsn(ALOAD, identifierIndex);
    }

    static void packetToString(int packetIndex) {
        try {
            mv.visitTypeInsn(NEW, Type.getInternalName(String.class));
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, packetIndex);
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(DatagramPacket.class), "getData", Type.getMethodDescriptor(DatagramPacket.class.getMethod("getData")), false);
            mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(String.class), "<init>", Type.getConstructorDescriptor(String.class.getConstructor(byte[].class)), false);
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(String.class), "trim", Type.getMethodDescriptor(String.class.getMethod("trim")), false);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static Label visitLabel() {
        Label label = new Label();
        mv.visitLabel(label);
        return label;
    }

    static void gotoLabel(Label label) {
        mv.visitJumpInsn(GOTO, label);
    }

    static byte[] closeClass() {
        mv.visitInsn(RETURN);
        mv.visitEnd();
        mv.visitMaxs(-1, -1);
        cw.visitEnd();
        return cw.toByteArray();
    }
}

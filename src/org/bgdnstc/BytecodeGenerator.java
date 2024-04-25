package org.bgdnstc;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class BytecodeGenerator {
    private static ClassWriter cw;

    private static void createClass(String className) {
        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V21, ACC_PUBLIC | ACC_STATIC, "main", "(" + Type.getDescriptor(String[].class) + ")V", null, null);
    }
}

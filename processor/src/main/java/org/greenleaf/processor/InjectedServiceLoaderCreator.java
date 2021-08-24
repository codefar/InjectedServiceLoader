package org.greenleaf.processor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改已有类。 该类无效，注解处理器不能修改源代码类
 * 修改类的操作，则需要与ClassReader和ClassVisitor一起使用
 */
public class InjectedServiceLoaderCreator implements Opcodes {
    public static byte[] create(final HashMap<String, Map<String, String>> serviceMap) throws Exception {
        ClassReader classReader = new ClassReader("org/greenleaf/api/InjectedServiceLoader");
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        classReader.accept(new InjectedServiceLoaderClassVisitor(classWriter, serviceMap), 0);
        return classWriter.toByteArray();
    }

    static class InjectedServiceLoaderClassVisitor extends ClassVisitor {
        final HashMap<String, Map<String, String>> serviceMap;

        public InjectedServiceLoaderClassVisitor(ClassVisitor classVisitor, HashMap<String, Map<String, String>> serviceMap) {
            super(ASM4, classVisitor);
            this.serviceMap = serviceMap;
        }

        @Override
        public FieldVisitor visitField(int i, String s, String s1, String s2, Object o) {
            if (s1.equals("I")) s1 = "J";
            return super.visitField(i, s, s1, s2, o);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("<clinit>")) {
                return new InjectedServiceLoaderMethodVisitor(methodVisitor, serviceMap);
            } else {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
        }
    }

    static class InjectedServiceLoaderMethodVisitor extends MethodVisitor {
        final HashMap<String, Map<String, String>> serviceMap;

        public InjectedServiceLoaderMethodVisitor(MethodVisitor mv, final HashMap<String, Map<String, String>> serviceMap) {
            super(ASM4, mv);
            this.serviceMap = serviceMap;
        }

        @Override
        public void visitCode() {
            super.visitCode();
            visitTypeInsn(NEW, "java/util/HashMap");
            visitInsn(DUP);
            visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
            visitFieldInsn(PUTSTATIC, "org/greenleaf/api/InjectedServiceLoader", "serviceListMap", "Ljava/util/HashMap;");

            for (Map.Entry<String, Map<String, String>> interfaceEntry : serviceMap.entrySet()) {
                String interfaceName = interfaceEntry.getKey();
                Map<String, String> interfacesMap = interfaceEntry.getValue();

                visitTypeInsn(NEW, "java/util/HashMap");
                visitInsn(DUP);
                visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
                visitVarInsn(ASTORE, 0);

                for (Map.Entry<String, String> keyServiceEntry : interfacesMap.entrySet()) {
                    String interfaceKey = keyServiceEntry.getKey();
                    String interfaceClass = keyServiceEntry.getValue();

                    visitVarInsn(ALOAD, 0);
                    visitLdcInsn(interfaceKey);
                    visitLdcInsn(interfaceClass);
                    visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
                    visitInsn(POP);
                }

                visitFieldInsn(GETSTATIC, "org/greenleaf/api/InjectedServiceLoader", "serviceListMap", "Ljava/util/HashMap;");
                visitLdcInsn(interfaceName);
                visitVarInsn(ALOAD, 0);
                visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
                visitInsn(POP);
            }
            visitInsn(RETURN);
            visitEnd();
        }
    }
}
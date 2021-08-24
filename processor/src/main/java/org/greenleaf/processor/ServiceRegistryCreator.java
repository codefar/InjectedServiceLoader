package org.greenleaf.processor;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成配置class
 */
public class ServiceRegistryCreator implements Opcodes {
    public static byte[] create(final HashMap<String, Map<String, String>> serviceMap) throws Exception {
        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor0;

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_FINAL | ACC_SUPER, "org/greenleaf/api/ServiceClassRegistry", null, "java/lang/Object", null);
        classWriter.visitSource("ServiceClassRegistry.java", null);

        {
            fieldVisitor = classWriter.visitField(ACC_PUBLIC | ACC_FINAL | ACC_STATIC, "serviceListMap", "Ljava/util/HashMap;", "Ljava/util/HashMap<Ljava/lang/String;Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;>;", null);
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(6, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "Lorg/greenleaf/api/ServiceClassRegistry;", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(7, label0);
            methodVisitor.visitTypeInsn(NEW, "java/util/HashMap");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
            methodVisitor.visitFieldInsn(PUTSTATIC, "org/greenleaf/api/ServiceClassRegistry", "serviceListMap", "Ljava/util/HashMap;");

            int startLineNumber = 10; //这行代码是第10行

            Label label;
            for (Map.Entry<String, Map<String, String>> interfaceEntry : serviceMap.entrySet()) {
                String interfaceName = interfaceEntry.getKey();
                Map<String, String> interfacesMap = interfaceEntry.getValue();
                label = new Label();
                methodVisitor.visitLabel(label);
                methodVisitor.visitLineNumber(startLineNumber, label);
                methodVisitor.visitTypeInsn(NEW, "java/util/HashMap");
                methodVisitor.visitInsn(DUP);
                methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
                methodVisitor.visitVarInsn(ASTORE, 0);
                for (Map.Entry<String, String> keyServiceEntry : interfacesMap.entrySet()) {
                    String interfaceKey = keyServiceEntry.getKey();
                    String interfaceClass = keyServiceEntry.getValue();

                    label = new Label();
                    methodVisitor.visitLabel(label);
                    methodVisitor.visitLineNumber(++startLineNumber, label);
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    methodVisitor.visitLdcInsn(interfaceKey);
                    methodVisitor.visitLdcInsn(interfaceClass);
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
                    methodVisitor.visitInsn(POP);
                    ;
                }

                label = new Label();
                methodVisitor.visitLabel(label);
                methodVisitor.visitLineNumber(++startLineNumber, label);
                methodVisitor.visitFieldInsn(GETSTATIC, "org/greenleaf/api/ServiceClassRegistry", "serviceListMap", "Ljava/util/HashMap;");
                methodVisitor.visitLdcInsn(interfaceName);
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
                methodVisitor.visitInsn(POP);
            }
            label = new Label();
            methodVisitor.visitLabel(label);
            methodVisitor.visitLineNumber(++startLineNumber, label);
            methodVisitor.visitInsn(RETURN);
//            methodVisitor.visitLocalVariable("stringStringMap", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", label2, label17, 0);
//            methodVisitor.visitMaxs(3, 1);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}
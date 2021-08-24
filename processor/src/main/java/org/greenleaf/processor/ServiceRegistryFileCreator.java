package org.greenleaf.processor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

/**
 * 生成配置源文件
 */
public class ServiceRegistryFileCreator {
    public static void create(String hostPackageName, ProcessingEnvironment processingEnv, final HashMap<String, Map<String, String>> serviceMap) throws Exception {
        BufferedWriter writer = null;
        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile("org.greenleaf.api.InjectedServiceRegistry");
            writer = new BufferedWriter(sourceFile.openWriter());

            writer.write("package " + hostPackageName + ";\n\n");
            writer.write("import java.util.HashMap;\n");
            writer.write("import java.util.Map;\n\n");
            writer.write("/** This class is generated, do not edit. */\n");
            writer.write("public final class InjectedServiceRegistry {\n");
            writer.write("    public static final HashMap<String, Map<String, String>> serviceListMap = new HashMap();\n\n");
            writer.write("    public InjectedServiceRegistry() {\n");
            writer.write("    }\n");
            writer.write("    static {\n");
            writer.write("        HashMap var0 = new HashMap();\n");

            for (Map.Entry<String, Map<String, String>> interfaceEntry : serviceMap.entrySet()) {
                String interfaceName = interfaceEntry.getKey();
                Map<String, String> interfacesMap = interfaceEntry.getValue();
                for (Map.Entry<String, String> keyServiceEntry : interfacesMap.entrySet()) {
                    String interfaceKey = keyServiceEntry.getKey();
                    String interfaceClass = keyServiceEntry.getValue();
                    writer.write("        var0.put(\"" + interfaceKey + "\"," + "\"" + interfaceClass + "\"" + ");\n");
                }
                writer.write("        serviceListMap.put(\"" + interfaceName + "\", var0);\n");
            }
            writer.write("    }\n");
            writer.write("}\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    //Silent
                }
            }
        }

    }
}
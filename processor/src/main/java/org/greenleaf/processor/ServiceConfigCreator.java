package org.greenleaf.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

public class ServiceConfigCreator {
    public static void create(ProcessingEnvironment processingEnv, final HashMap<String, Map<String, String>> serviceMap) throws Exception {
        Filer filer = processingEnv.getFiler();
        Messager messager = processingEnv.getMessager();
        for (Map.Entry<String, Map<String, String>> interfaceEntry : serviceMap.entrySet()) {
            try {
                String resourceFile = "META-INF/services/" + interfaceEntry.getKey() + "_injected";
                try {
                    FileObject existingFile = filer.getResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);
                    existingFile.delete();
                } catch (IOException error) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "IOException encountered, [" + error + "]");
                }

                Map<String, String> interfacesMap = interfaceEntry.getValue();
                if (interfacesMap == null) continue;

                FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);
                Writer writer = fileObject.openWriter();
                for (String key : interfacesMap.keySet()) {
                    writer.write(key + " : " + interfacesMap.get(key));
                    writer.write("\n");
                }
                writer.flush();
                writer.close();
            } catch (Exception error) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Exception encountered, [" + error + "]");
                return;
            }
        }
    }
}
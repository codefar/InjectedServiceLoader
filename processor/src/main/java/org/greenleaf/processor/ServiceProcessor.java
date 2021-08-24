package org.greenleaf.processor;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;

import org.greenleaf.annotation.Service;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class ServiceProcessor extends BaseAbstractProcessor {
    private static final String PREFIX_OF_LOGGER = "InjectedServiceLoader";
    private final HashMap<String, Map<String, String>> serviceMap = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(Service.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            processImpl(set, roundEnvironment);
        } catch (Exception e) {
            error(e);
        }
        return true;
    }

    private void processImpl(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (roundEnvironment.processingOver()) {
            generateServiceConfig();
            generateServiceClassRegistry();
            generateServiceSourceFileConfig();
//            generateInjectServiceConfig();
        } else {
            collectAnnotation(set, roundEnvironment);
        }
    }

    private void collectAnnotation(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> serviceSet = roundEnvironment.getElementsAnnotatedWith(Service.class);
        for (Element element : serviceSet) {
            if (element.getKind() != ElementKind.CLASS) continue;
            Service serviceAnnotation = element.getAnnotation(Service.class);
            String key = serviceAnnotation.key();
            TypeElement typeElement = MoreElements.asType(element);
            AnnotationMirror annotationMirror = MoreElements.getAnnotationMirror(element, Service.class).get();
            info(typeElement.toString());
            info(annotationMirror.toString());
            String serviceImplName = elementUtils.getBinaryName(typeElement).toString();
            String serviceInterfaceName = null;
            try {
                Class<?> serviceClass = serviceAnnotation.serviceClass();
                if (serviceClass != null || serviceClass != Void.class) {
                    serviceInterfaceName = serviceClass.getCanonicalName();
                }
            } catch (MirroredTypeException mirroredTypeException) {
                serviceInterfaceName = ProcessorTypeUtils.processMirroredTypeException(mirroredTypeException);
            }

            info(element.toString());
            info(serviceAnnotation.toString());
            info(key);

            info(serviceInterfaceName + "--" + serviceImplName);

            Map<String, String> serviceListMap = serviceMap.get(serviceInterfaceName);
            if (serviceListMap == null) {
                serviceListMap = new HashMap<>();
                serviceMap.put(serviceInterfaceName, serviceListMap);
            }
            serviceListMap.put(key, serviceImplName);
        }
    }

    private void generateServiceClassRegistry() {
        try {
            byte[] code = ServiceRegistryCreator.create(serviceMap);
            JavaFileObject javaFileObject = filer.createClassFile("org.greenleaf.api.ServiceClassRegistry");
            OutputStream outputStream = javaFileObject.openOutputStream();
            outputStream.write(code);
            outputStream.close();
            info(new String(code));
        } catch (Exception e) {
            error(e);
        }
    }

    private void generateServiceConfig() {
        try {
            ServiceConfigCreator.create(processingEnv, serviceMap);
        } catch (Exception e) {
            error(e);
        }
    }

    private void generateServiceSourceFileConfig() {
        try {
            ServiceRegistryFileCreator.create("org.greenleaf.injectedserviceloader", processingEnv, serviceMap);
        } catch (Exception e) {
            error(e);
        }
    }

    private void generateInjectServiceConfig() {
        try {
            InjectedServiceLoaderCreator.create(serviceMap);
        } catch (Exception e) {
            error(e);
        }
    }
}
package org.greenleaf.processor;

import com.google.auto.service.AutoService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import org.apache.commons.collections4.ListUtils;
import org.greenleaf.annotation.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class ServiceProcessor extends BaseAbstractProcessor {
    private final HashMap<String, List<Service>> serviceMap = new HashMap<>();
    private final Multimap<String, String> multimap = ArrayListMultimap.create();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(Service.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (roundEnvironment.processingOver()) {
            generateServiceConfig();
        } else {
            processImpl(set, roundEnvironment);
        }
        return true;
    }

    private void processImpl(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> serviceSet = roundEnvironment.getElementsAnnotatedWith(Service.class);
        serviceSet.forEach(new Consumer<Element>() {
            @Override
            public void accept(Element element) {
                Service service = element.getAnnotation(Service.class);
                Class clazz = service.serviceClass();
                String serviceInterfaceName = clazz.getCanonicalName();
                String serviceImplName = getElementOwnerClassName(element);
                logger(serviceInterfaceName + "--" + serviceImplName );
                multimap.put(serviceInterfaceName, serviceImplName);
            }
        });
    }

    private void generateServiceConfig() {
        logger(multimap.toString());
    }


}
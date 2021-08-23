package org.greenleaf.processor;

import com.google.auto.common.MoreElements;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public abstract class BaseAbstractProcessor extends AbstractProcessor {
    protected Elements elementUtils;
    protected Types typeUtils;
    protected Filer filer;
    protected Logger logger;

    @Override
    public synchronized  void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
        logger = new Logger(processingEnv.getMessager());
    }

    @Override
    public  SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    protected void loggerE(Throwable throwable) {
        logger.error(throwable);
    }

    protected void logger(String str) {
        logger.info(str);
    }


    /**
     * Get class name of special element
     */
    protected  String getElementOwnerClassName(Element element) {
        String clazzName;

        // todo: Get the Class which element's owner.
        ElementKind elementKind = element.getKind();
        if (ElementKind.FIELD == elementKind) {
            clazzName = MoreElements.asVariable(element).getEnclosingElement().toString();
            logger("[getDIClazzSafe]MoreElements.asVariable().getEnclosingElement(): " + MoreElements.asVariable(element).getEnclosingElement());
        } else if (ElementKind.METHOD == elementKind) {
            clazzName = MoreElements.asExecutable(element).getEnclosingElement().toString();
        } else {
            clazzName = typeUtils.erasure(element.asType()).toString();
        }


        return clazzName;
    }

    protected  Element getElementOwnerElement(Element element) {
        Element resultEle;

        ElementKind elementKind = element.getKind();
        if (ElementKind.FIELD == elementKind) {
            resultEle = MoreElements.asVariable(element).getEnclosingElement();
        } else if (ElementKind.METHOD == elementKind) {
            resultEle = MoreElements.asExecutable(element).getEnclosingElement();
        } else {
            resultEle = element;
        }
        return resultEle;
    }


}
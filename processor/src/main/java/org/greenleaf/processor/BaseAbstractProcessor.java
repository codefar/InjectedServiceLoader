package org.greenleaf.processor;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public abstract class BaseAbstractProcessor extends AbstractProcessor {
    protected Elements elementUtils;
    protected Types typeUtils;
    protected Filer filer;
    protected Messager messager;

    @Override
    public synchronized  void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public final void info(CharSequence info) {
        if (StringUtils.isNotEmpty(info)) {
            messager.printMessage(Diagnostic.Kind.NOTE, info);
        }
    }

    public final void error(CharSequence error) {
        if (StringUtils.isNotEmpty(error)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "An exception is encountered, [" + error + "]");
        }
    }

    public final void error(Throwable error) {
        if (null != error) {
            messager.printMessage(Diagnostic.Kind.ERROR, "An exception is encountered, [" + error.getMessage() + "]" + "\n" + formatStackTrace(error.getStackTrace()));
        }
    }

    public final void warning(CharSequence warning) {
        if (StringUtils.isNotEmpty(warning)) {
            messager.printMessage(Diagnostic.Kind.WARNING, warning);
        }
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
package org.greenleaf.processor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

/**
 * https://codingdict.com/questions/115522
 * http://www.jcodecraeer.com/a/chengxusheji/java/2015/0210/2457.html
 */
public final class ProcessorTypeUtils {
    //如果接口类还没有被编译，也就是没有Class文件，这种情况下，直接获取Class会抛出MirroredTypeException异常，
    //但是MirroredTypeException包含一个TypeMirror，它表示未被编译的类，可以通过TypeMirror可以得到类名
    public static String processMirroredTypeException(MirroredTypeException mte) {
        DeclaredType classTypeMirror = (DeclaredType)mte.getTypeMirror();
        TypeElement classTypeElement = (TypeElement)classTypeMirror.asElement();
        return classTypeElement.getQualifiedName().toString();
    }
}
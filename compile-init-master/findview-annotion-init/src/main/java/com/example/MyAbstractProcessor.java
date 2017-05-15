package com.example;

import com.google.auto.service.AutoService;

import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class MyAbstractProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private HashMap<String, HandleFindView> mProxyMap = new HashMap<String, HandleFindView>();
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> strings = new LinkedHashSet<>();
        strings.add(FindView.class.getCanonicalName());
        return strings;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE,"process...........");
        mProxyMap.clear();
        //get field and class info
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(FindView.class);
        for (Element element : elementsAnnotatedWith) {
            if (element.getKind()!= ElementKind.FIELD)
            {
                messager.printMessage(Diagnostic.Kind.NOTE,"no field...");
            }
            if (element.getModifiers().contains(Modifier.PRIVATE));
            {
                messager.printMessage(Diagnostic.Kind.NOTE,"is private....");
            }
            VariableElement variableElement= (VariableElement) element;
            TypeElement typeElement= (TypeElement) variableElement.getEnclosingElement();
            String className = typeElement.getQualifiedName().toString();
            HandleFindView handleFindView = mProxyMap.get(className);
            if (handleFindView==null)
            {
                handleFindView=new HandleFindView(elementUtils, typeElement);
                mProxyMap.put(className,handleFindView);
            }
            FindView annotation = variableElement.getAnnotation(FindView.class);
            int value = annotation.value();
            handleFindView.setInjectVariables(value, variableElement);
        }

        //ceate file
        Set<String> strings = mProxyMap.keySet();
        for (String string : strings) {
            HandleFindView handleFindView = mProxyMap.get(string);
            try {
                JavaFileObject sourceFile = filer.createSourceFile(handleFindView.getFullClassName(), handleFindView.getClassType());
                Writer writer = sourceFile.openWriter();
                writer.write(handleFindView.getJavaCode());
                writer.flush();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
                messager.printMessage(Diagnostic.Kind.NOTE, "error==="+e.getMessage());
            }
        }
        return true;
    }
}

package com.example;

import java.util.HashMap;
import java.util.Set;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by user on 2017/5/12.
 */
public class HandleFindView {

    public static final String PROXY = "ViewFindImp";

    public HashMap<Integer, VariableElement> injectVariables = new HashMap<>();

    Elements elements;
    TypeElement typeElement;
    String packageName;
    String proxyName;

    public HandleFindView(Elements elements, TypeElement typeElement) {
        this.elements = elements;
        this.typeElement = typeElement;
        PackageElement packageOf = elements.getPackageOf(typeElement);
        packageName = packageOf.getQualifiedName().toString();
        String className = getClassName(typeElement, packageName);
        proxyName = className+"$$"+PROXY;
    }

    public void setInjectVariables(Integer rid,VariableElement val){
        injectVariables.put(rid,val);
    }

    public TypeElement getClassType(){
        return typeElement;
    }

    public String getFullClassName(){
        return packageName+"."+proxyName;
    }

    public String getJavaCode(){
        StringBuilder builder=new StringBuilder();
        builder.append("package ").append(packageName).append(";\n\n");

        builder.append("public class ").append(proxyName);
        builder.append(" {\n");

        builder.append("public static void findView("+typeElement.getQualifiedName().toString()+" activity){\n\n");


        Set<Integer> integers = injectVariables.keySet();
        for (Integer id : integers) {
            VariableElement variableElement = injectVariables.get(id);
            String s = variableElement.getSimpleName().toString();
            String s1 = variableElement.asType().toString();
            builder.append("activity."+s+"= ("+s1+") activity.findViewById("+id+");//id="+id+"\n\n");
        }


        builder.append("}\n");


        builder.append("}\n");
        return builder.toString();
    }

    public static String getClassName(TypeElement type, String packageName)
    {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

}

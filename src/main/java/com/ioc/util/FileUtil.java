package com.ioc.util;

import com.ioc.support.IsNotFileException;
import com.ioc.support.IsNotXmlFileException;
import com.ioc.support.FileResolveException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtil {

    public static String getClassPath() {
        File file = new File("src\\main\\java");

        return file.getAbsolutePath();
    }

    public static String getResourcePath(){
        File file = new File("src\\main\\resources");

        return file.getAbsolutePath();
    }

    public static String[] getRootPath()
    {
        return new String[]{getClassPath(), getResourcePath()};
    }

    public static String resolvePath(String path) throws FileResolveException, FileNotFoundException {
        return resolvePath(path,false);
    }

    public static String resolvePath(String path,boolean isFileExist) throws FileResolveException, FileNotFoundException {
        String actualPath = null;

        if (path.startsWith("/"))
        {
            path = path.replace("/","\\");
            String resourcePath = getResourcePath();
            actualPath = resourcePath + "\\" + path;
            return actualPath;
        }

        if ((path.startsWith("classpath:")))
        {
            path = path.substring("classpath:".length());
            int index = path.lastIndexOf(".");
            String fileType = path.substring(index);
            path = path.replaceAll("\\.","\\\\");
            path = path.substring(0,index);
            path += fileType;
            actualPath = getClassPath() + "\\" + path;
            if (isFileExist)
            {
                File file = new File(actualPath);
                if (file.exists())
                    return actualPath;
                else
                {
                    actualPath = getResourcePath() + "\\" + path;
                    file = new File(actualPath);
                    if (file.exists())
                        return actualPath;
                    else
                        throw new FileNotFoundException(path + "No such file or directory!");
                }
            }
           return actualPath;
        }

        if (!(path.startsWith("/")) && !(path.startsWith("classpath:")))
        {
            path = "classpath:" + path;
            return resolvePath(path,isFileExist);
        }
        throw  new FileResolveException("cannot resolve this path[" + path + "],please check your path is correct");
    }

    public static String getCurrentPath(Class clazz) throws IOException {
        File file = new File(clazz.getResource("").getPath());
        return file.getPath().replace("target\\classes","src\\main\\java");
    }

    public static boolean isXmlFile(String xmlPath) throws IsNotFileException, FileNotFoundException {
        File file = new File(xmlPath);
        if (!file.exists())
        {
            throw new FileNotFoundException("No such file or directory!");
        }
        if (!file.isFile())
        {
            throw new IsNotFileException(xmlPath + " is not a file!");
        }
        if (!file.getName().endsWith(".xml"))
        {
            throw new IsNotXmlFileException(xmlPath + " is not a XML file!");
        }
        return true;
    }


}

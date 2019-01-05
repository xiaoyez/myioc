package com.ioc.beans;

import com.ioc.context.ConfigurationConst;
import com.ioc.support.XmlParserException;
import com.ioc.util.FileUtil;
import com.ioc.util.StringUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class XmlDefinitionParser {

    BeanDefinitionRegistry registry;

    public XmlDefinitionParser(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public Map<String,Object> parse(String xmlPath) throws Exception {
        if (FileUtil.isXmlFile(xmlPath))
        {
            Map<String,Object> map = new HashMap<>();
            File file = new File(xmlPath);
            SAXReader reader = new SAXReader();
            Document document = reader.read(file);
            parseDocument(document,map);
            return map;
        }
        return null;
    }

    private void parseDocument(Document document,Map<String,Object> map) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Element rootElement = document.getRootElement();
//        System.out.println(rootElement.getName());
        if (!"beans".equals(rootElement.getName()))
        {
            throw new XmlParserException("the name of the root element in the config file must be beans");
        }
        else
        {
            //解析bean标签
            List<BeanConfiguration> beanConfigurations = new ArrayList<>();
            List<Element> beanElements = rootElement.elements("bean");
            for (Element beanElement : beanElements)
            {
                BeanConfiguration beanConfiguration = parseBean(beanElement);
                beanConfigurations.add(beanConfiguration);
            }
            map.put(ConfigurationConst.BEAN_CONFIGURATION,beanConfigurations);

            //解析component-scan标签
            List<Element> componentScanElements = rootElement.elements(ConfigurationConst.COMPONENT_SCAN);
            List<String> basePackages = new ArrayList<>(componentScanElements.size());
            for (Element componentScanElement : componentScanElements) {
                String basePackage = parseComponentScan(componentScanElement);
                basePackages.add(basePackage);
            }
            map.put(ConfigurationConst.COMPONENT_SCAN,basePackages);

        }
    }

    private String parseComponentScan(Element componentScanElement) {
        String basePackage = componentScanElement.attributeValue("base-package");
        if (!StringUtil.hasText(basePackage))
            throw new XmlParserException("component-scan element must has a base-package attribute whose value must have text");
        return basePackage;
    }

    private BeanConfiguration parseBean(Element beanElement) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        BeanConfiguration beanConfiguration = new BeanConfiguration();
        String beanClassName = beanElement.attributeValue("class");
        beanConfiguration.setClassName(beanClassName);

        //获取构造器参数
        List<BeanConfiguration.ConstructorArg> constructorArgs = parseConstructor(beanElement);
        beanConfiguration.setConstructorArgs(constructorArgs);

        //解析property标签，为bean对象属性赋值
        List<BeanConfiguration.Property> propertyList = parseProperty(beanElement);
        beanConfiguration.setPropertyList(propertyList);

        //获取scope
        String scope = beanElement.attributeValue("scope");
        if (!StringUtil.hasText(scope))
            scope = "singleton";
        beanConfiguration.setScope(scope);

        //获取id
        String id = beanElement.attributeValue("id");
        if (!StringUtil.hasText(id))
            id = beanClassName;
        beanConfiguration.setId(id);

        return beanConfiguration;

    }

    private List<BeanConfiguration.ConstructorArg> parseConstructor(Element beanElement) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Element> constructorElements = beanElement.elements("constructor-arg");
        List<BeanConfiguration.ConstructorArg> constructorArgs = new ArrayList<>(constructorElements.size());
        Object[] args = new Object[constructorElements.size()];
        int i = 0;
        Object bean = null;
        for (Element constructorElement : constructorElements)
        {
            String value = constructorElement.attributeValue("value");
            BeanConfiguration.ConstructorArg arg = new BeanConfiguration.ConstructorArg();
            arg.setValue(value);
            constructorArgs.add(arg);
        }

        return constructorArgs;

    }

    private List<BeanConfiguration.Property> parseProperty(Element beanElement) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
        List<Element> propertyElements = beanElement.elements("property");
        List<BeanConfiguration.Property> propertyList = new ArrayList<>();
        for (Element propertyElement : propertyElements)
        {
            BeanConfiguration.Property property = new BeanConfiguration.Property();
            String name = propertyElement.attributeValue("name");
            property.setName(name);
            //找value属性值
            Object value = propertyElement.attributeValue("value");
            if (value == null)
            {
                //找ref属性值
                String refName = propertyElement.attributeValue("ref");
                if (StringUtil.hasText(refName))
                    property.setRef(refName);
                else
                {
                    //找list
                    Element listElement = propertyElement.element("list");

                    if (listElement != null)
                    {
                        BeanConfiguration.ValueList list = parseList(listElement);
                        value = list;
                    }
                    else
                    {
                        //找map
                        Element mapElement = propertyElement.element("map");
                        if (mapElement != null)
                        {
                            Map<BeanConfiguration.Key,BeanConfiguration.Value> map = parseMap(mapElement);
                            property.setMap(map);
                        }
                    }
                }
            }
            propertyList.add(property);
        }
        return propertyList;
    }

    private Map<BeanConfiguration.Key,BeanConfiguration.Value> parseMap(Element mapElement) {
        Map<BeanConfiguration.Key,BeanConfiguration.Value> map = new HashMap();
        List<Element> entryElements = mapElement.elements("entry");

        for (Element entryElement : entryElements)
        {
            //获取key
            BeanConfiguration.Key key = new BeanConfiguration.Key();
            Element keyElement = entryElement.element("key");
            String v = keyElement.attributeValue("value");
            if (StringUtil.hasText(v))
            {
                key.setValue(v);
            }
            else
            {
                String ref = keyElement.attributeValue("ref");
                if (StringUtil.hasText(ref))
                    key.setRef(ref);
                else
                {
                    v = keyElement.getText();
                    if (StringUtil.hasText(v))
                        key.setValue(v);
                    else
                        throw new XmlParserException("key element must have a value attribute or a ref attribute or a text child element");
                }

            }

            //获取value
            Element valueElement = entryElement.element("value");
            BeanConfiguration.Value value;
            if (valueElement != null)
            {
                value = parseValue(valueElement);
            }
            else
            {
                Element refElement = entryElement.element("ref");
                String ref = refElement.attributeValue("bean");
                value = new BeanConfiguration.Value(null,ref);
            }


            map.put(key,value);
        }
        return map;
    }

    private BeanConfiguration.Value parseValue(Element valueElement)
    {

        BeanConfiguration.Value value = new BeanConfiguration.Value();
        String v = valueElement.attributeValue("value");
        if (v != null)
            value.setValue(v);
        else
        {
            String ref = valueElement.attributeValue("ref");
            if (StringUtil.hasText(ref))
                value.setValue(ref);
            else
            {
                v = valueElement.getText();
                if (StringUtil.hasText(v))
                    value.setValue(v);
                else
                    throw new XmlParserException("value element must have a value attribute or a ref attribute or a text child element");
            }
        }
        return value;
    }

    private BeanConfiguration.ValueList parseList(Element listElement) {
        List<Element> elements = listElement.elements();
        BeanConfiguration.ValueList list = new BeanConfiguration.ValueList();
        for (Element element : elements)
        {
            if(element.getName().equals("value"))
            {
                BeanConfiguration.Value value = parseValue(element);

                list.addValue(value);
            }
        }
        return list;
    }

}

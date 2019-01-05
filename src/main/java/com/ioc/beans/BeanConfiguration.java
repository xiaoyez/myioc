package com.ioc.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BeanConfiguration {
    private String id;

    private String className;

    private List<ConstructorArg> constructorArgs;

    private List<Property> propertyList;



    private String scope;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<ConstructorArg> getConstructorArgs() {
        return constructorArgs;
    }

    public void setConstructorArgs(List<ConstructorArg> constructorArgs) {
        this.constructorArgs = constructorArgs;
    }

    public List<Property> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<Property> propertyList) {
        this.propertyList = propertyList;
    }



    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public static class ConstructorArg extends Value{

        public ConstructorArg() {
        }

        public ConstructorArg(String value) {
            super(value);
        }

        public ConstructorArg(String value, String ref) {
            super(value, ref);
        }
    }

    public static class Property extends Value{
        private String name;
        private ValueList list = null;
        private Map<Key,Value> map = null;

        public Property() {
        }

        public Property(String value, String name) {
            super(value);
            this.name = name;
        }

        public Property(String value, String ref, String name) {
            super(value, ref);
            this.name = name;
        }

        public Property(ValueList list)
        {
            super();
            this.list = list;
        }

        public ValueList getList() {
            return list;
        }

        public void setList(ValueList list) {
            this.list = list;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<Key, Value> getMap() {
            return map;
        }

        public void setMap(Map<Key, Value> map) {
            this.map = map;
        }

        public void put(Key key, Value value)
        {
            map.put(key,value);
        }

        public Value get(Key key)
        {
            return map.get(key);
        }
    }

    public static class ValueList{
        private List<Value> values;

        public ValueList()
        {
            values = new ArrayList<>();
        }

        public ValueList(List<Value> values) {
            this.values = values;
        }

        public void addValue(Value value)
        {
            values.add(value);
        }

        public List<Value> getValues() {
            return values;
        }

        public void setValues(List<Value> values) {
            this.values = values;
        }
    }

    public static class Value{
        protected String value = null;
        protected String ref = null;

        public Value() {}

        public Value(String value) {
            this.value = value;
        }

        public Value(String value, String ref) {
            this.value = value;
            this.ref = ref;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }
    }

    public static class Key extends Value{

        public Key() {
        }

        public Key(String value) {
            super(value);
        }

        public Key(String value, String ref) {
            super(value, ref);
        }
    }
}

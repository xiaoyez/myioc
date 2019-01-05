package test.entity;

import com.ioc.context.annotation.Autowired;
import com.ioc.context.annotation.Component;

import java.util.List;
import java.util.Map;


public class School {

    private List<User> teachers;

    private Map<String, User> teacherMap;



    public Map<String, User> getTeacherMap() {
        return teacherMap;
    }

    public void setTeacherMap(Map<String, User> teacherMap) {
        this.teacherMap = teacherMap;
    }

    public School() {}

    public School(List<User> teachers) {
        this.teachers = teachers;
    }



    public List<User> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<User> teachers) {
        this.teachers = teachers;
    }

    @Override
    public String toString() {
        return "School{" +
                "teachers=" + teachers +
                ", teacherMap=" + teacherMap +
                '}';
    }
}

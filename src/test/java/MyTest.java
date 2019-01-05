import com.ioc.context.AnnotationConfigurationApplicationContext;
import com.ioc.context.ApplicationContext;
import com.ioc.context.ClasspathXmlApplicationContext;
import com.ioc.context.annotation.Scope;
import com.ioc.support.FileResolveException;
import com.ioc.util.AnnotationUtil;
import com.ioc.util.ClassUtil;
import org.junit.Test;
import test.config.MyConfiguration;
import test.entity.Lost;
import test.entity.School;
import test.entity.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class MyTest {

    @Test
    public void test() throws FileResolveException, FileNotFoundException {
        ApplicationContext context = new ClasspathXmlApplicationContext("beans.xml");
        User bean = context.getBean("user",User.class);
        User bean2 = context.getBean("user", User.class);
        System.out.println(bean);
        System.out.println(bean == bean2);
        School school = context.getBean(School.class);
        System.out.println(school);

        Lost lost1 = context.getBean("test.entity.Lost",Lost.class);
        System.out.println(lost1);
    }

    @Test
    public void test2() throws NoSuchMethodException {
        Class clazz = School.class;
        Method setTeachers = clazz.getMethod("setTeachers", List.class);
        Type[] genericParameterTypes = setTeachers.getGenericParameterTypes();
        ParameterizedType parameterizedType = (ParameterizedType)genericParameterTypes[0];
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        System.out.println(actualTypeArguments[0]);
    }

    @Test
    public void testAnnotationConfigurationApplicationContext()
    {
        ApplicationContext context = new AnnotationConfigurationApplicationContext(MyConfiguration.class);
        User user = context.getBean("user", User.class);
        System.out.println(user.toString());

        Lost lost1 = context.getBean("test.entity.Lost",Lost.class);
        System.out.println(lost1);
    }

    @Test
    public void testAnnotationUtil()
    {
        boolean isComponent = AnnotationUtil.isComponent(MyConfiguration.class);
        System.out.println(isComponent);
    }

    @Test
    public void testSearchClass() throws IOException, ClassNotFoundException {
        List<Class> classList = ClassUtil.searchClass("org.junit");
        for (Class clazz : classList) {
            System.out.println(clazz.getName());
        }
    }


}

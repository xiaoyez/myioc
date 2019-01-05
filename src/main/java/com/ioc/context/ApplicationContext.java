package com.ioc.context;

import com.ioc.beans.BeanFactory;

public interface ApplicationContext extends BeanFactory {

    void init();

}

package org.Authentication.app;

import org.onlab.rest.AbstractWebApplication;
import java.util.Set;

public class WebApplication extends AbstractWebApplication{
    @Override
    public Set<Class<?>> getClasses(){
        return getClasses(WebResource.class);
    }
}
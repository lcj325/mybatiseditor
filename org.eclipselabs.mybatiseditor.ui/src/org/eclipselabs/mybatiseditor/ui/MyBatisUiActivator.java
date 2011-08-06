package org.eclipselabs.mybatiseditor.ui;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class MyBatisUiActivator extends AbstractUIPlugin {
    
    private static MyBatisUiActivator instance;
    
    private static synchronized void setInstance(MyBatisUiActivator instance) {
        MyBatisUiActivator.instance = instance;
    }
    
    public static synchronized MyBatisUiActivator getInstance() {
        return instance;
    }
    
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        setInstance(instance);
    }
    
    @Override
    public void stop(BundleContext context) throws Exception {
        setInstance(null);
        super.stop(context);
    }
}

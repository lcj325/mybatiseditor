package org.eclipselabs.mybatiseditor.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class MyBatisEditorUiActivator extends AbstractUIPlugin {

    private static MyBatisEditorUiActivator instance;

    private static synchronized void setInstance(MyBatisEditorUiActivator instance) {
        MyBatisEditorUiActivator.instance = instance;
    }

    public static synchronized MyBatisEditorUiActivator getInstance() {
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

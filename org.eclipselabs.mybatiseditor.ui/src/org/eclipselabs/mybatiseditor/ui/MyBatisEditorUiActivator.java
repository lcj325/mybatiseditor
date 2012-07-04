package org.eclipselabs.mybatiseditor.ui;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class MyBatisEditorUiActivator extends AbstractUIPlugin {

    private static final String ICON = "MyBatisEditorUI.ICON";

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
        setInstance(this);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        setInstance(null);
        super.stop(context);
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        ImageDescriptor iconDesc = ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path("icons/mybatislogo.png"), null));
        reg.put(ICON, iconDesc);
    }

    public Image getLogo() {
        Image image = getImageRegistry().get(ICON);
        return image;
    }
}

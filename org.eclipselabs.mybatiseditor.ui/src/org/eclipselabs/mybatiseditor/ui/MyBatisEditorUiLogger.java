package org.eclipselabs.mybatiseditor.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public final class MyBatisEditorUiLogger {

    private MyBatisEditorUiLogger() {
        // No instance needed
    }

    public static void error(String message, Throwable error) {
        MyBatisEditorUiActivator instance = MyBatisEditorUiActivator.getInstance();
        instance.getLog().log(new Status(IStatus.ERROR, instance.getBundle().getSymbolicName(), message, error));
    }
}

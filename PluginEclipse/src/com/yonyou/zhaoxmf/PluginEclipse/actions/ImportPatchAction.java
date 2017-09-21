package com.yonyou.zhaoxmf.PluginEclipse.actions;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.yonyou.zhaoxmf.PluginEclipse.Activator;
import com.yonyou.zhaoxmf.PluginEclipse.Shell.ImportPatchWizard;

public class ImportPatchAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	ImportPatchWizard wizard;
	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		wizard=new ImportPatchWizard(window);
		WizardDialog dialog=new WizardDialog(window.getShell(), wizard){
			@Override
			protected void configureShell(Shell newShell) {
				// TODO Auto-generated method stub
				super.configureShell(newShell);
				URL imageURL = Platform.getBundle(Activator.PLUGIN_ID).getEntry("icons/importpatch_small.png"); 
				Image image = ImageDescriptor.createFromURL(imageURL).createImage();
				newShell.setImage(image);
			}
		};
		dialog.open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if(wizard!=null){
			wizard.setSelection(selection);
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		this.window=window;
	}

	

}

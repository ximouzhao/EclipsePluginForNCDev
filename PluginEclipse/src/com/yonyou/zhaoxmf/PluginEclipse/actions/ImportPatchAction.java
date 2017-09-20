package com.yonyou.zhaoxmf.PluginEclipse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.yonyou.zhaoxmf.PluginEclipse.Shell.ImportPatchWizard;

public class ImportPatchAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		ImportPatchWizard wizard=new ImportPatchWizard(window);
		WizardDialog dialog=new WizardDialog(window.getShell(), wizard);
		dialog.open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
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

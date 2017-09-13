package com.yonyou.zhaoxmf.PluginEclipse.Shell;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;


public class ImportPatchWizard extends Wizard {

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		MessageDialog.openInformation(getShell(), "performFinish", "performFinish");
		return true;
	}

	@Override
	public void addPages() {
		// TODO Auto-generated method stub
		ImportPatchWizardPage1 page1=new ImportPatchWizardPage1();
		ImportPatchWizardPage2 page2=new ImportPatchWizardPage2();
		ImportPatchWizardPage3 page3=new ImportPatchWizardPage3();
		addPage(page1);
		addPage(page2);
		addPage(page3);
		super.addPages();
	}

	

}

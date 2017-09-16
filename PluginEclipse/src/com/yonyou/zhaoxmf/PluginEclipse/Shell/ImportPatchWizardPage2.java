package com.yonyou.zhaoxmf.PluginEclipse.Shell;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;


public class ImportPatchWizardPage2 extends WizardPage {

	public ImportPatchWizardPage2() {
		super("选取导入的补丁路径");
		setTitle("选取导入的补丁路径");
		setDescription("请选取导入的补丁路径");/*
		setImageDescriptor(ImageKeys.
				getImageDescriptor(ImageKeys.IMG_WIZARD_NEW));*/
	}

	@Override
	public void createControl(Composite parent) {
		 Composite container = new Composite(parent, SWT.NULL);
	      container.setLayout(new FormLayout());
	      setControl(container);
	}

	@Override
	public IWizardPage getNextPage() {
		// TODO Auto-generated method stub
		return super.getNextPage();
	}

	@Override
	public IWizardPage getPreviousPage() {
		// TODO Auto-generated method stub
		return super.getPreviousPage();
	}


}

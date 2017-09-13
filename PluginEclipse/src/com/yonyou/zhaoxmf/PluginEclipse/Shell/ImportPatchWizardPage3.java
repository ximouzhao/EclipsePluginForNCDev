package com.yonyou.zhaoxmf.PluginEclipse.Shell;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;


public class ImportPatchWizardPage3 extends WizardPage {

	public ImportPatchWizardPage3() {
		super("选择文件夹后缀");
		setTitle("选择文件夹后缀");
		setDescription("文件夹后缀用于和public，private，client拼接，如public_dev,private_dev,client_dev");/*
		setImageDescriptor(ImageKeys.
				getImageDescriptor(ImageKeys.IMG_WIZARD_NEW));*/
	}

	@Override
	public void createControl(Composite parent) {
		setControl(parent);
	}


}

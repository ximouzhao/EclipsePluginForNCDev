package com.yonyou.zhaoxmf.PluginEclipse.Shell;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;


public class ImportPatchWizardPage3 extends WizardPage {

	public ImportPatchWizardPage3() {
		super("ѡ���ļ��к�׺");
		setTitle("ѡ���ļ��к�׺");
		setDescription("�ļ��к�׺���ں�public��private��clientƴ�ӣ���public_dev,private_dev,client_dev");/*
		setImageDescriptor(ImageKeys.
				getImageDescriptor(ImageKeys.IMG_WIZARD_NEW));*/
	}

	@Override
	public void createControl(Composite parent) {
		 Composite container = new Composite(parent, SWT.NULL);
	      container.setLayout(new FormLayout());
	      setControl(container);
	}


}

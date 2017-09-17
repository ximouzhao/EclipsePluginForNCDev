package com.yonyou.zhaoxmf.PluginEclipse.Shell;


import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class ImportPatchWizardPage2 extends WizardPage {

	String fillPath="";
	public ImportPatchWizardPage2() {
		super("选取导入的补丁路径");
		setTitle("选取导入的补丁路径");
		setDescription("请选取导入的补丁路径");/*
		setImageDescriptor(ImageKeys.
				getImageDescriptor(ImageKeys.IMG_WIZARD_NEW));*/
	}

	@Override
	public void createControl(Composite parent) {
		 Composite container = new Composite(parent, SWT.PUSH);
		 final GridLayout gridLayout=new GridLayout();
		 gridLayout.numColumns=3;
	     container.setLayout(gridLayout);
		 container.setBounds(10, 10, 600, 600);
	     final Label label=new Label(container, SWT.NORMAL);
	     label.setText("模块路径");
	     label.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
	     final Text text=new Text(container, SWT.BORDER);
	     text.setText("");
	     text.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));
	     final Button button =new Button(container, SWT.PUSH);
	     button.setText("Browse");

	     final DirectoryDialog dirDialog=new DirectoryDialog(getShell());
	     dirDialog.setText("请选择模块路径");
	     button.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
			     fillPath=dirDialog.open();
			     text.setText(fillPath);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			     fillPath=dirDialog.open();
			     text.setText(fillPath);
			}
		});
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

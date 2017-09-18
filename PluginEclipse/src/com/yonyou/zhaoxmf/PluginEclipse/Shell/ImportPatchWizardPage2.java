package com.yonyou.zhaoxmf.PluginEclipse.Shell;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.print.attribute.standard.Fidelity;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

	private String filePath="";
	public String getfilePath() {
		return filePath;
	}
	private Text tipText=null;
	private Button openButton=null;
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
		 gridLayout.numColumns=4;
	     container.setLayout(gridLayout);
		 container.setBounds(10, 10, 600, 600);
	     final Label label=new Label(container, SWT.NORMAL);
	     label.setText("模块路径");
	     label.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false,1,1));
	     final Text text=new Text(container, SWT.BORDER);
	     text.setText("");
	     text.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));
	     text.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				filePath=text.getText();
				validateFilePath(filePath);
			}
		});
	     openButton =new Button(container, SWT.PUSH);
	     openButton.setText("open");
	     openButton.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
	     openButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					java.awt.Desktop.getDesktop().open(new File(filePath));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				try {
					java.awt.Desktop.getDesktop().open(new File(filePath));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	     final Button button =new Button(container, SWT.PUSH);
	     button.setText("Browse");
	     button.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
	     tipText=new Text(container, SWT.NORMAL);
	     tipText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,4,1));
	     tipText.setEnabled(false);
	     final DirectoryDialog dirDialog=new DirectoryDialog(getShell());
	     dirDialog.setText("请选择模块路径");
	     button.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				dirDialog.setFilterPath(filePath);
			     String str=dirDialog.open();
			     if(str!=null){
				     text.setText(str);
				     validateFilePath(str);
			     }
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				dirDialog.setFilterPath(filePath);
			     String str=dirDialog.open();
			     if(str!=null){
				     text.setText(str);
				     validateFilePath(str);
			     }
			}
		});
	     setControl(container);
	}
	private void validateFilePath(String path){
		if(path==null||path.equals(""))return;
		File file=new File(path);
		if(!file.exists()){
			tipText.setText("文件或文件夹不存在");
			//openButton.setEnabled(false);
		}else if(!file.isDirectory()){
			tipText.setText("请选择一个文件夹，当前选择项是文件");
		}else if(!file.canRead()){
			tipText.setText("无法读取该文件夹");
		}else if(file.list().length==0){
			tipText.setText("空文件夹");
		}else if(Arrays.asList(file.list()).contains("modules")){
			tipText.setText("检测到此路径为nchome根路径,请选择对应的模块");
		}else if(Arrays.asList(file.list()).contains("client")||Arrays.asList(file.list()).contains("lib")||Arrays.asList(file.list()).contains("META-INF")){
			tipText.setText("路径正确");
		}else {
			tipText.setText("该文件夹不是合法的模块路径");
		}
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

package com.yonyou.zhaoxmf.PluginEclipse.Shell;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.print.attribute.standard.Fidelity;

import org.eclipse.jdt.internal.ui.wizards.buildpaths.SetFilterWizardPage;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.yonyou.zhaoxmf.PluginEclipse.Activator;


public class ImportPatchWizardPage2 extends WizardPage {

	public static final String IS_IMPORT_FOLER="IS_IMPORT_FOLER";
	public static final String IMPORT_DIR="IMPORT_DIR";
	public static final String SOURCE_FOLDER_POSTFIX="SOURCE_FOLDER_POSTFIX";
	public static String postfix="";
	
	IPreferenceStore preferenceStore;
	private String filePath="";
	
	Dialog dirDialog;
	private Text tipText=null;
	private Button openButton=null;
	public ImportPatchWizardPage2() {
		super("选取导入的补丁路径");
		setTitle("选取导入的补丁路径");
		setDescription("请选取导入的补丁路径");
		preferenceStore=Activator.getDefault().getPreferenceStore();/*
		setImageDescriptor(ImageKeys.
				getImageDescriptor(ImageKeys.IMG_WIZARD_NEW));*/
	}
	public String getfilePath() {
		return filePath;
	}
	public static String getPostfix() {
		return postfix;
	}

	@Override
	public void createControl(Composite parent) {
		
		 Composite container = new Composite(parent, SWT.PUSH);
		 final GridLayout gridLayout=new GridLayout();
		 gridLayout.numColumns=8;
	     container.setLayout(gridLayout);
		 container.setBounds(10, 10, 600, 600);
		 Group group=new Group(container, SWT.NONE);
		 group.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,8,1));
		 group.setLayout(new GridLayout());
		 final Button radioImportFoler=new Button(group, SWT.RADIO);
		 radioImportFoler.setText("选取模块目录");
		 
		 radioImportFoler.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(radioImportFoler.getSelection()){
					  dirDialog=new DirectoryDialog(getShell());
					  dirDialog.setText("请选择模块路径");
					  preferenceStore.setValue(IS_IMPORT_FOLER, true);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if(radioImportFoler.getSelection()){
					  dirDialog=new DirectoryDialog(getShell());
					  dirDialog.setText("请选择模块路径");
					  preferenceStore.setValue(IS_IMPORT_FOLER, true);
				}
			}
		});
		 final Button radioImportPatch=new Button(group, SWT.RADIO);
		 radioImportPatch.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(radioImportPatch.getSelection()){
					  dirDialog=new FileDialog(getShell());
					  ((FileDialog)dirDialog).setFilterExtensions(new String[]{"*.zip"});
					  dirDialog.setText("请选择补丁文件");
					  preferenceStore.setValue(IS_IMPORT_FOLER, false);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if(radioImportPatch.getSelection()){
					  dirDialog=new FileDialog(getShell());
					  ((FileDialog)dirDialog).setFilterExtensions(new String[]{"*.zip"});
					  dirDialog.setText("请选择补丁文件");
					  preferenceStore.setValue(IS_IMPORT_FOLER, false);
				}
			}
		});
		 radioImportPatch.setText("选取补丁文件");
		 if(preferenceStore.getBoolean(IS_IMPORT_FOLER)){
			 dirDialog=new DirectoryDialog(getShell());
			 ((DirectoryDialog)dirDialog).setFilterPath(filePath);
			 radioImportFoler.setSelection(true);
		 }else{
			 dirDialog=new FileDialog(getShell());
			 ((FileDialog)dirDialog).setFilterPath(filePath);
			 ((FileDialog)dirDialog).setFilterExtensions(new String[]{"*.zip"});
			 radioImportPatch.setSelection(true);
		 }
	     final Label label=new Label(container, SWT.NORMAL);
	     label.setText("模块路径");
	     label.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false,2,1));
	     final Text text=new Text(container, SWT.BORDER);
	     text.setText(preferenceStore.getString(IMPORT_DIR));
	     filePath=preferenceStore.getString(IMPORT_DIR);
	     text.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,3,1));
	     text.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				filePath=text.getText();
				 preferenceStore.setValue(IMPORT_DIR, filePath);
				validateFilePath(filePath);
			}
		});
	     openButton =new Button(container, SWT.PUSH);
	     openButton.setText("打开...");
	     openButton.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
	     openButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					File tempfFile=new File(filePath);
					if(radioImportFoler.getSelection()&&tempfFile.isFile()){
						tempfFile=tempfFile.getParentFile();
					}
					java.awt.Desktop.getDesktop().open(tempfFile);
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
	     button.setText("选择...");
	     button.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,2,1));
	     tipText=new Text(container, SWT.NORMAL);
	     tipText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,8,1));
	     tipText.setEnabled(false);
	     
	     final Label postfixLabel=new Label(container, SWT.NORMAL);
	     postfixLabel.setText("资源文件夹后缀(非必输项),如public_");
	     postfixLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false,4,1));
	     
	     final Text postfixText=new Text(container, SWT.BORDER);
	     postfixText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,1,1));
	     postfixText.setEnabled(true);
	     postfixText.setText(preferenceStore.getString(SOURCE_FOLDER_POSTFIX));
	     postfixText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				preferenceStore.setValue(SOURCE_FOLDER_POSTFIX,postfixText.getText().trim());
				postfix=postfixText.getText().trim();
			}
		});
	     button.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(radioImportFoler.getSelection()){
					File tempFile=new File(filePath);
					if(tempFile.isFile()){
						
						((DirectoryDialog)dirDialog).setFilterPath(tempFile.getParent());
					}else{
						((DirectoryDialog)dirDialog).setFilterPath(filePath);
					}
				     String str=((DirectoryDialog)dirDialog).open();
				     if(str!=null){
				    	
					     text.setText(str);
					     validateFilePath(str);
				     }
				}else{
					File tempFile=new File(filePath);
					if(tempFile.isFile()){
						((FileDialog)dirDialog).setFilterPath(tempFile.getParent());
					}else{
						((FileDialog)dirDialog).setFilterPath(filePath);
					}
				     String str=((FileDialog)dirDialog).open();
				     if(str!=null){
					     text.setText(str);
					     validateFilePath(str);
				     }
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if(radioImportFoler.getSelection()){
					((DirectoryDialog)dirDialog).setFilterPath(filePath);
				     String str=((DirectoryDialog)dirDialog).open();
				     if(str!=null){
				    	
					     text.setText(str);
					     validateFilePath(str);
				     }
				}else{
					File tempFile=new File(filePath);
					if(tempFile.isFile()){
						((FileDialog)dirDialog).setFilterPath(filePath+"/..");
					}else{
						((FileDialog)dirDialog).setFilterPath(filePath);
					}
				     String str=((FileDialog)dirDialog).open();
				     if(str!=null){
					     text.setText(str);
					     validateFilePath(str);
				     }
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
			tipText.setText("当前选择项是文件");
		}else if(!file.canRead()){
			tipText.setText("无法读取该文件夹");
		}else if(file.list().length==0){
			tipText.setText("空文件夹");
		}else if(Arrays.asList(file.list()).contains("modules")){
			tipText.setText("检测到此路径为nchome根路径,请选择对应的模块");
		}else if(isModulesOrPatchPath(path)){
			tipText.setText("路径正确");
		}else {
			tipText.setText("该文件夹不是合法的模块路径");
		}
	}
	public static boolean isModulesOrPatchPath(String path){
		File file=new File(path+"\\classes");
		if(file.exists())return true;
		file=new File(path+"\\client\\classes");
		if(file.exists())return true;
		file=new File(path+"\\META-INF\\classes");
		if(file.exists())return true;
		return false;
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

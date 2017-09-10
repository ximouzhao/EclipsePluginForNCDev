package com.yonyou.zhaoxmf.PluginEclipse.Shell;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;



public class ClearCacheDialog extends Dialog{
	
	protected ClearCacheDialog(Shell parentShell,int style) {
		super(parentShell);
		//setShellStyle(style);
	}
	public static void showClearCacheShell(Shell shell){
		ClearCacheDialog dialog=new ClearCacheDialog(shell,SWT.SYSTEM_MODAL);
		
		dialog.open();
	}
	@Override
	protected void configureShell(Shell newShell) {
		// TODO Auto-generated method stub
		super.configureShell(newShell);
		newShell.setText("清除NC缓存");
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container =(Composite)super.createDialogArea(parent);
		final Label nameLabel = new Label(container, SWT.NONE);
		 nameLabel.setText("名字:");
		 final Label nameLabe2 = new Label(container, SWT.NONE);
		 nameLabe2.setText("名字2:");
		 final Label nameLabe3 = new Label(container, SWT.NONE);
		 nameLabe3.setText("名字3:");
		 final Label nameLabe4 = new Label(container, SWT.NONE);
		 nameLabe4.setText("名字4:");
		 final Button button =new Button(container, SWT.PUSH);
		 button.setText("清除");
		return container;
	}
	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		/*return super.createButton(parent, id, label, defaultButton);*/
		return null;
	}
	

}

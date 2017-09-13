package com.yonyou.zhaoxmf.PluginEclipse.Shell;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ClearCacheDialog extends Dialog{
	static String failMessage="";
	Text text=null;
	String NCCacheDir="";
	protected ClearCacheDialog(Shell parentShell,int style) {
		super(parentShell);
		//setShellStyle(style);
	}
	public static void showClearCacheShell(Shell shell){
		ClearCacheDialog dialog=new ClearCacheDialog(shell,SWT.SYSTEM_MODAL);
		dialog.open();
	}
	@Override
	protected Point getInitialSize() {
		return new Point(400, 300);
	}
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("清NC缓存");
		super.configureShell(newShell);
	}
	@Override
	protected int getShellStyle() {
		return super.getShellStyle()| SWT.RESIZE;
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container =(Composite)super.createDialogArea(parent);
		final GridLayout gridlayout=new GridLayout();
		gridlayout.numColumns=3;
		container.setLayout(gridlayout);
		 container.setBounds(10, 10, 600, 600);
		final Label label = new Label(container,SWT.PUSH);
		NCCacheDir = System.getProperty("user.home")+"\\NCCACHE";
		label.setText(NCCacheDir);
		label.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));
		final Button button1 =new Button(container, SWT.PUSH);
		button1.setText("打开..");
		 final Button button2 =new Button(container, SWT.PUSH);
		 button2.setText("清空");
		 text=new Text(container,SWT.BORDER|SWT.WRAP |SWT.V_SCROLL |SWT.H_SCROLL|SWT.MULTI);
		 //text.setText(container.getLayout().toString());
		 text.setEditable(false);
		 text.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,3,1));
		 button1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					//Runtime.getRuntime().exec("explorer "+NCCacheDir);
					java.awt.Desktop.getDesktop().open(new File(NCCacheDir));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				super.widgetSelected(e);
			}
			 
		});
		 button2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ClearCache();
			}
		});
		ClearCache();
		return container;
	}
	private void ClearCache(){

		File file=new File(NCCacheDir);
		failMessage="";
		if(file.list().length==0){
			text.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
			text.setText("无NC缓存文件");
		}else{
			for(String childDir:new File(NCCacheDir).list()){
				Boolean bool=deleteDir(new File(NCCacheDir,childDir));
				if(!bool){
					failMessage+="删除文件夹失败："+NCCacheDir+"\\"+childDir;
				}
			}
			if(failMessage.equals("")){
				text.setForeground(new Color(Display.getDefault(), 25,194,98));
				text.setText("已成功清除缓存");
			}else{
				text.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				text.setText(failMessage);
			}
		}
	
	}
	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		/*return super.createButton(parent, id, label, defaultButton);*/
		return null;
	}
	private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

}

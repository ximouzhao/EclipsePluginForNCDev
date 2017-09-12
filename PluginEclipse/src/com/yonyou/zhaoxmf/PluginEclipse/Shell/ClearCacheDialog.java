package com.yonyou.zhaoxmf.PluginEclipse.Shell;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

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
	protected Point getInitialSize() {
		return new Point(400, 300);
	}
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("��NC����");
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
		gridlayout.numColumns=2;
		container.setLayout(gridlayout);
		 container.setBounds(10, 10, 600, 600);
		final Label label = new Label(container,SWT.PUSH);
		final String NCCacheDir = System.getProperty("user.home")+"\\NCCACHE";
		label.setText(NCCacheDir);
		 final Button button2 =new Button(container, SWT.PUSH);
		 button2.setText("���");
		 label.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));
		 final Text text=new Text(container,SWT.BORDER|SWT.WRAP |SWT.V_SCROLL |SWT.H_SCROLL|SWT.MULTI);
		 //text.setText(container.getLayout().toString());
		 text.setEditable(false);
		 text.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,2,1));
		 button2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				File file=new File(NCCacheDir);
				if(file.list().length==0){
					text.setText("��NC�����ļ�");
				}else{
					boolean issucceed=deleteDir(new File(NCCacheDir), text);
					if(issucceed){
						new File(NCCacheDir).mkdirs();
						text.setText("�ѳɹ��������");
					}
				}
			}
		});
		
		return container;
	}
	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		/*return super.createButton(parent, id, label, defaultButton);*/
		return null;
	}
	private static boolean deleteDir(File dir,Text text) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //�ݹ�ɾ��Ŀ¼�е���Ŀ¼��
            for (int i=0; i<children.length; i++) {
            	File currDir=new File(dir, children[i]);
                boolean success = deleteDir(new File(dir, children[i]),text);
                if (!success) {
                	text.setText("ɾ��ʧ�ܣ�"+currDir.toString());
                    return false;
                }else{
                	text.setText("ɾ���ɹ���"+currDir.toString());
                }
            }
        }
        // Ŀ¼��ʱΪ�գ�����ɾ��
        return dir.delete();
    }

}

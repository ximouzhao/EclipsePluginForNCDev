package com.yonyou.zhaoxmf.PluginEclipse.actions;

import java.io.IOException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

public class ConnectToPLSQLAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	@Override
	public void run(IAction action) {
		try {
			Runtime.getRuntime().exec("\"C:\\Program Files\\PLSQL Developer\\plsqldev.exe\" userid=WLX_SHYS_201797/1@20.10.129.63:1521/orcl");
		} catch (IOException e) {
			MessageDialog.openError(window.getShell(), "ERROR", e.getStackTrace().toString());
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		this.window=window;
	}


}

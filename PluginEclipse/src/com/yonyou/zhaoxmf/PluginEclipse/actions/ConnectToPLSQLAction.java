package com.yonyou.zhaoxmf.PluginEclipse.actions;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yonyou.zhaoxmf.PluginEclipse.Activator;
import com.yonyou.zhaoxmf.PluginEclipse.preferences.PreferenceConstants;

import sysmanager.sm.util.Encode;


public class ConnectToPLSQLAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	@Override
	public void run(IAction action) {
		try {
			Encode encode = new Encode();
			
			IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(),"nc.uap.studio.common");
			String homePath=preferenceStore.getString("FIELD_NC_HOME");
			File file=new File(homePath+"\\ierp\\bin\\prop.xml");
			if(!file.canRead()){
				throw new Exception("无法读取NCHomeProp.xml文件,请检查NChome路径设置是否正确");
			}
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder builder = factory.newDocumentBuilder(); 
			Document doc = builder.parse(file); 
			NodeList nList=doc.getElementsByTagName("dataSource"); 
			if(nList.getLength()>0){
				Element node = (Element)nList.item(0);
				String databaseUrl=node.getElementsByTagName("databaseUrl").item(0).getFirstChild().getNodeValue();
				String user=node.getElementsByTagName("user").item(0).getFirstChild().getNodeValue();
				String password=node.getElementsByTagName("password").item(0).getFirstChild().getNodeValue();
				password=encode.decode(password);
				String plsqlPath=Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PATH);
				if(plsqlPath==null||!plsqlPath.contains(".exe")||!new File(plsqlPath).exists()||!new File(plsqlPath).isFile()){throw new Exception("PL/sql路径设置错误,请在首选项中重新设置");}
				//String cmd="\"C:\\Program Files\\PLSQL Developer\\plsqldev.exe\" userid="+user+"/"+password+databaseUrl.substring(databaseUrl.indexOf("@"));
				String cmd="\""+plsqlPath+"\" userid="+user+"/"+password+databaseUrl.substring(databaseUrl.indexOf("@"));
				Runtime.getRuntime().exec(cmd);
			}else{
				throw new Exception("读取数据源出错");
			}
		} catch (Exception e) {
			MessageDialog.openError(window.getShell(), "ERROR",e.getMessage());
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

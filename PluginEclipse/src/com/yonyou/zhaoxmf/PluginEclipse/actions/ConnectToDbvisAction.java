package com.yonyou.zhaoxmf.PluginEclipse.actions;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sysmanager.sm.util.Encode;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.yonyou.zhaoxmf.PluginEclipse.Activator;
import com.yonyou.zhaoxmf.PluginEclipse.preferences.PreferenceConstants;

public class ConnectToDbvisAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	@Override
	public void run(IAction action) {

		try {
			Encode encode = new Encode();
			
			IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(),"nc.uap.studio.common");
			String homePath=preferenceStore.getString("FIELD_NC_HOME");
			File file=new File(homePath+File.separator+"ierp"+File.separator+"bin"+File.separator+"prop.xml");
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
				String ip="";
				String port="";
				String sid="";
				password=encode.decode(password);
				String dbvisPath=Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.D_PATH);
				if(dbvisPath==null||!dbvisPath.contains(".exe")||!new File(dbvisPath).exists()||!new File(dbvisPath).isFile()){throw new Exception("Dbvis路径设置错误,请在首选项中重新设置");}
				//String cmd="\"C:\\Program Files\\PLSQL Developer\\plsqldev.exe\" userid="+user+"/"+password+databaseUrl.substring(databaseUrl.indexOf("@"));
				String cutDatabaseUrl=databaseUrl.substring(databaseUrl.indexOf("@"));
				if(!cutDatabaseUrl.contains("/")){
					String []tempStrArr=cutDatabaseUrl.split(":");
					ip=tempStrArr[0].substring(1);//去除@符号
					port=tempStrArr[1];
					sid=tempStrArr[2];
					//cutDatabaseUrl=tempStrArr[0]+":"+tempStrArr[1]+"/"+tempStrArr[2];
				}else{
					String []tempStrArr1=cutDatabaseUrl.split(":");
					ip=tempStrArr1[0].substring(1);;
					String []tempStrArr2=tempStrArr1[1].split("/");
					port=tempStrArr2[0];
					sid=tempStrArr2[1];
				}
				addDatabaseToDbvis(user,password,ip,port,sid);
				String cmd="\""+dbvisPath+"\"";
				Runtime.getRuntime().exec(cmd);
			}else{
				throw new Exception("读取数据源出错");
			}
		} catch (Exception e) {
			MessageDialog.openError(window.getShell(), "ERROR",e.getMessage());
			e.printStackTrace();
		}
	
	}
	public void createContent(Document doc,Element parent,String element,String content){
		Element childElement=doc.createElement(element);
		childElement.setTextContent(content);
		parent.appendChild(childElement);
	}
	public void createContentAndAttr(Document doc,Element parent,String element,String attr,String attrValue,String content){
		Element childElement=doc.createElement(element);
		childElement.setAttribute(attr, attrValue);
		childElement.setTextContent(content);
		parent.appendChild(childElement);
	}
	public void addDatabaseToDbvis(String user,String password,String ip,String port,String sid) throws Exception{
		
		password=encrypt(password, "qinda");
		String dbvisConfiguration=System.getProperty("user.home")+File.separator+".dbvis"+File.separator+"config70"+File.separator+"dbvis.xml";
		File file=new File(dbvisConfiguration);
		if(!file.canRead()){
			throw new Exception("dbvis配置文件不存在！");
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder builder = factory.newDocumentBuilder(); 
		Document doc = builder.parse(file); 
		Node nodeObjects=doc.getElementsByTagName("Objects").item(0); 
		NodeList nList=((Element)nodeObjects).getElementsByTagName("Database");
		String id="1";
		if(nList.getLength()>0){
			Element node = (Element)nList.item(nList.getLength()-1);
			String databaseID=node.getAttribute("id");
			id=Integer.parseInt(databaseID)+1+"";
		}
		
		Element childElement=doc.createElement("Database");
		childElement.setAttribute("id",id);
		nodeObjects.appendChild(childElement);
		
		Element databaseNode=doc.createElement("Database");
		databaseNode.setAttribute("id", id);
		Date date=new Date();

		createContent(doc, databaseNode, "Alias", user+"_"+date.toLocaleString());
		createContent(doc, databaseNode, "Url", "");
		createContent(doc, databaseNode, "Driver", "Oracle Thin");
		createContent(doc, databaseNode, "Userid", user);
		createContent(doc, databaseNode, "Profile", "auto");
		createContent(doc, databaseNode, "Type", "oracle");
		createContent(doc, databaseNode, "Password", password);
		createContent(doc, databaseNode, "ServerInfoFormat", "1");
		
		
		Element propertiesNode=doc.createElement("Properties");
		propertiesNode.setAttribute("key", "dbvis.ConnectionModeMigrated");
		propertiesNode.setTextContent("true");
		databaseNode.appendChild(propertiesNode);
		
		createContent(doc, databaseNode, "UrlFormat", "1");
		
		Element urlVariablesNode=doc.createElement("UrlVariables");
		Element driver=doc.createElement("Driver");
		driver.setTextContent("Oracle Thin");
		
		createContentAndAttr(doc, driver, "UrlVariable","UrlVariableName","Server", ip);
		createContentAndAttr(doc, driver, "UrlVariable","UrlVariableName","Port", port);
		createContentAndAttr(doc, driver, "UrlVariable","UrlVariableName","SID", sid);

		urlVariablesNode.appendChild(driver);
		
		databaseNode.appendChild(urlVariablesNode);
		
		Element sshSettingsNode=doc.createElement("SshSettings");
		createContent(doc, sshSettingsNode, "SshEnabled", "false");
		createContent(doc, sshSettingsNode, "SshHost", "");
		createContent(doc, sshSettingsNode, "SshPort", "22");
		createContent(doc, sshSettingsNode, "SshUserid", "");
		createContent(doc, sshSettingsNode, "SshPassword", "");
		createContent(doc, sshSettingsNode, "SshPrivateKeyFile", "");
		createContent(doc, sshSettingsNode, "SshPassphrase", "");
		
		databaseNode.appendChild(sshSettingsNode);
		
		Node nodeDatabases=doc.getElementsByTagName("Databases").item(0); 
		nodeDatabases.appendChild(databaseNode);
		
		//创建TransformerFactory对象  
        TransformerFactory tff = TransformerFactory.newInstance();  
		
		 try {  
	            //创建Transformer对象  
	            Transformer tf = tff.newTransformer();  
	            tf.setOutputProperty(OutputKeys.INDENT, "yes");  
	            tf.setOutputProperty(
	            		 "{http://xml.apache.org/xalan}indent-amount", "2");
	            //String xmlfile="C:\\Users\\zhaoxmf\\.dbvis\\test1.xml";
	            tf.transform(new DOMSource(doc),new StreamResult(new File(dbvisConfiguration)));  
	        } catch (TransformerConfigurationException e) {  
	            e.printStackTrace();  
	        } catch (TransformerException e) {  
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


    public static void main(String args[]) throws GeneralSecurityException {
        System.out.println(encrypt("password", "qinda")); //qinda是在源码中发现的key
        System.out.println(decrypt("0NPLgHCLaTs=", "qinda"));
    }

    public static String encrypt(String paramString1, String paramString2)
            throws GeneralSecurityException {
        return encrypt(paramString1, paramString2, "8859_1");
    }

    private static Cipher getCipher(String paramString, SecretKey paramSecretKey,
            AlgorithmParameterSpec paramAlgorithmParameterSpec, int paramInt)
            throws GeneralSecurityException {
        String str = "";

        Cipher localCipher = null;
        try {
            str = str + " , getting encrypt cipher";

            localCipher = Cipher.getInstance(paramString);

            str = str + " , initializing cipher";

            localCipher.init(paramInt, paramSecretKey,
                    paramAlgorithmParameterSpec);
        } catch (GeneralSecurityException localGeneralSecurityException) {
            localGeneralSecurityException.printStackTrace();
        }
        return localCipher;
    }

    private static Cipher getCipher(String paramString, int paramInt)
            throws GeneralSecurityException {
        Cipher localCipher = null;

        SecretKey localSecretKey = getSecretKey(paramString);

        byte[] arrayOfByte = { -114, 18, 57, -100, 7, 114, 111, 90 };

        PBEParameterSpec localPBEParameterSpec = new PBEParameterSpec(
                arrayOfByte, 10);

        String str = "PBEWithMD5AndDES";

        localCipher = getCipher(str, localSecretKey, localPBEParameterSpec, paramInt);

        return localCipher;
    }

    private static SecretKey getSecretKey(String paramString)
            throws GeneralSecurityException {
        SecretKey localSecretKey = null;
        try {
            PBEKeySpec localPBEKeySpec = new PBEKeySpec(
                    paramString.toCharArray());

            SecretKeyFactory localSecretKeyFactory = SecretKeyFactory
                    .getInstance("PBEWithMD5AndDES");

            localSecretKey = localSecretKeyFactory
                    .generateSecret(localPBEKeySpec);
        } catch (GeneralSecurityException localGeneralSecurityException) {

        }
        return localSecretKey;
    }

    public static String encrypt(String paramString1, String paramString2,
            String paramString3) throws GeneralSecurityException {
        Cipher localCipher = getCipher(paramString2, 1);
        try {
            byte[] arrayOfByte1 = paramString1.getBytes(paramString3);

            byte[] arrayOfByte2 = Base64.encode(
                    localCipher.doFinal(arrayOfByte1)).getBytes();

            return new String(arrayOfByte2, paramString3);

        } catch (UnsupportedEncodingException localUnsupportedEncodingException) {

        }
        return null;
    }

    public static String decrypt(String text, String key)
            throws GeneralSecurityException {
        return decrypt(text, key, "8859_1");
    }

    public static String decrypt(String encrptedText, String key, String encoding)
            throws GeneralSecurityException {
        Cipher localCipher = getCipher(key, 2);
        try {
            byte[] arrayOfByte1 = encrptedText.getBytes(encoding);

            byte[] arrayOfByte2 = Base64.decode((arrayOfByte1));

            byte[] arrayOfByte3 = localCipher.doFinal(arrayOfByte2);

            return new String(arrayOfByte3, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}

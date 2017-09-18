package com.yonyou.zhaoxmf.PluginEclipse;

import java.io.File;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SWTTest extends Shell{

	/**
	 * @param args
	 */
	private static Text text;
	private static Button button;
	public static void main(String[] args) {
		Display display=Display.getDefault();
		final Shell shell=new Shell(display);
		shell.setText("Hello SWT");
		shell.setSize(260,283);
		File file=new File("\\\\10.11.115.79\\nc\\patch_NCM_TBB_65_更新控制方案数组越界\\replacement\\modules\\tbb\\classes\\nc");
		file=new File("\\\\10.11.84.44\\lvchengfdc");
		file.canRead();
		shell.open();
		while(!shell.isDisposed()){
			if(!display.readAndDispatch())
			display.sleep();
		}
	}

}

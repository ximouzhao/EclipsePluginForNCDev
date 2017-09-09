package com.yonyou.zhaoxmf.PluginEclipse;

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
		shell.open();
		while(!shell.isDisposed()){
			if(!display.readAndDispatch())
			display.sleep();
		}
	}

}

package com.yonyou.zhaoxmf.PluginEclipse.Shell;

import java.util.ArrayList;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.ide.actions.BuildUtilities;
import org.eclipse.ui.internal.ide.dialogs.ResourceComparator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.yonyou.zhaoxmf.PluginEclipse.Activator;

public class ImportPatchWizardPage1 extends WizardPage {

	public static String IS_CLEAR_ALREADY_PATCH="IS_CLEAR_ALREADY_PATCH"; 
	
	IWorkbenchWindow window;
	IPreferenceStore preferenceStore;
	private CheckboxTableViewer projectNames;
	private Object[] selection;
	private Button button;
	private boolean isClearAlreadyPatch;
	public boolean isClearAlreadyPatch() {
		return isClearAlreadyPatch;
	}
	public Object[] getSelection() {
		return selection;
	}
	public ArrayList<IJavaProject> getJavaProject(){
		if(selection==null||selection.length==0)return null;
		ArrayList<IJavaProject> list =new ArrayList<IJavaProject>();
		for(Object o:selection){
			list.add(JavaCore.create((Project)o));
		}
		return list;
	}
	public ImportPatchWizardPage1(IWorkbenchWindow window) {
		super("选择NC项目");
		preferenceStore=Activator.getDefault().getPreferenceStore();
		setTitle("选择NC项目");
		setDescription("选择需要导入NC补丁的项目");
		this.window=window;
		/*
		setImageDescriptor(ImageKeys.
				getImageDescriptor(ImageKeys.IMG_WIZARD_NEW));*/
	}
	public boolean getButtonSelection(){
		return isClearAlreadyPatch;
	}
	@Override
	public void createControl(Composite parent) {
		 Composite container = new Composite(parent, SWT.PUSH);
		 final GridLayout gridLayout=new GridLayout();
	     container.setLayout(gridLayout);
		createProjectSelectionTable(container);
		button=new Button(container, SWT.CHECK);
		button.setSelection(preferenceStore.getBoolean(IS_CLEAR_ALREADY_PATCH));
		button.setText("清除已有补丁");
		button.setSelection(preferenceStore.getBoolean(IS_CLEAR_ALREADY_PATCH));
		isClearAlreadyPatch=preferenceStore.getBoolean(IS_CLEAR_ALREADY_PATCH);
		button.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				isClearAlreadyPatch=(button.getSelection());
				preferenceStore.setValue(IS_CLEAR_ALREADY_PATCH, button.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				isClearAlreadyPatch=(button.getSelection());
				preferenceStore.setValue(IS_CLEAR_ALREADY_PATCH, button.getSelection());
			}
		});
		setControl(container);
	}
	
	private void createProjectSelectionTable(Composite radioGroup) {
	        projectNames = CheckboxTableViewer.newCheckList(radioGroup, SWT.BORDER);
	        projectNames.setContentProvider(new WorkbenchContentProvider());
	        projectNames.setLabelProvider(new WorkbenchLabelProvider());
	        projectNames.setComparator(new ResourceComparator(ResourceComparator.NAME));
	        projectNames.addFilter(new ViewerFilter() {
	            private final IProject[] projectHolder = new IProject[1];
	            public boolean select(Viewer viewer, Object parentElement, Object element) {
	                if (!(element instanceof IProject)) {
	                    return false;
	                }
	                IProject project = (IProject) element;
	                if (!project.isAccessible()) {
	                    return false;
	                }
	                try {
						if (!project.hasNature(JavaCore.NATURE_ID)) {
							/*fCurrJProject= JavaCore.create(project);
							fEntries= fCurrJProject.getRawClasspath();
							fOutputLocation= fCurrJProject.getOutputLocation();
							fProjectStatus.setOK();*/
							return false;
						}
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

	                projectHolder[0] = project;
	                return BuildUtilities.isEnabled(projectHolder, IncrementalProjectBuilder.CLEAN_BUILD);
	            }
	        });
	        projectNames.setInput(ResourcesPlugin.getWorkspace().getRoot());
	        GridData data = new GridData(GridData.FILL_BOTH);
	        data.horizontalSpan = 2;
	        data.widthHint = IDialogConstants.ENTRY_FIELD_WIDTH;
	        data.heightHint = IDialogConstants.ENTRY_FIELD_WIDTH;
	        projectNames.getTable().setLayoutData(data);
	        selection=BuildUtilities.findSelectedProjects(window);
	        projectNames.setCheckedElements(selection);
	        //table is disabled to start because all button is selected
	      //  projectNames.getTable().setEnabled(selectedButton.getSelection());
	        projectNames.addCheckStateListener(new ICheckStateListener() {
	            public void checkStateChanged(CheckStateChangedEvent event) {
	                selection = projectNames.getCheckedElements();
	                //updateEnablement();
	            }
	        });
	    }

}

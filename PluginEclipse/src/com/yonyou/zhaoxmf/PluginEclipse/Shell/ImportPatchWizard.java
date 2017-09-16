package com.yonyou.zhaoxmf.PluginEclipse.Shell;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathsBlock;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

public class ImportPatchWizard extends Wizard {

	ImportPatchWizardPage1 page1;

	public ImportPatchWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	ImportPatchWizardPage2 page2;
	ImportPatchWizardPage3 page3;
	private IJavaProject fCurrJProject;
	private IClasspathEntry[] fEntries;
	private IPath fOutputLocation;

	private IWorkspaceRoot fWorkspaceRoot;
	private IClasspathEntry[] fNewEntries;
	private IPath fNewOutputLocation;

	private StatusInfo fProjectStatus;
	private StatusInfo fRootStatus;

	boolean fIsProjectAsSourceFolder = false;
	private IPackageFragmentRoot fCreatedRoot;

	@Override
	public boolean performFinish() {
		return doFinish();
	}

	private boolean doFinish() {
		IWorkspaceRunnable op = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException,
					OperationCanceledException {
				//◊Ó÷’÷¥––ÃÂ
				finishPage(monitor);
			}
		};
		try {
			ISchedulingRule rule = null;
			Job job = Job.getJobManager().currentJob();
			if (job != null)
				rule = job.getRule();
			IRunnableWithProgress runnable = null;
			if (rule != null)
				runnable = new WorkbenchRunnableAdapter(op, rule, true);
			else
				runnable = new WorkbenchRunnableAdapter(op, getSchedulingRule());
			getContainer().run(canRunForked(), true, runnable);
		} catch (InvocationTargetException e) {
			// handleFinishException(getShell(), e);
			return false;
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	protected boolean canRunForked() {
		return true;
	}

	protected ISchedulingRule getSchedulingRule() {
		return ResourcesPlugin.getWorkspace().getRoot(); // look all by default
	}

	private boolean finishPage(IProgressMonitor monitor) {
		try {
			if (monitor == null) {
				monitor= new NullProgressMonitor();
			}
			monitor.beginTask(NewWizardMessages.NewSourceFolderWizardPage_operation, 3);
			fProjectStatus = new StatusInfo();
			fWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			fCurrJProject = page1.getJavaProject();
			fEntries = fCurrJProject.getRawClasspath();
			fOutputLocation = fCurrJProject.getOutputLocation();
			IPath projPath = fCurrJProject.getProject().getFullPath();
			String str = "public1";
			if (str.length() == 0) {
				fRootStatus
						.setError(Messages
								.format(NewWizardMessages.NewSourceFolderWizardPage_error_EnterRootName,
										BasicElementLabels.getPathLabel(
												fCurrJProject.getPath(), false)));
			} else {
				IPath path = projPath.append(str);
				IStatus validate = fWorkspaceRoot.getWorkspace().validatePath(
						path.toString(), IResource.FOLDER);
				if (validate.matches(IStatus.ERROR)) {
					fRootStatus
							.setError(Messages
									.format(NewWizardMessages.NewSourceFolderWizardPage_error_InvalidRootName,
											validate.getMessage()));
				} else {
					IResource res = fWorkspaceRoot.findMember(path);
					if (res != null) {
						if (res.getType() != IResource.FOLDER) {
							fRootStatus
									.setError(NewWizardMessages.NewSourceFolderWizardPage_error_NotAFolder);
							return false;
						}
						if (res.isVirtual()) {
							fRootStatus
									.setError(NewWizardMessages.NewSourceFolderWizardPage_error_FolderIsVirtual);
							return false;
						}
					} else {
						if (!ResourcesPlugin
								.getWorkspace()
								.validateFiltered(
										fWorkspaceRoot.getFolder(path)).isOK()) {
							fRootStatus
									.setError(NewWizardMessages.NewSourceFolderWizardPage_error_FolderNameFiltered);
							return false;
						}
						URI projLocation = fCurrJProject.getProject()
								.getLocationURI();
						if (projLocation != null) {
							try {
								IFileStore store = EFS.getStore(projLocation)
										.getChild(str);
								if (store.fetchInfo().exists()) {
									fRootStatus
											.setError(NewWizardMessages.NewSourceFolderWizardPage_error_AlreadyExistingDifferentCase);
									return false;
								}
							} catch (CoreException e) {
								// we couldn't create the file store. Ignore the
								// exception
								// since we can't check if the file exist.
								// Pretend that it
								// doesn't.
							}
						}
					}
					ArrayList<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>(
							fEntries.length + 1);
					int projectEntryIndex = -1;

					for (int i = 0; i < fEntries.length; i++) {
						IClasspathEntry curr = fEntries[i];
						if (curr.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
							if (path.equals(curr.getPath())) {
								fRootStatus
										.setError(NewWizardMessages.NewSourceFolderWizardPage_error_AlreadyExisting);
								return false;
							}
							if (projPath.equals(curr.getPath())) {
								projectEntryIndex = i;
							}
						}
						newEntries.add(curr);
					}

					IClasspathAttribute[] attributes;
					attributes = new IClasspathAttribute[] {};
					IClasspathEntry newEntry = JavaCore.newSourceEntry(path,
							null, null, null, attributes);

					Set<IClasspathEntry> modified = new HashSet<IClasspathEntry>();

					if (projectEntryIndex != -1) {
						fIsProjectAsSourceFolder = true;
						newEntries.set(projectEntryIndex, newEntry);
					} else {
						IClasspathEntry entry = JavaCore.newSourceEntry(path,
								null, null, null, attributes);
						insertAtEndOfCategory(entry, newEntries);
					}

					fNewEntries = newEntries
							.toArray(new IClasspathEntry[newEntries.size()]);
					fNewOutputLocation = fOutputLocation;

					IJavaModelStatus status = JavaConventions
							.validateClasspath(fCurrJProject, fNewEntries,
									fNewOutputLocation);
					if (!status.isOK()) {
						if (fOutputLocation.equals(projPath)) {
							fNewOutputLocation = projPath
									.append(PreferenceConstants
											.getPreferenceStore()
											.getString(
													PreferenceConstants.SRCBIN_BINNAME));
							IStatus status2 = JavaConventions
									.validateClasspath(fCurrJProject,
											fNewEntries, fNewOutputLocation);
							if (status2.isOK()) {
								if (fIsProjectAsSourceFolder) {
									fRootStatus
											.setInfo(Messages
													.format(NewWizardMessages.NewSourceFolderWizardPage_warning_ReplaceSFandOL,
															BasicElementLabels
																	.getPathLabel(
																			fNewOutputLocation,
																			false)));
								} else {
									fRootStatus
											.setInfo(Messages
													.format(NewWizardMessages.NewSourceFolderWizardPage_warning_ReplaceOL,
															BasicElementLabels
																	.getPathLabel(
																			fNewOutputLocation,
																			false)));
								}
								return false;
							}
						}
						fRootStatus.setError(status.getMessage());
						return false;
					} else if (fIsProjectAsSourceFolder) {
						fRootStatus
								.setInfo(NewWizardMessages.NewSourceFolderWizardPage_warning_ReplaceSF);
						return false;
					}
					if (!modified.isEmpty()) {
						String info = modified.size() == 1 ? Messages
								.format(NewWizardMessages.NewSourceFolderWizardPage_warning_AddedExclusions_singular,
										(modified.iterator().next()).getPath())
								: Messages
										.format(NewWizardMessages.NewSourceFolderWizardPage_warning_AddedExclusions_plural,
												String.valueOf(modified.size()));
						fRootStatus.setInfo(info);
						return false;
					}
				}
			}
			
			try {
				projPath= fCurrJProject.getProject().getFullPath();
				if (fOutputLocation.equals(projPath) && !fNewOutputLocation.equals(projPath)) {
					if (BuildPathsBlock.hasClassfiles(fCurrJProject.getProject())) {
						if (BuildPathsBlock.getRemoveOldBinariesQuery(getShell()).doQuery(false, projPath)) {
							BuildPathsBlock.removeOldClassfiles(fCurrJProject.getProject());
						}
					}
				}


				IFolder folder= fCurrJProject.getProject().getFolder(str);
				if (!folder.exists()) {
					CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 1));
				}
				if (monitor.isCanceled()) {
					throw new InterruptedException();
				}

				fCurrJProject.setRawClasspath(fNewEntries, fNewOutputLocation, new SubProgressMonitor(monitor, 2));

				fCreatedRoot= fCurrJProject.getPackageFragmentRoot(folder);
			}catch(Exception e){
				e.printStackTrace();
			} finally {
				monitor.done();
			}

		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		fProjectStatus.setOK();
		return true;
	}

	private void insertAtEndOfCategory(IClasspathEntry entry,
			List<IClasspathEntry> entries) {
		int length = entries.size();
		IClasspathEntry[] elements = entries
				.toArray(new IClasspathEntry[length]);
		/*int i = 0;
		while (i < length && elements[i].getEntryKind() != entry.getEntryKind()) {
			i++;
		}
		if (i < length) {
			i++;
			while (i < length
					&& elements[i].getEntryKind() == entry.getEntryKind()) {
				i++;
			}
			entries.add(i, entry);
			return;
		}*/
		switch (entry.getEntryKind()) {
		case IClasspathEntry.CPE_SOURCE:
			entries.add(0, entry);
			break;
		case IClasspathEntry.CPE_CONTAINER:
		case IClasspathEntry.CPE_LIBRARY:
		case IClasspathEntry.CPE_PROJECT:
		case IClasspathEntry.CPE_VARIABLE:
		default:
			entries.add(entry);
			break;
		}
	}

	@Override
	public void addPages() {
		page1 = new ImportPatchWizardPage1();
		page2 = new ImportPatchWizardPage2();
		page3 = new ImportPatchWizardPage3();
		addPage(page1);
		addPage(page2);
		addPage(page3);
	}

}

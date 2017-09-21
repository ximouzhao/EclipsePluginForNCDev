package com.yonyou.zhaoxmf.PluginEclipse.Shell;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.swing.JProgressBar;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.DeleteResourceElementsOperation;
import org.eclipse.jdt.internal.corext.refactoring.reorg.JavaDeleteProcessor;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jdt.internal.ui.refactoring.reorg.DeleteUserInterfaceManager;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathsBlock;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.DeleteRefactoring;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.ui.internal.ide.dialogs.IDEResourceInfoUtils;
import org.eclipse.ui.wizards.datatransfer.FileStoreStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

import com.yonyou.zhaoxmf.PluginEclipse.Activator;

public class ImportPatchWizard extends Wizard {
	IWorkbenchWindow window;
	ISelection selection;
	public ISelection getSelection() {
		return selection;
	}

	public void setSelection(ISelection selection) {
		this.selection = selection;
	}

	public static final String PUBLIC_STR = "public";
	public static final String PRIVATE_STR = "private";
	public static final String CLIENT_STR = "client";

	public static final String CREATE_SOURCE_FOLDER_STR = "正在创建source folder：";
	public static final String COPY_JAVA_FILE_STR = "正在复制java文件到：";

	public static String postfix="";
	public static String filePath="";
	//加后缀后的目录名
	public static String public_str_with_postfix=PUBLIC_STR;
	public static String private_str_with_postfix=PRIVATE_STR;
	public static String client_str_with_postfix=CLIENT_STR;
	
	public ImportPatchWizard(IWorkbenchWindow window) {
		super();
		setNeedsProgressMonitor(true);
		this.window = window;
	}

	ImportPatchWizardPage1 page1;
	ImportPatchWizardPage2 page2;
	private IJavaProject fCurrJProject;
	private IClasspathEntry[] fEntries;
	private IPath fOutputLocation;

	private IWorkspaceRoot fWorkspaceRoot;
	private IClasspathEntry[] fNewEntries;
	private IPath fNewOutputLocation;

	private StatusInfo fProjectStatus;
	private StatusInfo fRootStatus;
	private MultiStatus errorStatus;
	boolean fIsProjectAsSourceFolder = false;
	
	private boolean isClearAlreadyPatch;
	@Override
	public boolean performFinish() {
		return doFinish();
	}
	
	/**
     * 解压文件
     * 
     * @param filePath
     *            压缩文件路径
	 * @throws IOException 
     */
    public static void unzip(String filePath,String toFilePath) throws IOException {
        File source = new File(filePath);
        if (source.exists()) {
            ZipInputStream zis = null;
            BufferedOutputStream bos = null;
            try {
                zis = new ZipInputStream(new FileInputStream(source));
                ZipEntry entry = null;
                while ((entry = zis.getNextEntry()) != null
                        && !entry.isDirectory()) {
                    File target =new File(toFilePath, entry.getName());
                    if (!target.getParentFile().exists()) {
                        // 创建文件父目录
                        target.getParentFile().mkdirs();
                    }
                    // 写入文件
                    bos = new BufferedOutputStream(new FileOutputStream(target));
                    int read = 0;
                    byte[] buffer = new byte[1024 * 10];
                    while ((read = zis.read(buffer, 0, buffer.length)) != -1) {
                        bos.write(buffer, 0, read);
                    }
                    bos.flush();
                }
                zis.closeEntry();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                bos.close();
                zis.close();
            }
        }
    }
    public static String getFileNameNoEx(String filename) { 
        if ((filename != null) && (filename.length() > 0)) { 
            int dot = filename.lastIndexOf('.'); 
            if ((dot >-1) && (dot < (filename.length()))) { 
                return filename.substring(0, dot); 
            } 
        } 
        return filename; 
    }
	private boolean doFinish() {

		try {
			final ArrayList<IJavaProject> list = page1.getJavaProject();
			filePath = page2.getfilePath();
			if (list == null || list.size() == 0 || filePath == null
					|| filePath.equals("")) {
				throw new Exception("请选择项目并设置导入的路径");
			};
			
			if(new File(filePath).isFile()&&filePath.endsWith(".zip")){
				String extractPatchFolder=System.getProperty("user.home")+File.separator+"ExtractPatchTemp";
				File extractPatchTempFile=new File(extractPatchFolder);
				if(!extractPatchTempFile.exists()){
					extractPatchTempFile.mkdir();
				}
				String filenameNoEx=getFileNameNoEx(new File(filePath).getName());
				unzip(filePath, extractPatchFolder+File.separator+filenameNoEx);
				String patchModulesPath=extractPatchFolder+File.separator+
						filenameNoEx+File.separator+"replacement"+File.separator+"modules";
				File patchModulesPathFile=new File(patchModulesPath);
				if(patchModulesPathFile.exists()&&patchModulesPathFile.list()!=null&&patchModulesPathFile.list().length>=1){
					patchModulesPath+=File.separator+patchModulesPathFile.list()[0];
					filePath=patchModulesPath;
				}else{
					throw new Exception("未找到该补丁的补丁文件，请检查该文件是否是补丁文件");
				}
			}
			IWorkspaceRunnable op = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					// 最终执行体
					try {
						// ResourcesPlugin.getWorkspace().isAutoBuilding();
						
						monitor.beginTask("正在导入",list.size());
						for (IJavaProject project : page1.getJavaProject()) {
							monitor.subTask("导入项目"
									+ project.getProject().getName());
							try {
								
								isClearAlreadyPatch=page1.getButtonSelection();
								postfix=page2.getPostfix();
								if(postfix!=null&&!postfix.equals("")){
									public_str_with_postfix=PUBLIC_STR+"_"+postfix;
									private_str_with_postfix=PRIVATE_STR+"_"+postfix;
									client_str_with_postfix=CLIENT_STR+"_"+postfix;
								}else{
									public_str_with_postfix=PUBLIC_STR;
									private_str_with_postfix=PRIVATE_STR;
									client_str_with_postfix=CLIENT_STR;
									
								}
								finishPage(project, new SubProgressMonitor(monitor,	1));
								
							} catch (OperationCanceledException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							monitor.worked(1);
						}
					} finally {
						monitor.done();
					}

				}
			};
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
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openInformation(getShell(), "错误", e.getMessage());
			return false;
		}
		return true;
	}

	private IFileStore[] buildFileStores(final String[] fileNames) {
		IFileStore[] stores = new IFileStore[fileNames.length];
		for (int i = 0; i < fileNames.length; i++) {
			IFileStore store = IDEResourceInfoUtils.getFileStore(fileNames[i]);
			if (store == null) {
				reportFileInfoNotFound(fileNames[i]);
				return null;
			}
			stores[i] = store;
		}
		return stores;
	}

	private void reportFileInfoNotFound(final String fileName) {

		getShell().getDisplay().syncExec(new Runnable() {
			public void run() {
				ErrorDialog.openError(
						getShell(),
						getProblemsTitle(),
						NLS.bind(
								IDEWorkbenchMessages.CopyFilesAndFoldersOperation_infoNotFound,
								fileName), null);
			}
		});
	}

	protected String getProblemsTitle() {
		return IDEWorkbenchMessages.CopyFilesAndFoldersOperation_copyFailedTitle;
	}

	protected boolean canRunForked() {
		return true;
	}

	protected ISchedulingRule getSchedulingRule() {
		return ResourcesPlugin.getWorkspace().getRoot(); // look all by default
	}

	private boolean finishPage(IJavaProject project, IProgressMonitor monitor)
			throws OperationCanceledException, CoreException,
			InterruptedException {
		//createPackage(project,monitor);
		

		monitor.beginTask("", 60);
		
		if(isClearAlreadyPatch){
			final IPath path1 = project.getPath().append(public_str_with_postfix);
			IPackageFragmentRoot ifr1=project.findPackageFragmentRoot(path1);
			if(ifr1!=null)ifr1.delete(IResource.KEEP_HISTORY, IPackageFragmentRoot.ORIGINATING_PROJECT_CLASSPATH, monitor);
			
			final IPath path2 = project.getPath().append(client_str_with_postfix);
			IPackageFragmentRoot ifr2=project.findPackageFragmentRoot(path2);
			if(ifr2!=null)ifr2.delete(IResource.KEEP_HISTORY, IPackageFragmentRoot.ORIGINATING_PROJECT_CLASSPATH, monitor);
			
			final IPath path3 = project.getPath().append(private_str_with_postfix);
			IPackageFragmentRoot ifr3=project.findPackageFragmentRoot(path3);
			if(ifr3!=null)ifr3.delete(IResource.KEEP_HISTORY, IPackageFragmentRoot.ORIGINATING_PROJECT_CLASSPATH, monitor);
		}
	
		monitor.subTask(CREATE_SOURCE_FOLDER_STR + client_str_with_postfix);
		
		 createSourceFolder(project,monitor,client_str_with_postfix);
		//Thread.sleep(5000);
		monitor.worked(10);
		monitor.subTask(CREATE_SOURCE_FOLDER_STR + public_str_with_postfix);
		 createSourceFolder(project,monitor,public_str_with_postfix);
		
		//Thread.sleep(5000);
		monitor.worked(10);
		monitor.subTask(CREATE_SOURCE_FOLDER_STR + private_str_with_postfix);
		//Thread.sleep(5000);
		 createSourceFolder(project,monitor,private_str_with_postfix);
		monitor.worked(10);
		monitor.subTask(COPY_JAVA_FILE_STR + private_str_with_postfix);
		
		// "\\\\10.11.115.79\\nc\\patch_NCM_TBB_65_更新控制方案数组越界\\replacement\\modules\\tbb\\classes\\nc"
		File file = new File(filePath + "\\classes");
		if (file.exists() && file.list().length != 0) {
			String[] fileData = convertAllChildDir(file);
			 copyFile(project,new SubProgressMonitor(monitor,
			 1),fileData,public_str_with_postfix);
		}
		//Thread.sleep(5000);
		monitor.worked(10);
		monitor.subTask(COPY_JAVA_FILE_STR + client_str_with_postfix);
		file = new File(filePath + "\\client\\classes");
		if (file.exists() && file.list().length != 0) {
			String[] fileData = convertAllChildDir(file);
			 copyFile(project,new SubProgressMonitor(monitor,
			 1),fileData,client_str_with_postfix);
		}
		//Thread.sleep(5000);
		monitor.worked(10);
		monitor.subTask(COPY_JAVA_FILE_STR + private_str_with_postfix);
		file = new File(filePath + "\\META-INF\\classes");
		if (file.exists() && file.list().length != 0) {
			String[] fileData = convertAllChildDir(file);
			 copyFile(project,new SubProgressMonitor(monitor,
			 1),fileData,private_str_with_postfix);
		}
		//Thread.sleep(5000);
		monitor.worked(10);
		return true;
	}

	private String[] convertAllChildDir(File file) {
		ArrayList<String> dirList = new ArrayList<String>();
		for (String s : file.list()) {
			dirList.add(file.getAbsolutePath() + File.separator + s);
		}
		return dirList.toArray(new String[] {});
	}

	private void copyFile(IJavaProject project, IProgressMonitor monitor,
			String[] fileData, String sourcePath) {
		fCurrJProject = project;
		IPath path = fCurrJProject.getPath().append(new Path(sourcePath));
		fWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IResource res = fWorkspaceRoot.findMember(path);
		IPackageFragmentRoot root = fCurrJProject.getPackageFragmentRoot(res);
		Folder container = (Folder) root.getResource();
		IFileStore[] stores = buildFileStores(fileData);
		if (stores == null) {
			return;
		}
		IStatus fileStatus = checkExist(stores);
		if (fileStatus.getSeverity() != IStatus.OK) {
			displayError(fileStatus);
			return;
		}
		String errorMsg = validateImportDestinationInternal(container, stores);
		if (errorMsg != null) {
			displayError(errorMsg);
			return;
		}
		final IPath destinationPath = container.getFullPath();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		if (workspaceRoot.exists(destinationPath)) {
			IContainer workspaceRootContainer = (IContainer) workspaceRoot
					.findMember(destinationPath);

			performFileImport(stores, container, monitor);
		}
		if (errorStatus != null) {
			displayError(errorStatus);
			errorStatus = null;
		}
	}

	private void performFileImport(IFileStore[] stores, IContainer target,
			IProgressMonitor monitor) {
		IOverwriteQuery query = new IOverwriteQuery() {
			public String queryOverwrite(String pathString) {
				if (true) {// 全部覆盖
					return ALL;
				}

				final String returnCode[] = { CANCEL };
				final String msg = NLS
						.bind(IDEWorkbenchMessages.CopyFilesAndFoldersOperation_overwriteQuestion,
								pathString);
				final String[] options = { IDialogConstants.YES_LABEL,
						IDialogConstants.YES_TO_ALL_LABEL,
						IDialogConstants.NO_LABEL,
						IDialogConstants.CANCEL_LABEL };
				getShell().getDisplay().syncExec(new Runnable() {
					public void run() {
						MessageDialog dialog = new MessageDialog(
								getShell(),
								IDEWorkbenchMessages.CopyFilesAndFoldersOperation_question,
								null, msg, MessageDialog.QUESTION, options, 0) {
							protected int getShellStyle() {
								return super.getShellStyle() | SWT.SHEET;
							}
						};
						dialog.open();
						int returnVal = dialog.getReturnCode();
						String[] returnCodes = { YES, ALL, NO, CANCEL };
						returnCode[0] = returnVal == -1 ? CANCEL
								: returnCodes[returnVal];
					}
				});
				if (returnCode[0] == ALL) {
				} else if (returnCode[0] == CANCEL) {
				}
				return returnCode[0];
			}
		};

		ImportOperation op = new ImportOperation(target.getFullPath(),
				stores[0].getParent(), FileStoreStructureProvider.INSTANCE,
				query, Arrays.asList(stores));
		op.setContext(getShell());
		op.setCreateContainerStructure(false);
		op.setVirtualFolders(false);
		op.setCreateLinks(false);
		op.setRelativeVariable(null);
		try {
			op.run(monitor);
		} catch (InterruptedException e) {
			return;
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof CoreException) {
				displayError(((CoreException) e.getTargetException())
						.getStatus());
			} else {
				display(e);
				e.printStackTrace();
			}
			return;
		}
		// Special case since ImportOperation doesn't throw a CoreException on
		// failure.
		IStatus status = op.getStatus();
		if (!status.isOK()) {
			if (errorStatus == null) {
				errorStatus = new MultiStatus(PlatformUI.PLUGIN_ID,
						IStatus.ERROR, getProblemsMessage(), null);
			}
			errorStatus.merge(status);
		}
	}

	private void display(InvocationTargetException e) {
		// CoreExceptions are collected above, but unexpected runtime
		// exceptions and errors may still occur.
		IDEWorkbenchPlugin
				.getDefault()
				.getLog()
				.log(StatusUtil.newStatus(IStatus.ERROR, MessageFormat.format(
						"Exception in {0}.performCopy(): {1}", //$NON-NLS-1$
						new Object[] { getClass().getName(),
								e.getTargetException() }), null));
		displayError(NLS
				.bind(IDEWorkbenchMessages.CopyFilesAndFoldersOperation_internalError,
						e.getTargetException().getMessage()));
	}

	private String validateImportDestinationInternal(IContainer destination,
			IFileStore[] sourceStores) {
		if (!isAccessible(destination))
			return IDEWorkbenchMessages.CopyFilesAndFoldersOperation_destinationAccessError;

		if (!destination.isVirtual()) {
			IFileStore destinationStore;
			try {
				destinationStore = EFS.getStore(destination.getLocationURI());
			} catch (CoreException exception) {
				IDEWorkbenchPlugin.log(exception.getLocalizedMessage(),
						exception);
				return NLS
						.bind(IDEWorkbenchMessages.CopyFilesAndFoldersOperation_internalError,
								exception.getLocalizedMessage());
			}
			for (int i = 0; i < sourceStores.length; i++) {
				IFileStore sourceStore = sourceStores[i];
				IFileStore sourceParentStore = sourceStore.getParent();

				if (sourceStore != null) {
					if (destinationStore.equals(sourceStore)
							|| (sourceParentStore != null && destinationStore
									.equals(sourceParentStore))) {
						return NLS
								.bind(IDEWorkbenchMessages.CopyFilesAndFoldersOperation_importSameSourceAndDest,
										sourceStore.getName());
					}
					// work around bug 16202. replacement for
					// sourcePath.isPrefixOf(destinationPath)
					if (sourceStore.isParentOf(destinationStore)) {
						return IDEWorkbenchMessages.CopyFilesAndFoldersOperation_destinationDescendentError;
					}
				}
			}
		}
		return null;
	}

	private void displayError(final String message) {
		getShell().getDisplay().syncExec(new Runnable() {
			public void run() {
				MessageDialog
						.openError(getShell(), getProblemsTitle(), message);
			}
		});
	}

	private void displayError(final IStatus status) {
		getShell().getDisplay().syncExec(new Runnable() {
			public void run() {
				ErrorDialog.openError(getShell(), getProblemsTitle(), null,
						status);
			}
		});
	}

	IStatus checkExist(IFileStore[] stores) {
		MultiStatus multiStatus = new MultiStatus(PlatformUI.PLUGIN_ID,
				IStatus.OK, getProblemsMessage(), null);

		for (int i = 0; i < stores.length; i++) {
			if (stores[i].fetchInfo().exists() == false) {
				String message = NLS
						.bind(IDEWorkbenchMessages.CopyFilesAndFoldersOperation_resourceDeleted,
								stores[i].getName());
				IStatus status = new Status(IStatus.ERROR,
						PlatformUI.PLUGIN_ID, IStatus.OK, message, null);
				multiStatus.add(status);
			}
		}
		return multiStatus;
	}

	private boolean isAccessible(IResource resource) {
		switch (resource.getType()) {
		case IResource.FILE:
			return true;
		case IResource.FOLDER:
			return true;
		case IResource.PROJECT:
			return ((IProject) resource).isOpen();
		default:
			return false;
		}
	}

	protected String getProblemsMessage() {
		return IDEWorkbenchMessages.CopyFilesAndFoldersOperation_problemMessage;
	}

	private void createPackage(IJavaProject project, IProgressMonitor monitor)
			throws JavaModelException {
		fCurrJProject = project;
		IPath path = fCurrJProject.getPath().append(new Path("public"));
		fWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IResource res = fWorkspaceRoot.findMember(path);
		IPackageFragmentRoot root = fCurrJProject.getPackageFragmentRoot(res);
		IPackageFragment pack = root
				.getPackageFragment("com.yonyou.zhaoxmf.plugin");
		if (!pack.exists()) {
			String packName = pack.getElementName();
			pack = root.createPackageFragment(packName, true,
					new SubProgressMonitor(monitor, 1));
		} else {
			monitor.worked(1);
		}

	}

	private boolean createSourceFolder(IJavaProject project,
			IProgressMonitor monitor, String str)
			throws OperationCanceledException, CoreException,
			InterruptedException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask(
				NewWizardMessages.NewSourceFolderWizardPage_operation, 3);
		fProjectStatus = new StatusInfo();
		fRootStatus = new StatusInfo();
		fWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		fCurrJProject = project;
		fEntries = fCurrJProject.getRawClasspath();
		fOutputLocation = fCurrJProject.getOutputLocation();
		IPath projPath = fCurrJProject.getProject().getFullPath();
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
					if (!ResourcesPlugin.getWorkspace()
							.validateFiltered(fWorkspaceRoot.getFolder(path))
							.isOK()) {
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
				IClasspathEntry newEntry = JavaCore.newSourceEntry(path, null,
						null, null, attributes);

				Set<IClasspathEntry> modified = new HashSet<IClasspathEntry>();

				if (projectEntryIndex != -1) {
					fIsProjectAsSourceFolder = true;
					newEntries.set(projectEntryIndex, newEntry);
				} else {
					IClasspathEntry entry = JavaCore.newSourceEntry(path, null,
							null, null, attributes);
					insertAtEndOfCategory(entry, newEntries);
				}

				fNewEntries = newEntries.toArray(new IClasspathEntry[newEntries
						.size()]);
				fNewOutputLocation = fOutputLocation;

				IJavaModelStatus status = JavaConventions.validateClasspath(
						fCurrJProject, fNewEntries, fNewOutputLocation);
				if (!status.isOK()) {
					if (fOutputLocation.equals(projPath)) {
						fNewOutputLocation = projPath
								.append(PreferenceConstants
										.getPreferenceStore()
										.getString(
												PreferenceConstants.SRCBIN_BINNAME));
						IStatus status2 = JavaConventions.validateClasspath(
								fCurrJProject, fNewEntries, fNewOutputLocation);
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
		projPath = fCurrJProject.getProject().getFullPath();
		if (fOutputLocation.equals(projPath)
				&& !fNewOutputLocation.equals(projPath)) {
			if (BuildPathsBlock.hasClassfiles(fCurrJProject.getProject())) {
				if (BuildPathsBlock.getRemoveOldBinariesQuery(getShell())
						.doQuery(false, projPath)) {
					BuildPathsBlock.removeOldClassfiles(fCurrJProject
							.getProject());
				}
			}
		}

		IFolder folder = fCurrJProject.getProject().getFolder(str);
		if (!folder.exists()) {
			CoreUtility.createFolder(folder, true, true,
					new SubProgressMonitor(monitor, 1));
		}
		if (monitor.isCanceled()) {
			throw new InterruptedException();
		}

		fCurrJProject.setRawClasspath(fNewEntries, fNewOutputLocation,
				new SubProgressMonitor(monitor, 2));

		fCurrJProject.getPackageFragmentRoot(folder);

		fProjectStatus.setOK();
		return true;

	}

	private void insertAtEndOfCategory(IClasspathEntry entry,
			List<IClasspathEntry> entries) {
		int length = entries.size();
		IClasspathEntry[] elements = entries
				.toArray(new IClasspathEntry[length]);
		/*
		 * int i = 0; while (i < length && elements[i].getEntryKind() !=
		 * entry.getEntryKind()) { i++; } if (i < length) { i++; while (i <
		 * length && elements[i].getEntryKind() == entry.getEntryKind()) { i++;
		 * } entries.add(i, entry); return; }
		 */
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
		page1 = new ImportPatchWizardPage1(window);
		page2 = new ImportPatchWizardPage2();
		addPage(page1);
		addPage(page2);
		//addPage(page3);
	}

}
